package rules

import model.ChessModel
import model.SquareLocation

class WhitePawnMovementRule: Rule() {
    override fun apply(model: ChessModel, square: SquareLocation): Set<SquareLocation> {
        val s = SquareLocation(square.x, square.y + 1)
        return if (s.isValid() && model.pieceAt(s) == null) {
            val s2 = SquareLocation(square.x, square.y + 2)
            if (square.y == 1 && s2.isValid() && model.pieceAt(s2) == null)
                setOf(s, s2)
            else
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
        return if (s.isValid() && model.pieceAt(s) == null) {
            val s2 = SquareLocation(square.x, square.y - 2)
            if (square.y == 6 && s2.isValid() && model.pieceAt(s2) == null)
                setOf(s, s2)
            else
                setOf(s)
        }
        else {
            emptySet()
        }
    }
}

class WhitePawnAttackRule(predicate: RulePredicate): Rule(predicate) {
    override fun apply(model: ChessModel, square: SquareLocation): Set<SquareLocation> {
        val squares = setOf(
            SquareLocation(square.x + 1, square.y + 1),
            SquareLocation(square.x - 1, square.y + 1),
        )
        return squares.filter {
            it.isValid() && predicate(model.pieceAt(it))
        }.toSet()
    }
}

class BlackPawnAttackRule(predicate: RulePredicate): Rule(predicate) {
    override fun apply(model: ChessModel, square: SquareLocation): Set<SquareLocation> {
        val squares = setOf(
            SquareLocation(square.x + 1, square.y - 1),
            SquareLocation(square.x - 1, square.y - 1),
        )
        return squares.filter {
            it.isValid() && predicate(model.pieceAt(it))
        }.toSet()
    }

}