package com.kyhsgeekcode.fogofwarchess

data class Move(
    val from: Coord,
    val to: Coord,
    val who: Piece,
    val isPromotion: Boolean = false,
    val capture: Boolean = false
) {
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