package rules

import model.ChessModel
import model.SquareLocation
import proto.ChessColor

class KingRule(predicate: RulePredicate): Rule(predicate) {
    override fun apply(model: ChessModel, square: SquareLocation): Set<SquareLocation> {
        val possibilities: MutableSet<SquareLocation> = mutableSetOf()
        for (x in -1..1)
            for (y in -1..1)
                possibilities.add(SquareLocation(square.x + x, square.y + y))
        return possibilities.filter {
            it.isValid() && predicate(model.pieceAt(it))
        }.toSet()
    }
}

class KingCastlingRule: Rule() {
    override fun apply(model: ChessModel, square: SquareLocation): Set<SquareLocation> {
        val possibilities: MutableSet<SquareLocation> = mutableSetOf()
        val piece = model.pieceAt(square)
        if (piece != null ) {
            val leftOk = if (piece.color == ChessColor.WHITE) model.whiteRook00Castling else model.blackRook07Castling
            val rightOk = if (piece.color == ChessColor.WHITE) model.whiteRook70Castling else model.blackRook77Castling
            val rangeEmpty = fun(x1: Int, x2: Int): Boolean {
                for (x in x1..x2) {
                    if (model.pieceAt(x, square.y) != null)
                        return false
                }
                return true
            }
            val moves: MutableMap<SquareLocation, RuleExtraMove> = mutableMapOf()
            if (leftOk && rangeEmpty(1, 3)) {
                val dest = SquareLocation(square.x - 2, square.y)
                possibilities.add(dest)
                extraMoves = moves
                moves[dest] = {
                    val rook = it.pieceAt(0, square.y)
                    if (rook != null) {
                        it.setPieceAt(null, 0, square.y)
                        it.setPieceAt(rook, square.x - 1, square.y)
                    }
                }
            }
            if (rightOk && rangeEmpty(5, 6)) {
                val dest = SquareLocation(square.x + 2, square.y)
                possibilities.add(dest)
                extraMoves = moves
                moves[dest] = {
                    val rook = it.pieceAt(7, square.y)
                    if (rook != null) {
                        it.setPieceAt(null, 7, square.y)
                        it.setPieceAt(rook, square.x + 1, square.y)
                    }
                }
            }
        }
        return possibilities
    }
}