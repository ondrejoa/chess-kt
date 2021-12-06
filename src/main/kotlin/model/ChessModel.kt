package model

import proto.*
import rules.Rules

class ChessModel {
    interface OnSquaresModifiedListener {
        fun onSquaresModified(squares: Set<SquareLocation>)
    }

    interface OnCurrentPlayerChangedListener {
        fun onCurrentPlayerChanged(player: ChessColor)
    }

    private val squares: Array<Array<ChessPiece?>> = Array(8) {
        Array(8) { null }
    }

    var whiteRook00Castling = false
    var whiteRook70Castling = false
    var blackRook07Castling = false
    var blackRook77Castling = false

    var currentPlayer: ChessColor = ChessColor.BLACK
        set(value) {
            field = value
            for (listener in onCurrentPlayerChangedListeners)
                listener.onCurrentPlayerChanged(currentPlayer)
        }

    private val onSquaresModifiedListeners: MutableList<OnSquaresModifiedListener> = mutableListOf()
    fun addOnSquaresModifiedListener(listener: OnSquaresModifiedListener) {
        onSquaresModifiedListeners.add(listener)
    }

    private val onCurrentPlayerChangedListeners: MutableList<OnCurrentPlayerChangedListener> = mutableListOf()
    fun addOnCurrentPlayerChangedListener(listener: OnCurrentPlayerChangedListener) {
        onCurrentPlayerChangedListeners.add(listener)
    }

    private var autoBroadcastModifications = true
    private val modified: MutableSet<SquareLocation> = mutableSetOf()

    private data class UndoMove(
        val square: SquareLocation,
        val piece: ChessPiece?,
        val whiteRook00Castling: Boolean,
        val whiteRook70Castling: Boolean,
        val blackRook07Castling: Boolean,
        val blackRook77Castling: Boolean,
    )

    private val undoStack: MutableList<UndoMove> = mutableListOf()

    fun reset() {
        currentPlayer = ChessColor.BLACK
        whiteRook00Castling = false
        whiteRook70Castling = false
        blackRook07Castling = false
        blackRook77Castling = false
        val modified: MutableSet<SquareLocation> = mutableSetOf()
        for (x in 0..7) {
            for (y in 0..7) {
                squares[x][y] = null
                modified.add(SquareLocation(x, y))
            }
        }
        setDefault()
        for (listener in onSquaresModifiedListeners) {
            listener.onSquaresModified(modified)
        }
    }

