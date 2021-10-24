package rules

import model.ChessModel
import model.SquareLocation
import proto.ChessPiece

typealias RulePredicate = (ChessPiece?) -> Boolean

typealias RuleExtraMove = (ChessModel) -> Unit

abstract class Rule(protected val predicate: RulePredicate = { true }) {
    abstract fun apply(model: ChessModel, square: SquareLocation): Set<SquareLocation>

    var extraMoves: Map<SquareLocation, RuleExtraMove>? = null
}