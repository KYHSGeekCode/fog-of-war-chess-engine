package com.kyhsgeekcode.fogofwarchess

import android.util.Log

fun Piece.checkPawnCapture(board: Board): List<Move> {
    val result = mutableListOf<Move>()
    if (color == PieceColor.WHITE) { // y decreases when march forward
        if (canCapture(board, Cell(x - 1, y - 1))) {
            result.add(Move(Cell(x, y), Cell(x - 1, y - 1), this, true))
        }
        if (canCapture(board, Cell(x + 1, y - 1))) {
            result.add(Move(Cell(x, y), Cell(x + 1, y - 1), this, true))
        }
    } else {
        if (canCapture(board, Cell(x - 1, y + 1))) {
            result.add(Move(Cell(x, y), Cell(x - 1, y + 1), this, true))
        }
        if (canCapture(board, Cell(x + 1, y + 1))) {
            result.add(Move(Cell(x, y), Cell(x + 1, y + 1), this, true))
        }
    }
    return result
}

fun Piece.getPawnPossibleMoves(board: Board): List<Move> {
    val result = mutableListOf<Move>()
    Log.d("Pawn", "getPawnPossibleMoves for ${this.coordCode}")
    if (color == PieceColor.WHITE) { // y decreases when march forward
        if (y == 6) { // beginning
            // can move 1 or 2
            result.add(Move(Cell(x, y), Cell(x, y - 2), this))
            result.add(Move(Cell(x, y), Cell(x, y - 1), this))
        } else if (y > 0) {
            // can move 1
            result.add(Move(Cell(x, y), Cell(x, y - 1), this, isPromotion = y == 1))
        } else {
            // should have been promoted
        }
    } else {
        if (y == 1) {
            // can move 1 or 2
            result.add(Move(Cell(x, y), Cell(x, y + 2), this))
            result.add(Move(Cell(x, y), Cell(x, y + 1), this))
        } else if (y < 7) {
            // can move 1
            result.add(Move(Cell(x, y), Cell(x, y + 1), this))
        } else {
            // should have been promoted
        }
    }
    result.addAll(checkPawnCapture(board))
    result.addAll(checkEnPassant(board))
    return result
}

private fun Piece.canPawnMoveForward(board: Board, cell: Cell): Boolean {
    if (color == PieceColor.WHITE) { // y decreases when march forward
        if (cell.x == x && cell.y == y - 1) {
            return true
        }
        if (cell.x == x && cell.y == y - 2 && !moved) {
            return true
        }
        return false
    } else {
        if (cell.x == x && cell.y == y + 1) {
            return true
        }
        if (cell.x == x && cell.y == y + 2 && !moved) {
            return true
        }
        return false
    }
}

private fun Piece.checkEnPassant(board: Board): List<Move> {
    val history = board.history
    if (history.isEmpty()) {
        return emptyList()
    }
    val lastMove = history.last()
    if (lastMove.who.type != PieceType.PAWN) { // should be pawn
        return emptyList()
    }
    // should have moved 2 cells
    val distance = lastMove.to.y - lastMove.from.y
    if (distance != 2 && distance != -2) {
        return emptyList()
    }
    // should be in the next column
    if (lastMove.to.x != x - 1 && lastMove.to.x != x + 1) {
        return emptyList()
    }
    // should be in the same row
    if (lastMove.to.y != y) {
        return emptyList()
    }
    return listOf(
        Move(Cell(x, y), Cell(lastMove.to.x, lastMove.to.y + distance / 2), this, false)
    )
}