    fun setDefault() {
        squares[0][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.ROOK).build()
        squares[1][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.KNIGHT).build()
        squares[2][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.BISHOP).build()
        squares[3][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.QUEEN).build()
        squares[4][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.KING).build()
        squares[5][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.BISHOP).build()
        squares[6][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.KNIGHT).build()
        squares[7][0] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.ROOK).build()
        for (x in 0..7) {
            squares[x][1] = ChessPiece.newBuilder().setColor(ChessColor.WHITE).setType(ChessPieceType.PAWN).build()
        }
        squares[0][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.ROOK).build()
        squares[1][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.KNIGHT).build()
        squares[2][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.BISHOP).build()
        squares[3][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.QUEEN).build()
        squares[4][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.KING).build()
        squares[5][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.BISHOP).build()
        squares[6][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.KNIGHT).build()
        squares[7][7] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.ROOK).build()
        for (x in 0..7) {
            squares[x][6] = ChessPiece.newBuilder().setColor(ChessColor.BLACK).setType(ChessPieceType.PAWN).build()
        }
        whiteRook00Castling = true
        whiteRook70Castling = true
        blackRook07Castling = true
        blackRook77Castling = true

        updateZoneOfControl()
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
        if (!autoBroadcastModifications) pushToUndoStack(pieceAt(x, y), x, y)
        updateCastlingCondition(x, y)
        squares[x][y] = piece
        updateZoneOfControl()
        addModification(SquareLocation(x, y))
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
        } else if (y == 7) {
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

    private fun pushToUndoStack(piece: ChessPiece?, x: Int, y: Int) {
        undoStack.add(
            UndoMove(
                square = SquareLocation(x, y),
                piece = piece,
                whiteRook00Castling = whiteRook00Castling,
                whiteRook70Castling = whiteRook70Castling,
                blackRook07Castling = blackRook07Castling,
                blackRook77Castling = blackRook77Castling,
            )
        )
    }

    private val zoc: Map<ChessColor, MutableSet<SquareLocation>> = mapOf(
        ChessColor.BLACK to mutableSetOf(),
        ChessColor.WHITE to mutableSetOf(),
    )


    private fun updateZoneOfControl() {
        zoc[ChessColor.BLACK]?.clear()
        zoc[ChessColor.WHITE]?.clear()
        for (x in 0..7) {
            for (y in 0..7) {
                val piece = pieceAt(x, y)
                if (piece != null) {
                    val zocRule = Rules.matchZoc(piece)
                    for (rule in zocRule) {
                        val zocSquares = rule.apply(this, SquareLocation(x, y))
                        zoc[piece.color]?.addAll(zocSquares)
                    }
                }
            }
        }
    }

    fun zoneOfControl(color: ChessColor): Set<SquareLocation> {
        return zoc[color] ?: emptySet()
    }


    private fun addModification(square: SquareLocation) {
        if (autoBroadcastModifications) {
            val mod = setOf(square)
            for (listener in onSquaresModifiedListeners) {
                listener.onSquaresModified(mod)
            }
        } else {
            modified.add(square)
        }
    }

    fun startEditing() {
        autoBroadcastModifications = false
    }

    fun endEditing() {
        autoBroadcastModifications = true
        for (listener in onSquaresModifiedListeners) {
            listener.onSquaresModified(modified)
        }
        undoStack.clear()
        modified.clear()
    }

    fun undoEditing() {
        for (undo in undoStack.asReversed()) {
            squares[undo.square.x][undo.square.y] = undo.piece
            whiteRook00Castling = undo.whiteRook00Castling
            whiteRook70Castling = undo.whiteRook70Castling
            blackRook07Castling = undo.blackRook07Castling
            blackRook77Castling = undo.blackRook77Castling
        }
        undoStack.clear()
    }

    fun createSaveGame(): SaveGame {
        val pieces: MutableList<PieceWithLocation> = mutableListOf()
        for (x in 0..7) {
            for (y in 0..7) {
                val piece = pieceAt(x, y)
                if (piece != null) {
                    pieces.add(
                        PieceWithLocation.newBuilder()
                            .setPiece(piece)
                            .setLocation(
                                Location.newBuilder()
                                    .setX(x)
                                    .setY(y)
                            )
                            .build()
                    )
                }
            }
        }
        return SaveGame.newBuilder()
            .setCurrentPlayer(currentPlayer)
            .addAllPieces(pieces)
            .setW00Castling(whiteRook00Castling)
            .setW70Castling(whiteRook70Castling)
            .setB07Castling(blackRook07Castling)
            .setB77Castling(blackRook77Castling)
            .build()
    }

    fun loadSaveGame(saveGame: SaveGame) {
        currentPlayer = saveGame.currentPlayer
        whiteRook00Castling = saveGame.w00Castling
        whiteRook70Castling = saveGame.w70Castling
        blackRook07Castling = saveGame.b07Castling
        blackRook77Castling = saveGame.b77Castling
        val modified: MutableSet<SquareLocation> = mutableSetOf()
        for (x in 0..7) {
            for (y in 0..7) {
                squares[x][y] = null
                modified.add(SquareLocation(x, y))
            }
        }
        for (piece in saveGame.piecesList) {
            squares[piece.location.x][piece.location.y] = piece.piece;
        }
        for (listener in onSquaresModifiedListeners) {
            listener.onSquaresModified(modified)
        }
    }

}