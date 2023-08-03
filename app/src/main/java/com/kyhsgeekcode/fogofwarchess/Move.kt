package com.kyhsgeekcode.fogofwarchess

data class Move(
    val from: Coord,
    val to: Coord,
    val who: Piece,
    val promotingTo: PieceType? = null,
    val capture: Boolean = false,
    val enPassantTarget: Piece? = null
) {
    fun getPgn(): String {
        val sb = StringBuilder()
        if (promotingTo != null) {
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