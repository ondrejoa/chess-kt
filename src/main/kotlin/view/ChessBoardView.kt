package view

import ChessController
import PieceLoader
import javafx.event.EventHandler
import javafx.geometry.VPos
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import model.ChessModel
import model.SquareLocation
import proto.ChessPiece
import rules.Rules

class ChessBoardView(w: Double, h: Double): Canvas(w + OFFSET_X, h + OFFSET_Y) {
    companion object {
        val PLAYER_BLACK_COLOR: Color = Color(0.0, 0.0, 0.0, 1.0)
        val PLAYER_WHITE_COLOR: Color = Color(0.9, 0.9, 0.9, 1.0)
        const val OFFSET_X: Double = 40.0
        const val OFFSET_Y: Double = 40.0
    }

    private val cellSizeX: Double = w / 8.0
    private val cellSizeY: Double = h / 8.0

    private val loader = PieceLoader()

    var model: ChessModel? = null
    set(value) {
        if (field != null) {
            // TODO: Unsubscribe
        }
        for (x in 0..7) {
            for (y in 0..7) {
                resetSquare(x, y)
                val piece = value?.pieceAt(x, y)
                if (piece != null) {
                    drawPiece(piece, x, y)
                }
            }
        }
        value?.addOnModificationListener {
            redrawSquares(it)
        }
        field = value
    }

    var controller: ChessController? = null

    private data class HighlightColors(val light: Color, val dark: Color)

    private var highlighted: MutableSet<SquareLocation> = mutableSetOf()

    private var selected: SquareLocation? = null
    set(value) {
        if (value == field)
            return
        if (value == null) {
            for (square in highlighted)
                redrawSquare(square.x, square.y)
            highlighted.clear()
        }
        else if (value != field) {
            for (square in highlighted)
                redrawSquare(square.x, square.y)
            highlighted.clear()
            highlighted.add(value)
            redrawSquare(value.x, value.y, HighlightColors(Color.LIGHTBLUE, Color.BLUE))

            val piece = model?.pieceAt(value)
            if (piece != null) {
                val movementRules = Rules.matchMovement(piece)
                val m = model
                for (rule in movementRules) {
                    if (m != null) {
                        val movementSquares = rule.apply(m, value)
                        highlighted.addAll(movementSquares)
                        redrawSquares(movementSquares, HighlightColors(Color.LIGHTGREEN, Color.GREEN))
                    }
                }
                val attackRules = Rules.matchAttack(piece)
                for (rule in attackRules) {
                    if (m != null) {
                        val attackSquares = rule.apply(m, value)
                        highlighted.addAll(attackSquares)
                        redrawSquares(attackSquares, HighlightColors(Color.RED, Color.DARKRED))
                    }
                }
            }
        }
        field = value
    }

    init {
        graphicsContext2D.textAlign = TextAlignment.CENTER
        graphicsContext2D.textBaseline = VPos.CENTER
        graphicsContext2D.fill = Color.BLACK
        graphicsContext2D.strokeLine(OFFSET_X, OFFSET_Y, width, OFFSET_Y)
        graphicsContext2D.strokeLine(OFFSET_X, OFFSET_Y, OFFSET_X, height)
        for (x in 0..7) {
            graphicsContext2D.fill = Color.BLACK
            graphicsContext2D.fillText(('A' + x).toString(), (x + 0.5) * cellSizeX + OFFSET_X, 10.0)
            for (y in 0..7) {
                if (x == 0) {
                    graphicsContext2D.fill = Color.BLACK
                    graphicsContext2D.fillText(('1' + y).toString(), 10.0, (y + 0.5) * cellSizeY + OFFSET_Y)
                }
                resetSquare(x, y)
            }
        }

        onMouseClicked = EventHandler {
            if (it.x < OFFSET_X || it.y < OFFSET_Y) {
                selected = null
                return@EventHandler
            }
            val x: Int = ((it.x - OFFSET_X) / cellSizeX).toInt()
            val y: Int = ((it.y - OFFSET_Y) / cellSizeY).toInt()
            when (it.button) {
                MouseButton.SECONDARY -> selected = SquareLocation(x, y)
                MouseButton.PRIMARY -> {
                    val s = selected
                    selected = null
                    if (s != null) {
                        controller?.tryMove(s, SquareLocation(x, y))
                    }
                }
            }
        }
    }

    private fun redrawSquares(squares: Set<SquareLocation>, highlight: HighlightColors? = null) {
        for (square in squares)
            redrawSquare(square.x, square.y, highlight)
    }

    private fun redrawSquare(x: Int, y: Int, highlight: HighlightColors? = null) {
        resetSquare(x, y, highlight)
        val piece = model?.pieceAt(x, y)
        if (piece != null) {
            drawPiece(piece, x, y)
        }
    }

    private fun drawPiece(piece: ChessPiece, x: Int, y: Int) {
        val image = loader.getImage(piece)
        graphicsContext2D.drawImage(image, x * cellSizeX + OFFSET_X, y * cellSizeY + OFFSET_Y, cellSizeX, cellSizeY)
    }

    private fun resetSquare(x: Int, y: Int, highlight: HighlightColors? = null) {
        graphicsContext2D.fill = if ((x + y) % 2 == 0)
            highlight?.dark ?: PLAYER_BLACK_COLOR
        else
            highlight?.light ?: PLAYER_WHITE_COLOR
        graphicsContext2D.fillRect(x * cellSizeX + OFFSET_X, y * cellSizeY + OFFSET_Y, cellSizeX, cellSizeY)
    }

}