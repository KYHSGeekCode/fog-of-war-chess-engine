package com.kyhsgeekcode.fogofwarchess

import android.util.Log

fun Piece.checkPawnCapture(board: Board): List<Move> {
    val result = mutableListOf<Move>()
    if (color == PieceColor.WHITE) { // y decreases when march forward
        if (canCapture(board, Coord(x - 1, y - 1))) {
            result.add(
                Move(
                    Coord(x, y),
                    Coord(x - 1, y - 1),
                    this,
                    promotingTo = if (y == 1) PieceType.PAWN else null
                )
            )
        }
        if (canCapture(board, Coord(x + 1, y - 1))) {
            result.add(
                Move(
                    Coord(x, y),
                    Coord(x + 1, y - 1),
                    this,
                    promotingTo = if (y == 1) PieceType.PAWN else null
                )
            )
        }
    } else {
        if (canCapture(board, Coord(x - 1, y + 1))) {
            result.add(
                Move(
                    Coord(x, y),
                    Coord(x - 1, y + 1),
                    this,
                    promotingTo = if (y == 6) PieceType.PAWN else null
                )
            )
        }
        if (canCapture(board, Coord(x + 1, y + 1))) {
            result.add(
                Move(
                    Coord(x, y),
                    Coord(x + 1, y + 1),
                    this,
                    promotingTo = if (y == 6) PieceType.PAWN else null
                )
            )
        }
    }
    return result
}

fun Piece.appendIfNoPiece(
    board: Board, result: MutableList<Move>, coord: Coord, isPromotion: Boolean = false
) {
    if (!coord.isValid()) {
        return
    }

    val piece = board.getPiece(coord)
    if (piece == null) {
        result.add(
            Move(
                Coord(x, y),
                coord,
                this,
                promotingTo = if (isPromotion) PieceType.PAWN else null
            )
        )
    }
}

fun Piece.appendIfNoPiece2(
    board: Board,
    result: MutableList<Move>,
    coord1: Coord,
    coord2: Coord,
    isPromotion: Boolean = false
) {
    if (!coord1.isValid()) {
        return
    }
    if (!coord2.isValid()) {
        return
    }

    val piece1 = board.getPiece(coord1)
    val piece2 = board.getPiece(coord2)
    if (piece1 == null && piece2 == null) {
        result.add(
            Move(
                Coord(x, y),
                coord2,
                this,
                promotingTo = if (isPromotion) PieceType.PAWN else null
            )
        )
    }
}


fun Piece.getPawnPossibleMoves(board: Board): List<Move> {
    val result = mutableListOf<Move>()
    Log.d("Pawn", "getPawnPossibleMoves for ${this.coordCode}")
    if (color == PieceColor.WHITE) { // y decreases when march forward
        if (y == 6) { // beginning
            // can move 1 or 2
            appendIfNoPiece2(board, result, Coord(x, y - 1), Coord(x, y - 2))
            appendIfNoPiece(board, result, Coord(x, y - 1))
        } else if (y > 0) {
            // can move 1
            appendIfNoPiece(board, result, Coord(x, y - 1), isPromotion = y == 1)
        } else {
            // should have been promoted
        }
    } else {
        if (y == 1) {
            // can move 1 or 2
            appendIfNoPiece2(board, result, Coord(x, y + 1), Coord(x, y + 2))
            appendIfNoPiece(board, result, Coord(x, y + 1))
        } else if (y < 7) {
            // can move 1
            appendIfNoPiece(board, result, Coord(x, y + 1), isPromotion = y == 6)
        } else {
            // should have been promoted
        }
    }
    result.addAll(checkPawnCapture(board))
    result.addAll(checkEnPassant(board))
    return result
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
    // should have moved 2 Coords
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
        Move(
            Coord(x, y),
            Coord(lastMove.to.x, lastMove.to.y - distance / 2),
            this,
            promotingTo = null
        )
    )
}