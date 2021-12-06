import model.ChessModel
import model.SquareLocation
import proto.ChessColor
import proto.ChessPieceType
import rules.Rules

class ChessController(private val model: ChessModel) {
    fun tryMove(from: SquareLocation, to: SquareLocation): Boolean {
        val result = tryMoveImpl(from, to)
        if (result) {
            model.currentPlayer = if (model.currentPlayer == ChessColor.WHITE) ChessColor.BLACK else ChessColor.WHITE
        }
        return result
    }

    private fun tryMoveImpl(from: SquareLocation, to: SquareLocation): Boolean {
        val piece = model.pieceAt(from)
        if (piece != null) {
            var stepUsed = false
            model.startEditing()
            val movementRules = Rules.matchMovement(piece)
            for (rule in movementRules) {
                val movementSquares = rule.apply(model, from).filter { model.pieceAt(it) == null }
                if (movementSquares.contains(to)) {
                    model.setPieceAt(null, from)
                    model.setPieceAt(piece, to)
                    val extra = rule.extraMoves?.get(to)
                    if (extra != null) {
                        extra(model)
                    }
                    stepUsed = true
                    break
                }
            }
            val attackRules = Rules.matchAttack(piece)
            if (!stepUsed) {
                for (rule in attackRules) {
                    val attackSquares = rule.apply(model, from)
                    if (attackSquares.contains(to)) {
                        model.setPieceAt(null, from)
                        model.setPieceAt(piece, to)
                        stepUsed = true
                        break
                    }
                }
            }
            if (isKingInCheck(piece.color)) {
                model.undoEditing()
                stepUsed = false
            }
            model.endEditing()
            return stepUsed
        }
        return false
    }

    private fun isKingInCheck(color: ChessColor): Boolean {
        for (x in 0..7) {
            for (y in 0..7) {
                val piece = model.pieceAt(SquareLocation(x, y))
                if (piece != null && piece.type == ChessPieceType.KING && piece.color == color) {
                    val zoc = model.zoneOfControl(if (color == ChessColor.WHITE) ChessColor.BLACK else ChessColor.WHITE)
                    return zoc.contains(SquareLocation(x, y))
                }
            }
        }
        return false
    }

    fun trySelect(square: SquareLocation): SquareLocation? {
        val piece = model.pieceAt(square)
        if (piece != null && piece.color != model.currentPlayer)
            return null
        return square
    }
}