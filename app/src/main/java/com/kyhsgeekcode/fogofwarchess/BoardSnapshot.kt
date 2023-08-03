package com.kyhsgeekcode.fogofwarchess

data class BoardSnapshot(
    val cells: Array<Array<Cell>>,
    val pieces: Map<Pair<Int, Int>, Piece>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoardSnapshot

        if (!cells.contentDeepEquals(other.cells)) return false
        if (pieces != other.pieces) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cells.contentDeepHashCode()
        result = 31 * result + pieces.hashCode()
        return result
    }
}