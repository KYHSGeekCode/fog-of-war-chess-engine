package com.kyhsgeekcode.fogofwarchess

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


    fun onCellClicked(cell: Cell) {
        if (_selectedPiece.value == null) {
            selectPiece(cell)
        } else {
            // check if the cell is in possible moves
            val possibleMoves = _possibleMoves.value
            val move = possibleMoves.find { it.to == cell }
            if (move != null) {
                movePiece(move)
            } else {
                selectPiece(cell)
            }
        }
    }

    private fun selectPiece(cell: Cell) {
        val piece = board.pieces[cell.x to cell.y]
        if (piece != null && piece.color == _currentTurn.value) {
            _selectedPiece.value = piece
            _possibleMoves.value = piece.getPossibleMoves(board)
        } else {
            _selectedPiece.value = null
            _possibleMoves.value = emptyList()
        }
    }

    private fun movePiece(move: Move) {
        move.apply(board)
        _selectedPiece.value = null
        _currentTurn.value = _currentTurn.value.opposite()
        _possibleMoves.value = emptyList()
        _boardSnapshot.value = board.toSnapshot()
    }
}