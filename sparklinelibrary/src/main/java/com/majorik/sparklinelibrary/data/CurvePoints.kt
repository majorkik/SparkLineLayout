package com.majorik.sparklinelibrary.data

import android.graphics.PointF

/*
    p1, p2 - first & second points
    cp1, cp2 - first & second control points
 */

data class CurvePoints(
    val p1: PointF,
    val cp1: PointF,
    val cp2: PointF,
    val p2: PointF
)