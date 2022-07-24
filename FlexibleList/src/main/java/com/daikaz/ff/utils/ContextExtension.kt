package com.daikaz.ff.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.Size

fun Int.toPx(context: Context?): Int = (this * (context?.resources?.displayMetrics?.density ?: 0f)).toInt()
fun Float.toPx(context: Context?): Int = (this * (context?.resources?.displayMetrics?.density ?: 0f)).toInt()
fun Int.toDp(context: Context?): Int = (this / (context?.resources?.displayMetrics?.density ?: 0f)).toInt()

fun Context?.dpToPx(dp: Float) = dp.toPx(this)
fun Context?.pxToDp(dp: Int) = dp.toDp(this)

fun Context?.screenSize(): Size {
    this ?: return Size(0, 0)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        (this as? Activity)?.run {
            val bounds: Rect = windowManager.maximumWindowMetrics.bounds
            Size(bounds.width(), bounds.height())
        } ?: Size(0, 0)
    } else {
        Size(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
    }
}

fun Context?.screenWidth(): Int = screenSize().width
fun Context?.screenHeight(): Int = screenSize().height
