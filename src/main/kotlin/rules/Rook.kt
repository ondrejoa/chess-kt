package rules

import model.ChessModel
import model.SquareLocation

class RookRule(predicate: RulePredicate): Rule(predicate) {
    override fun apply(model: ChessModel, square: SquareLocation): Set<SquareLocation> {
        val possibilities: MutableSet<SquareLocation> = mutableSetOf()
        val addLine: ((Int) -> SquareLocation) -> Unit = {
            for (i in 1..7) {
                val s = it(i)
                possibilities.add(s)
                if (model.pieceAt(s) != null) {
                    break
                }
            }
        }

        addLine {
            SquareLocation(square.x + it, square.y)
        }
        addLine {
            SquareLocation(square.x - it, square.y)
        }
        addLine {
            SquareLocation(square.x, square.y + it)
        }
        addLine {
            SquareLocation(square.x, square.y - it)
        }

        return possibilities.filter {
            it.isValid() && predicate(model.pieceAt(it))
        }.toSet()
    }
}