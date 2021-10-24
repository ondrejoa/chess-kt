package rules

import model.ChessModel
import model.SquareLocation

class KnightRule(predicate: RulePredicate): Rule(predicate) {
    override fun apply(model: ChessModel, square: SquareLocation): Set<SquareLocation> {
        val possibilities: MutableSet<SquareLocation> = mutableSetOf(
            SquareLocation(square.x - 2, square.y - 1),
            SquareLocation(square.x - 2, square.y + 1),
            SquareLocation(square.x - 1, square.y - 2),
            SquareLocation(square.x - 1, square.y + 2),
            SquareLocation(square.x + 2, square.y - 1),
            SquareLocation(square.x + 2, square.y + 1),
            SquareLocation(square.x + 1, square.y - 2),
            SquareLocation(square.x + 1, square.y + 2),
        )
        return possibilities.filter {
            it.isValid() && predicate(model.pieceAt(it))
        }.toSet()
    }
}