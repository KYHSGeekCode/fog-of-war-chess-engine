package com.kyhsgeekcode.fogofwarchess

data class Coord(
    val x: Int,
    val y: Int
) {
    val coordCode = "${'a' + x}${8 - y}"

    constructor(coord: Pair<Int, Int>) : this(coord.first, coord.second)

    fun isValid(): Boolean = x in 0..7 && y in 0..7
}
