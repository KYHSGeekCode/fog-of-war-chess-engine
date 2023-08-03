package com.kyhsgeekcode.fogofwarchess

import androidx.compose.ui.graphics.Color

enum class PieceColor(val color: Color) {
    BLACK(Color(0xFF000000)),
    WHITE(Color(0xFFFFFFFF));

    fun opposite(): PieceColor = if (this == BLACK) WHITE else BLACK
}

enum class PieceType(val shortName: Char, val value: Int) {
    PAWN(' ', 1),
    ROOK('R', 5),
    KNIGHT('N', 3),
    BISHOP('B', 3),
    QUEEN('Q', 9),
    KING('K', 1000);
}

data class Move(
    val from: Cell,
    val to: Cell,
    val who: Piece,
    val isPromotion: Boolean = false,
    val capture: Boolean = false
) {
    fun apply(board: Board) {
        board.pieces.remove(from.x to from.y)
        board.pieces.remove(to.x to to.y)
        board.pieces[to.x to to.y] = who.copy(x = to.x, y = to.y, moved = true)
        if (capture) {
            // TODO: add captured piece to graveyard
        }
    }

    fun getPgn(): String {
        val sb = StringBuilder()
        if (isPromotion) {
            sb.append(who.type.shortName)
            sb.append(from.coordCode)
            sb.append(to.coordCode)
            sb.append(who.type.shortName)
            sb.append("=Q")
        } else {
            sb.append(who.type.shortName)
            sb.append(from.coordCode)
            if (capture) {
                sb.append('x')
            }
            sb.append(to.coordCode)
        }
        return sb.toString()
    }
}

