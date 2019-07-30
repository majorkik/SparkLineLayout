package com.majorik.sparklinelibrary.extensions

import android.graphics.Path
import android.graphics.PointF

fun Path.cubicTo(controlPoint1: PointF, controlPoint2: PointF, point2: PointF) {
    this.cubicTo(
        controlPoint1.x,
        controlPoint1.y,
        controlPoint2.x,
        controlPoint2.y,
        point2.x,
        point2.y
    )
}