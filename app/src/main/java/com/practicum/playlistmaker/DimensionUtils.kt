package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue

object DimensionUtils {
    fun pxToDp(px: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            px,
            context.resources.displayMetrics
        ).toInt()
    }
}