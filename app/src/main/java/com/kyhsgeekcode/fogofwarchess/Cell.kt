package com.kyhsgeekcode.fogofwarchess

data class Cell(
    val x: Int,
    val y: Int,
    val visible: Boolean = true,
    val color: BoardColor = if ((x + y) % 2 == 0) BoardColor.LIGHT else BoardColor.DARK
) {
    val coordCode = "${'a' + x}${8 - y}"
}