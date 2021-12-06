import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.FileChooser
import javafx.stage.Stage
import model.ChessModel
import proto.ChessColor
import proto.SaveGame
import view.ChessBoardView
import java.io.File

class ChessApplication: Application(), ChessModel.OnCurrentPlayerChangedListener {
    private lateinit var mainScene: Scene
    private lateinit var currentPlayerText: Text

    override fun start(stage: Stage) {
        stage.title = "Chess"

        val root = Group()
        mainScene = Scene(root)
        stage.scene = mainScene

        val vbox = VBox()
        vbox.spacing = 40.0
        root.children.add(vbox)

        val boardView = ChessBoardView(600.0, 600.0)
        vbox.children.add(boardView)

        val model = ChessModel()
        model.setDefault()
        boardView.model = model

        val controller = ChessController(model)
        boardView.controller = controller

        val hBox = HBox()
        vbox.children.add(hBox)
        hBox.spacing = 10.0

        val resetButton = Button("Reset")
        hBox.children.add(resetButton)

        val saveButton = Button("Save")
        hBox.children.add(saveButton)

        val loadButton = Button("Load")
        hBox.children.add(loadButton)

        resetButton.onMouseClicked = EventHandler {
            model.reset()
        }

        saveButton.onMouseClicked = EventHandler {
            val fileChooser = FileChooser()
            val filter = FileChooser.ExtensionFilter("Chess games", "*.chess")
            fileChooser.extensionFilters.add(filter)
            fileChooser.title = "Save game"
            var file = fileChooser.showSaveDialog(stage)
            if (file != null) {
                if (file.extension != "chess")
                    file = File(file.parent, file.name + ".chess")
                file.writeBytes(model.createSaveGame().toByteArray())
            }
        }

        loadButton.onMouseClicked = EventHandler {
            val fileChooser = FileChooser()
            val filter = FileChooser.ExtensionFilter("Chess games", "*.chess")
            fileChooser.extensionFilters.add(filter)
            fileChooser.title = "Load game"
            val file = fileChooser.showOpenDialog(stage)
            if (file != null) {
                val bytes = file.readBytes()
                val saveGame = SaveGame.parseFrom(bytes)
                model.loadSaveGame(saveGame)
            }
        }

        currentPlayerText = Text("Current player: ${model.currentPlayer}")
        hBox.children.add(currentPlayerText)
        model.addOnCurrentPlayerChangedListener(this)

        val helpText = Text("""
            Right click: select
            Left click: move or attack
            
            The game doesn't notify you when your king is in checkmate but it won't let you move (reset the game).
            Promotion isn't implemented.
        """.trimIndent())
        vbox.children.add(helpText)

        stage.show()
    }

    override fun onCurrentPlayerChanged(player: ChessColor) {
        currentPlayerText.text = "Current player: $player"
    }
}