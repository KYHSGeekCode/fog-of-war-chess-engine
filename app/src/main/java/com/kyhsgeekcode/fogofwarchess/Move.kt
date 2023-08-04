package com.kyhsgeekcode.fogofwarchess

import android.util.Log

data class Move(
    val boardSnapshot: BoardSnapshot,
    val from: Coord,
    val to: Coord,
    val who: Piece,
    val castlingRook: Piece? = null,
    val promotingTo: PieceType? = null,
    val captureTarget: Piece? = null
) {
    fun getPgn(): String {
        val sb = StringBuilder()
        // make from
        // Qxg4
        // e4
        // exd5
        // e8=Q
        // e8=Q+
        // e8=Q#
        // e8=Q++
        if (who.type == PieceType.PAWN) {
            // no need to specify the piece type
            // if it is a capture, specify the file
            if (captureTarget != null) {
                if (checkPawnInColumn(who, boardSnapshot)) {
                    sb.append(from.coordCode)
                } else {
                    sb.append(from.coordCode[0])
                }
                sb.append('x')
                sb.append(to.coordCode)
            } else {
                sb.append(to.coordCode)
            }
            if (promotingTo != null) {
                sb.append('=')
                sb.append(promotingTo.shortName)
            }
        } else if (castlingRook != null) {
            if (castlingRook.x == 0) {
                sb.append("O-O-O")
            } else {
                sb.append("O-O")
            }
        } else {
            sb.append(who.type.shortName)
            // TODO: unambiguous move
            // pile : a to h
            // rank : 1 to 8
            // check if there are other pieces that can move to the same place
            // if there are, specify the pile
            // if there are more than one, specify the rank
            // if there are more than one, specify both
            val ambiguousPieces = boardSnapshot.pieces.values.filter { piece ->
                piece.type == who.type && piece.color == who.color && piece != who
            }.filter {
                val moves = it.getPossibleMovesWithoutPawn(boardSnapshot)
                moves.any { move -> move.to == to }
            }
            if (ambiguousPieces.isNotEmpty()) {
                // check if pile is ambiguous also
                val ambiguousPiles = ambiguousPieces.filter { piece ->
                    piece.x == who.x
                }
                if (ambiguousPiles.isEmpty()) {
                    // pile only is ok
                    sb.append(from.coordCode[0])
                } else {
                    // check if rank is ambiguous also
                    val ambiguousRanks = ambiguousPiles.filter { piece ->
                        piece.y == who.y
                    }
                    if (ambiguousRanks.isEmpty()) {
                        // rank only is ok
                        sb.append(from.coordCode[1])
                    } else {
                        // both are ambiguous
                        sb.append(from.coordCode)
                    }
                }
            }
            if (captureTarget != null) {
                sb.append('x')
            }
            sb.append(to.coordCode)
            if (captureTarget?.type == PieceType.KING) {
                sb.append("#")
            }
        }
        return sb.toString()
    }

    private fun checkPawnInColumn(pawn: Piece, board: BoardSnapshot): Boolean {
        if (pawn.type != PieceType.PAWN) {
            return false
        }
        for (i in 0..7) {
            val piece = board.getPiece(pawn.x, i)
            if (piece != null && piece.type == PieceType.PAWN && pawn != piece && piece.color == pawn.color) {
                Log.d("Move", "Pawn in column: $piece")
                return true // there is a pawn in the same column
            }
        }
        return false
    }

}