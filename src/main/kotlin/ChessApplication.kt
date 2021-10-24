import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import model.ChessModel
import view.ChessBoardView

class ChessApplication: Application() {
    private lateinit var mainScene: Scene

    override fun start(stage: Stage) {
        stage.title = "Chess"

        val root = Group()
        mainScene = Scene(root)
        stage.scene = mainScene

        val boardView = ChessBoardView(600.0, 600.0)
        root.children.add(boardView)

        val model = ChessModel()
        model.setDefault()
        boardView.model = model

        val controller = ChessController(model)
        boardView.controller = controller

        stage.show()
    }
}