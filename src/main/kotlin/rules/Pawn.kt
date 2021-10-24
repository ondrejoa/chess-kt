package rules

import model.ChessModel
import model.SquareLocation

class WhitePawnMovementRule: Rule() {
    override fun apply(model: ChessModel, square: SquareLocation): Set<SquareLocation> {
        val s = SquareLocation(square.x, square.y + 1)
        return if (s.isValid() && model.pieceAt(s) == null) {
            setOf(s)
        }
        else {
            emptySet()
        }
    }

}

class BlackPawnMovementRule: Rule() {
    override fun apply(model: ChessModel, square: SquareLocation): Set<SquareLocation> {
        val s = SquareLocation(square.x, square.y - 1)
        return if (s.isValid()) {
            setOf(s)
        }
        else {
            emptySet()
        }
    }
}