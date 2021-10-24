import model.ChessModel
import model.SquareLocation
import rules.Rules

class ChessController(private val model: ChessModel) {
    fun tryMove(from: SquareLocation, to: SquareLocation): Boolean {
        val piece = model.pieceAt(from)
        if (piece != null) {
            val movementRules = Rules.matchMovement(piece)
            for (rule in movementRules) {
                val movementSquares = rule.apply(model, from).filter { model.pieceAt(it) == null }
                if (movementSquares.contains(to)) {
                    model.startEditing()
                    model.setPieceAt(null, from)
                    model.setPieceAt(piece, to)

                    val extra = rule.extraMoves?.get(to)
                    if (extra != null) {
                        extra(model)
                    }

                    model.endEditing()
                    return true
                }
            }
            val attackRules = Rules.matchAttack(piece)
            for (rule in attackRules) {
                val attackSquares = rule.apply(model, from)
                if (attackSquares.contains(to)) {
                    model.startEditing()
                    model.setPieceAt(null, from)
                    model.setPieceAt(piece, to)
                    model.endEditing()
                    return true
                }
            }
        }
        return false
    }
}