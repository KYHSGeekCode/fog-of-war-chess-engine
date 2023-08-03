package com.kyhsgeekcode.fogofwarchess

import androidx.compose.ui.graphics.Color

enum class PieceColor(val color: Color) {
    BLACK(Color(0xFF000000)),
    WHITE(Color(0xFFFFFFFF));

    fun opposite(): PieceColor = if (this == BLACK) WHITE else BLACK
}
