package com.kyhsgeekcode.fogofwarchess


enum class PieceType(
    val shortName: Char,
    val value: Int,
    val blackResId: Int,
    val whiteResId: Int
) {
    PAWN(' ', 1, R.drawable.pdt, R.drawable.plt),
    ROOK('R', 5, R.drawable.rdt, R.drawable.rlt),
    KNIGHT('N', 3, R.drawable.ndt, R.drawable.nlt),
    BISHOP('B', 3, R.drawable.bdt, R.drawable.blt),
    QUEEN('Q', 9, R.drawable.qdt, R.drawable.qlt),
    KING('K', 1000, R.drawable.kdt, R.drawable.klt);
}

data class Piece(
    val x: Int,
    val y: Int,
    val color: PieceColor,
    val type: PieceType,
    val moved: Boolean = false
) {
    val coordCode = "${'a' + x}${8 - y}"

    fun getPossibleMovesWithoutPawn(board: BoardSnapshot): List<Move> {
        val possibleMoves: List<Move>
        when (type) {
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

            else -> {
                throw Exception("Invalid piece type")
            }
        }
        return possibleMoves
    }

    // returns possible moves based on the current board
    fun getPossibleMoves(board: Board): List<Move> {
        val possibleMoves = when (type) {
            PieceType.PAWN -> {
                getPawnPossibleMoves(board)
            }

            else -> {
                getPossibleMovesWithoutPawn(board.toSnapshot())
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

    private fun getKingPossibleMoves(board: BoardSnapshot): List<Move> {
        val result = mutableListOf<Move>()
        for (i in -1..1) {
            for (j in -1..1) {
                val coord = x + i to y + j
                if (coord.first !in 0..7 || coord.second !in 0..7) {
                    continue
                }
                val piece = board.getPiece(coord)
                // if there is a piece, check if it is an enemy
                if (piece != null) {
                    if (piece.color != color) {
                        result.add(
                            Move(
                                board,
                                Coord(x, y),
                                Coord(x + i, y + j),
                                this,
                                captureTarget = piece
                            )
                        )
                    }
                    continue
                }
                // if there is no piece, add the move
                result.add(Move(board, Coord(x, y), Coord(x + i, y + j), this))
            }
        }
        // castling
        if (!moved) {
            // left rook
            val leftRook = board.getPiece(0, y)
            if (leftRook?.type == PieceType.ROOK && !leftRook.moved) {
                var canCastle = true
                for (i in 1 until x) {
                    if (board.getPiece(i, y) != null) {
                        canCastle = false
                        break
                    }
                }
                if (canCastle) {
                    result.add(
                        Move(
                            board,
                            Coord(x, y),
                            Coord(x - 2, y),
                            this,
                            castlingRook = leftRook
                        )
                    )
                }
            }
            // right rook
            val rightRook = board.getPiece(7, y)
            if (rightRook?.type == PieceType.ROOK && !rightRook.moved) {
                var canCastle = true
                for (i in x + 1 until 7) {
                    if (board.getPiece(i, y) != null) {
                        canCastle = false
                        break
                    }
                }
                if (canCastle) {
                    result.add(
                        Move(
                            board,
                            Coord(x, y),
                            Coord(x + 2, y),
                            this,
                            castlingRook = rightRook
                        )
                    )
                }
            }
        }
        return result
    }

    private fun getQueenPossibleMoves(board: BoardSnapshot): List<Move> {
        val result = mutableListOf<Move>()
        result.addAll(getRookPossibleMoves(board))
        result.addAll(getBishopPossibleMoves(board))
        return result
    }

    private fun getBishopPossibleMoves(board: BoardSnapshot): List<Move> {
        // extend to 4 directions
        val result = mutableListOf<Move>()
        for (i in 1..7) {
            val coord = Coord(x + i, y + i)
            if (!coord.isValid()) {
                break
            }
            val piece = board.getPiece(coord)
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(
                        Move(
                            board,
                            Coord(x, y),
                            coord,
                            this,
                            captureTarget = piece
                        )
                    )
                }
                break
            }
            // if there is no piece, add the move
            result.add(Move(board, Coord(x, y), coord, this))
        }
        for (i in 1..7) {
            val coord = Coord(x - i, y - i)
            if (!coord.isValid()) {
                break
            }
            val piece = board.getPiece(coord)
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(
                        Move(
                            board,
                            Coord(x, y),
                            coord,
                            this,
                            captureTarget = piece
                        )
                    )
                }
                break
            }
            // if there is no piece, add the move
            result.add(Move(board, Coord(x, y), coord, this))
        }
        for (i in 1..7) {
            val coord = Coord(x + i, y - i)
            if (!coord.isValid()) {
                break
            }
            val piece = board.getPiece(coord)
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(
                        Move(
                            board,
                            Coord(x, y),
                            coord,
                            this,
                            captureTarget = piece
                        )
                    )
                }
                break
            }
            // if there is no piece, add the move
            result.add(Move(board, Coord(x, y), coord, this))
        }
        for (i in 1..7) {
            val coord = Coord(x - i, y + i)
            if (!coord.isValid()) {
                break
            }
            val piece = board.getPiece(coord)
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(
                        Move(
                            board,
                            Coord(x, y),
                            coord,
                            this,
                            captureTarget = piece
                        )
                    )
                }
                break
            }
            // if there is no piece, add the move
            result.add(Move(board, Coord(x, y), coord, this))
        }
        return result
    }

    private fun getKnightPossibleMoves(board: BoardSnapshot): List<Move> {
        val result = mutableListOf<Move>()
        val dx = intArrayOf(1, 2, 2, 1, -1, -2, -2, -1)
        val dy = intArrayOf(2, 1, -1, -2, -2, -1, 1, 2)
        for (i in 0..7) {
            val nx = x + dx[i]
            val ny = y + dy[i]
            if (nx in 0..7 && ny in 0..7) {
                val piece = board.getPiece(nx to ny)
                if (piece == null) {
                    result.add(Move(board, Coord(x, y), Coord(nx, ny), this))
                } else {
                    if (piece.color != color) {
                        result.add(
                            Move(
                                board,
                                Coord(x, y),
                                Coord(nx, ny),
                                this,
                                captureTarget = piece
                            )
                        )
                    }
                }
            }
        }
        return result
    }

    private fun getRookPossibleMoves(board: BoardSnapshot): List<Move> {
        // extend to 4 directions
        val result = mutableListOf<Move>()
        for (i in x + 1..7) {
            val piece = board.getPiece(i, y)
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(
                        Move(
                            board,
                            Coord(x, y),
                            Coord(i, y),
                            this,
                            captureTarget = piece
                        )
                    )
                }
                break
            }
            // no piece, continue
            result.add(Move(board, Coord(x, y), Coord(i, y), this))
        }
        for (i in x - 1 downTo 0) {
            val piece = board.getPiece(i to y)
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(
                        Move(
                            board,
                            Coord(x, y),
                            Coord(i, y),
                            this,
                            captureTarget = piece
                        )
                    )
                }
                break
            }
            // no piece, continue
            result.add(Move(board, Coord(x, y), Coord(i, y), this))
        }
        for (i in y + 1..7) {
            val piece = board.getPiece(x to i)
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(
                        Move(
                            board,
                            Coord(x, y),
                            Coord(x, i),
                            this,
                            captureTarget = piece
                        )
                    )
                }
                break
            }
            // no piece, continue
            result.add(Move(board, Coord(x, y), Coord(x, i), this))
        }
        for (i in y - 1 downTo 0) {
            val piece = board.getPiece(x to i)
            // if there is a piece, check if it is an enemy
            if (piece != null) {
                if (piece.color != color) {
                    result.add(
                        Move(
                            board,
                            Coord(x, y),
                            Coord(x, i),
                            this,
                            captureTarget = piece
                        )
                    )
                }
                break
            }
            // no piece, continue
            result.add(Move(board, Coord(x, y), Coord(x, i), this))
        }
        return result
    }


    fun canCapture(board: BoardSnapshot, Coord: Coord): Boolean {
        if (Coord.x < 0 || Coord.x > 7 || Coord.y < 0 || Coord.y > 7) { // out of board
            return false
        }
        val piece = board.getPiece(Coord.x to Coord.y) ?: return false
        return piece.color != color
    }
}