data class Piece(
    val x: Int,
    val y: Int,
    val color: PieceColor,
    val type: PieceType,
    val moved: Boolean = false
) {
    val coordCode = "${'a' + x}${8 - y}"

    // returns possible moves based on the current board
    fun getPossibleMoves(board: Board): List<Move> {
        val possibleMoves: List<Move>
        when (type) {
            PieceType.PAWN -> {
                possibleMoves = getPawnPossibleMoves(board)
            }

            PieceType.ROOK -> {
                possibleMoves = getRookPossibleMoves(board)
            }

            PieceType.KNIGHT -> {
                possibleMoves = getKnightPossibleMoves(board)
            }

            PieceType.BISHOP -> {
                possibleMoves = getBishopPossibleMoves(board)
            }

            PieceType.QUEEN -> {
                possibleMoves = getQueenPossibleMoves(board)
            }

            PieceType.KING -> {
                possibleMoves = getKingPossibleMoves(board)
            }
        }
        // check if the move is valid: needless, as the king can be captured in the fog of war chess
//        val result = mutableListOf<Move>()
//        for (move in possibleMoves) {
//            val newBoard = board.clone()
//            move.apply(newBoard)
//            if (!newBoard.isCheck(color)) {
//                result.add(move)
//            }
//        }
        return possibleMoves
    }

    private fun getKingPossibleMoves(board: Board): List<Move> {
        val result = mutableListOf<Move>()
        for (i in -1..1) {
            for (j in -1..1) {
                val coord = x + i to y + j
                if (coord.first !in 0..7 || coord.second !in 0..7) {
                    continue
                }
                val piece = board.pieces[coord]
                // if there is a piece, check if it is an enemy
                if (piece != null) {
                    if (piece.color != color) {
                        result.add(Move(Cell(x, y), Cell(x + i, y + j), this, true))
                    }
                    continue
                }
                // if there is no piece, add the move
                result.add(Move(Cell(x, y), Cell(x + i, y + j), this))
            }
        }
        return result
    }

    private fun getQueenPossibleMoves(board: Board): List<Move> {
        val result = mutableListOf<Move>()
        result.addAll(getRookPossibleMoves(board))
        result.addAll(getBishopPossibleMoves(board))
        return result
    }

    private fun getBishopPossibleMoves(board: Board): List<Move> {
        // extend to 4 directions
        val result = mutableListOf<Move>()
        for (i in 1..7) {
            val coord = x + i to y + i
            if (coord.first !in 0..7 || coord.second !in 0..7) {
                break
            }
            val piece = board.pieces[coord]
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(Move(Cell(x, y), Cell(x + i, y + i), this, true))
                }
                break
            }
            // if there is no piece, add the move
            result.add(Move(Cell(x, y), Cell(x + i, y + i), this))
        }
        for (i in 1..7) {
            val coord = x - i to y - i
            if (coord.first !in 0..7 || coord.second !in 0..7) {
                break
            }
            val piece = board.pieces[coord]
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(Move(Cell(x, y), Cell(x - i, y + i), this, true))
                }
                break
            }
            // if there is no piece, add the move
            result.add(Move(Cell(x, y), Cell(x - i, y + i), this))
        }
        for (i in 1..7) {
            val coord = x + i to y - i
            if (coord.first !in 0..7 || coord.second !in 0..7) {
                break
            }
            val piece = board.pieces[coord]
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(Move(Cell(x, y), Cell(x + i, y - i), this, true))
                }
                break
            }
            // if there is no piece, add the move
            result.add(Move(Cell(x, y), Cell(x + i, y - i), this))
        }
        for (i in 1..7) {
            val coord = x - i to y - i
            if (coord.first !in 0..7 || coord.second !in 0..7) {
                break
            }
            val piece = board.pieces[coord]
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(Move(Cell(x, y), Cell(x - i, y - i), this, true))
                }
                break
            }
            // if there is no piece, add the move
            result.add(Move(Cell(x, y), Cell(x - i, y - i), this))
        }
        return result
    }

    private fun getKnightPossibleMoves(board: Board): List<Move> {
        val result = mutableListOf<Move>()
        val dx = intArrayOf(1, 2, 2, 1, -1, -2, -2, -1)
        val dy = intArrayOf(2, 1, -1, -2, -2, -1, 1, 2)
        for (i in 0..7) {
            val nx = x + dx[i]
            val ny = y + dy[i]
            if (nx in 0..7 && ny in 0..7) {
                val piece = board.pieces[nx to ny]
                if (piece == null) {
                    result.add(Move(Cell(x, y), Cell(nx, ny), this))
                } else {
                    if (piece.color != color) {
                        result.add(Move(Cell(x, y), Cell(nx, ny), this, true))
                    }
                }
            }
        }
        return result
    }

    private fun getRookPossibleMoves(board: Board): List<Move> {
        // extend to 4 directions
        val result = mutableListOf<Move>()
        for (i in x + 1..7) {
            val piece = board.pieces[i to y]
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(Move(Cell(x, y), Cell(i, y), this, true))
                }
                break
            }
            // no piece, continue
            result.add(Move(Cell(x, y), Cell(i, y), this))
        }
        for (i in x - 1 downTo 0) {
            val piece = board.pieces[i to y]
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(Move(Cell(x, y), Cell(i, y), this, true))
                }
                break
            }
            // no piece, continue
            result.add(Move(Cell(x, y), Cell(i, y), this))
        }
        for (i in y + 1..7) {
            val piece = board.pieces[x to i]
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(Move(Cell(x, y), Cell(x, i), this, true))
                }
                break
            }
            // no piece, continue
            result.add(Move(Cell(x, y), Cell(x, i), this))
        }
        for (i in y - 1 downTo 0) {
            val piece = board.pieces[x to i]
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(Move(Cell(x, y), Cell(x, i), this, true))
                }
                break
            }
            // no piece, continue
            result.add(Move(Cell(x, y), Cell(x, i), this))
        }
        return result
    }


    fun canCapture(board: Board, cell: Cell): Boolean {
        if (cell.x < 0 || cell.x > 7 || cell.y < 0 || cell.y > 7) { // out of board
            return false
        }
        val piece = board.pieces[cell.x to cell.y] ?: return false
        return piece.color != color
    }

    fun moveTo(cell: Cell) {
        TODO("Not yet implemented")
    }
}
