package model

import proto.ChessColor
import proto.ChessPiece
import proto.ChessPieceType

class ChessModel {
    private val squares: Array<Array<ChessPiece?>> = Array(8) {
        Array(8) { null }
    }

    var whiteRook00Castling = false
    var whiteRook70Castling = false
    var blackRook07Castling = false
    var blackRook77Castling = false

    fun setDefault() {
        squares[0][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.ROOK).build()
        squares[1][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.KNIGHT).build()
        squares[2][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.BISHOP).build()
        squares[3][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.QUEEN).build()
        squares[4][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.KING).build()
        squares[5][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.BISHOP).build()
        squares[6][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.KNIGHT).build()
        squares[7][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.ROOK).build()
        //for (x in 0..7) {
        //    squares[x][1] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.PAWN).build()
        //}
        squares[0][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.ROOK).build()
        squares[1][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.KNIGHT).build()
        squares[2][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.BISHOP).build()
        squares[3][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.QUEEN).build()
        squares[4][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.KING).build()
        squares[5][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.BISHOP).build()
        squares[6][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.KNIGHT).build()
        squares[7][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.ROOK).build()
        //for (x in 0..7) {
        //    squares[x][6] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.PAWN).build()
        //}
        whiteRook00Castling = true
        whiteRook70Castling = true
        blackRook07Castling = true
        blackRook77Castling = true
    }

    fun pieceAt(x: Int, y: Int): ChessPiece? {
        return squares[x][y]
    }

    fun pieceAt(square: SquareLocation): ChessPiece? {
        if (!square.isValid())
            return null
        return pieceAt(square.x, square.y)
    }

    fun setPieceAt(piece: ChessPiece?, x: Int, y: Int) {
        updateCastlingCondition(x, y)
        addModification(SquareLocation(x, y))
        squares[x][y] = piece
    }

    fun setPieceAt(piece: ChessPiece?, square: SquareLocation) {
        setPieceAt(piece, square.x, square.y)
    }

    private fun updateCastlingCondition(x: Int, y: Int) {
        if (y == 0) {
            when (x) {
                4 -> {
                    whiteRook00Castling = false
                    whiteRook70Castling = false
                }
                0 -> whiteRook00Castling = false
                7 -> whiteRook70Castling = false
            }
        }
        else if (y == 7) {
            when (x) {
                4 -> {
                    blackRook07Castling = false
                    blackRook77Castling = false
                }
                0 -> blackRook07Castling = false
                7 -> blackRook77Castling = false
            }
        }
    }

    private var autoBroadcastModifications = true
    private val modified: MutableSet<SquareLocation> = mutableSetOf()

    private val onModificationListeners: MutableList<(Set<SquareLocation>) -> Unit> = mutableListOf()
    fun addOnModificationListener(listener: (Set<SquareLocation>) -> Unit) {
        onModificationListeners.add(listener)
    }

    private fun addModification(square: SquareLocation) {
        if (autoBroadcastModifications) {
            val mod = setOf(square)
            for (listener in onModificationListeners) {
                listener(mod)
            }
        }
        else {
            modified.add(square)
        }
    }

    fun startEditing() {
        autoBroadcastModifications = false
    }

    fun endEditing() {
        autoBroadcastModifications = true
        for (listener in onModificationListeners) {
            listener(modified)
        }
        modified.clear()
    }
}