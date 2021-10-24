package rules

import proto.Chess
import proto.ChessColor
import proto.ChessPiece
import proto.ChessPieceType

object Rules {
    fun matchMovement(piece: ChessPiece): Set<Rule> {
        return when (piece.type) {
            ChessPieceType.KING -> setOf(KingRule { it == null }, KingCastlingRule())
            ChessPieceType.QUEEN -> setOf(QueenRule { it == null })
            ChessPieceType.ROOK -> setOf(RookRule { it == null })
            ChessPieceType.BISHOP -> setOf(BishopRule { it == null })
            ChessPieceType.KNIGHT -> setOf(KnightRule { it == null })
            ChessPieceType.PAWN -> setOf(if (piece.color == ChessColor.WHITE)
                WhitePawnMovementRule()
            else
                BlackPawnMovementRule()
            )
            else -> emptySet()
        }
    }
    fun matchAttack(piece: ChessPiece): Set<Rule> {
        val attackPredicate = { p: ChessPiece? -> p != null && p.color != piece.color && p.type != ChessPieceType.KING}
        return when (piece.type) {
            ChessPieceType.KING -> setOf(KingRule(attackPredicate))
            ChessPieceType.QUEEN -> setOf(QueenRule(attackPredicate))
            ChessPieceType.ROOK -> setOf(RookRule(attackPredicate))
            ChessPieceType.BISHOP -> setOf(BishopRule(attackPredicate))
            ChessPieceType.KNIGHT -> setOf(KnightRule(attackPredicate))
            ChessPieceType.PAWN -> if (piece.color == ChessColor.WHITE)
                emptySet()
            else
                emptySet()
            else -> emptySet()
        }
    }

    fun matchZoc(piece: ChessPiece): Set<Rule> {
        return emptySet()
    }

}