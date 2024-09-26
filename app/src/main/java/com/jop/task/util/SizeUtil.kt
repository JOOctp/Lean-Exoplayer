package com.jop.task.util

import android.content.res.Resources

object SizeUtil {
    fun pxFromDp(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }
}