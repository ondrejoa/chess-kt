package rules

import proto.ChessColor
import proto.ChessPiece
import proto.ChessPieceType

object Rules {
    fun matchMovement(piece: ChessPiece): Set<Rule> {
        val kingPredicate: KingAdditionalPredicate = { p, m, s ->
            p != null && m != null && !m.zoneOfControl(
                if (p.color == ChessColor.BLACK) ChessColor.WHITE else ChessColor.BLACK
            ).contains(s)
        }
        return when (piece.type) {
            ChessPieceType.KING -> setOf(KingRule({ it == null }, kingPredicate), KingCastlingRule())
            ChessPieceType.QUEEN -> setOf(QueenRule { it == null })
            ChessPieceType.ROOK -> setOf(RookRule { it == null })
            ChessPieceType.BISHOP -> setOf(BishopRule { it == null })
            ChessPieceType.KNIGHT -> setOf(KnightRule { it == null })
            ChessPieceType.PAWN -> setOf(
                if (piece.color == ChessColor.WHITE)
                    WhitePawnMovementRule()
                else
                    BlackPawnMovementRule()
            )
            else -> emptySet()
        }
    }

    fun matchAttack(piece: ChessPiece): Set<Rule> {
        val attackPredicate = { p: ChessPiece? -> p != null && p.color != piece.color && p.type != ChessPieceType.KING }
        val kingPredicate: KingAdditionalPredicate = { p, m, s ->
            p != null && m != null && !m.zoneOfControl(
                if (p.color == ChessColor.BLACK) ChessColor.WHITE else ChessColor.BLACK
            ).contains(s)
        }
        return when (piece.type) {
            ChessPieceType.KING -> setOf(KingRule(attackPredicate, kingPredicate))
            ChessPieceType.QUEEN -> setOf(QueenRule(attackPredicate))
            ChessPieceType.ROOK -> setOf(RookRule(attackPredicate))
            ChessPieceType.BISHOP -> setOf(BishopRule(attackPredicate))
            ChessPieceType.KNIGHT -> setOf(KnightRule(attackPredicate))
            ChessPieceType.PAWN -> setOf(if (piece.color == ChessColor.WHITE)
                WhitePawnAttackRule { it != null }
            else
                BlackPawnAttackRule { it != null }
            )
            else -> emptySet()
        }
    }

    fun matchZoc(piece: ChessPiece): Set<Rule> {
        return when (piece.type) {
            ChessPieceType.KING -> setOf(KingRule({ true }, { _, _, _ -> true }))
            ChessPieceType.QUEEN -> setOf(QueenRule { true })
            ChessPieceType.ROOK -> setOf(RookRule { true })
            ChessPieceType.BISHOP -> setOf(BishopRule { true })
            ChessPieceType.KNIGHT -> setOf(KnightRule { true })
            ChessPieceType.PAWN -> setOf(if (piece.color == ChessColor.WHITE)
                WhitePawnAttackRule { true }
            else
                BlackPawnAttackRule { true }
            )
            else -> emptySet()
        }
    }

}