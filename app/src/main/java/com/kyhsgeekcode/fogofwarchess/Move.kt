package com.kyhsgeekcode.fogofwarchess

import android.util.Log

data class Move(
    val boardSnapshot: BoardSnapshot,
    val from: Coord,
    val to: Coord,
    val who: Piece,
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
        } else {
            sb.append(who.type.shortName)
//            if ()
//                sb.append(from.coordCode)
            if (captureTarget != null) {
                sb.append('x')
            }
            sb.append(to.coordCode)
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