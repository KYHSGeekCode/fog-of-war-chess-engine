package com.kyhsgeekcode.fogofwarchess

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChessViewModel : ViewModel() {
    private val _selectedPiece = MutableStateFlow<Piece?>(null)
    val selectedPiece = _selectedPiece as StateFlow<Piece?>

    private val _possibleMoves = MutableStateFlow<List<Move>>(emptyList())
    val possibleMoves = _possibleMoves as StateFlow<List<Move>>

    private val _currentTurn = MutableStateFlow(PieceColor.WHITE)
    val currentTurn = _currentTurn as StateFlow<PieceColor>

    private val board = Board()

    private val _boardSnapshot = MutableStateFlow(board.toSnapshot())
    val boardSnapshot = _boardSnapshot as StateFlow<BoardSnapshot>

    private val _promotingPawn = MutableStateFlow<Piece?>(null)

    // null: will not promote
    // pawn: will promote
    // piece: selected piece to promote to
    val promotingPawn = _promotingPawn as StateFlow<Piece?>

    private val _gamePhase = MutableStateFlow(GamePhase.PLAYING)
    val gamePhase = _gamePhase as StateFlow<GamePhase>

    private val _visibleCoords = MutableStateFlow<List<Coord>>(emptyList())
    val visibleCoords = _visibleCoords as StateFlow<List<Coord>>

    init {
        calculateVisibleCoords()
    }

    fun onCellClicked(coord: Coord, context: Context) {
        if (gamePhase.value != GamePhase.PLAYING) {
            return
        }
        if (_selectedPiece.value == null) {
            selectPiece(coord)
        } else {
            // check if the cell is in possible moves
            val possibleMoves = _possibleMoves.value
            val move = possibleMoves.find { it.to == coord }
            if (move != null) {
                movePiece(move, context)
            } else {
                selectPiece(coord)
            }
        }
    }

    private fun selectPiece(coord: Coord) {
        val piece = board.getPiece(coord)
        if (piece != null && piece.color == _currentTurn.value) {
            _selectedPiece.value = piece
            _possibleMoves.value = piece.getPossibleMoves(board)
        } else {
            _selectedPiece.value = null
            _possibleMoves.value = emptyList()
        }
    }

    private fun movePiece(move: Move, context: Context) {
        if (move.captureTarget == null) {
            val mp = MediaPlayer.create(context, R.raw.place)
            mp.start()
        } else {
            val mp = MediaPlayer.create(context, R.raw.drop)
            mp.start()
        }

        val winner = board.applyMove(move)
        _possibleMoves.value = emptyList()
        _boardSnapshot.value = board.toSnapshot()
        when (winner) {
            null -> {
                if (move.promotingTo != null) {
                    // show promotion options
                    _promotingPawn.value = move.who.copy(x = move.to.x, y = move.to.y)
                    return
                }
                _selectedPiece.value = null
                swapTurn()
                _possibleMoves.value = emptyList()
            }

            PieceColor.BLACK -> _gamePhase.value = GamePhase.BLACK_WIN
            PieceColor.WHITE -> _gamePhase.value = GamePhase.WHITE_WIN
        }
    }

    fun promotePiece(pieceType: PieceType) {
        val piece = _promotingPawn.value ?: return
        val winner = board.applyMove(
            Move(
                Coord(piece.x, piece.y),
                Coord(piece.x, piece.y),
                piece,
                promotingTo = pieceType
            )
        )
        _promotingPawn.value = null
        _selectedPiece.value = null
        swapTurn()
        _possibleMoves.value = emptyList()
        _boardSnapshot.value = board.toSnapshot()
        when (winner) {
            null -> {}
            PieceColor.BLACK -> _gamePhase.value = GamePhase.BLACK_WIN
            PieceColor.WHITE -> _gamePhase.value = GamePhase.WHITE_WIN
        }
    }

    private fun swapTurn() {
        _currentTurn.value = _currentTurn.value.opposite()
        calculateVisibleCoords()
    }

    private fun calculateVisibleCoords() {
        val currentColor = _currentTurn.value
        val visibleCoords = mutableListOf<Coord>()
        board.pieces.values.filter {
            it.color == currentColor
        }.forEach {
            visibleCoords.addAll(it.getPossibleMoves(board).map { move -> move.to })
            visibleCoords.add(Coord(it.x, it.y))
        }
        _visibleCoords.value = visibleCoords
    }
}