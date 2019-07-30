package com.majorik.sparklinelibrary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.majorik.sparklinelibrary.data.CurvePoints
import com.majorik.sparklinelibrary.extensions.cubicTo
import kotlin.math.*
import kotlin.random.Random


class SparkLineLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /*
     Default vars
     */
    companion object DefaultAttrs {
        private val SPARKLINE_COLOR = Color.parseColor("#222222")
        private const val SPARKLINE_THICKNESS = 2F
        private const val SPARKLINE_BEZIER = 0.5F
        private const val MARKER_WIDTH = 3F
        private const val MARKER_HEIGHT = 3F
        private const val MARKER_CORNER_RADIUS = 0F
        private val MARKER_BACKGROUND_COLOR = Color.parseColor("#222222")
        private val MARKER_BORDER_COLOR = Color.parseColor("#222222")
        private const val MARKER_BORDER_SIZE = 1F
        private const val MARKER_IS_CIRCLE_STYLE = false
    }

    /*
    Attrs
     */
    var sparkLineColor: Int = SPARKLINE_COLOR
        set(value) {
            field = value
            initPaint()
            invalidate()
        }

    var sparkLineThickness: Float = SPARKLINE_THICKNESS
        set(value) {
            field = value
            initPaint()
            invalidate()
        }

    var sparkLineBezier: Float = SPARKLINE_BEZIER
        set(value) {
            field = value
            initPaint()
            invalidate()
        }

    var markerWidth: Float = MARKER_WIDTH
        set(value) {
            field = value
            initPaint()
            invalidate()
        }
    var markerHeight: Float = MARKER_HEIGHT
        set(value) {
            field = value
            initPaint()
            invalidate()
        }
    var markerCornerRadius: Float = MARKER_CORNER_RADIUS
        set(value) {
            field = value
            initPaint()
            invalidate()
        }
    var markerBackgroundColor: Int = MARKER_BACKGROUND_COLOR
        set(value) {
            field = value
            initPaint()
            invalidate()
        }
    var markerBorderColor: Int = MARKER_BORDER_COLOR
        set(value) {
            field = value
            initPaint()
            invalidate()
        }
    var markerBorderSize: Float = MARKER_BORDER_SIZE
        set(value) {
            field = value
            initPaint()
            invalidate()
        }
    var markerIsCircleStyle: Boolean = MARKER_IS_CIRCLE_STYLE
        set(value) {
            field = value
            initPaint()
            invalidate()
        }

    var lineRatio: Float = 0.5F
        set(value) {
            field = value
            invalidate()
        }

    var isSplitLine = false
        set(value) {
            field = value
            invalidate()
        }
    /*
    Paint
     */
    var paintSparkLine: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var paintMarker: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var paintMarkerStroke: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var paintLineLeft: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var paintLineRight: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /*
    Path
     */
    var pathSparkLine: Path = Path()
    var pathLineLeft: Path = Path()
    var pathLineRight: Path = Path()

    /*
    Data
     */
    var data: ArrayList<Int> = arrayListOf()
        set(value) {
            field.clear()
            field = value
            invalidate()
        }

    /*
    Local vars
     */
    private var dataMax: Int = 0
    private var dataMin: Int = 0
    private var xStep: Float = 0F
    private var yStep: Float = 0F

    init {
        if (attrs != null) {
            val styledAttrs = context.obtainStyledAttributes(
                attrs,
                R.styleable.SparkLineLayout, 0, 0
            )

            sparkLineColor =
                styledAttrs.getColor(R.styleable.SparkLineLayout_s_line_color, SPARKLINE_COLOR)

            sparkLineThickness = styledAttrs.getDimension(
                R.styleable.SparkLineLayout_s_line_thickness,
                2F
            )

            sparkLineBezier =
                styledAttrs.getFloat(R.styleable.SparkLineLayout_s_line_bezier, SPARKLINE_BEZIER)

            markerWidth =
                styledAttrs.getDimension(R.styleable.SparkLineLayout_s_marker_width, MARKER_WIDTH)

            markerHeight =
                styledAttrs.getDimension(R.styleable.SparkLineLayout_s_marker_height, MARKER_HEIGHT)

            markerCornerRadius =
                styledAttrs.getDimension(
                    R.styleable.SparkLineLayout_s_marker_radius,
                    MARKER_CORNER_RADIUS
                )

            markerBackgroundColor = styledAttrs.getColor(
                R.styleable.SparkLineLayout_s_marker_background_color,
                MARKER_BACKGROUND_COLOR
            )

            markerBorderColor = styledAttrs.getColor(
                R.styleable.SparkLineLayout_s_marker_border_color,
                MARKER_BORDER_COLOR
            )

            markerBorderSize = styledAttrs.getDimension(
                R.styleable.SparkLineLayout_s_marker_border_size,
                MARKER_BORDER_SIZE
            )

            markerIsCircleStyle = styledAttrs.getBoolean(
                R.styleable.SparkLineLayout_s_marker_is_circle_style,
                MARKER_IS_CIRCLE_STYLE
            )



            styledAttrs.recycle()
        }

        if (isInEditMode) {
            data = arrayListOf(
                1, 2, 2, 3, 4, 0, -15, 5, 6, 15, 8, 8, 8, 8
            )
        } else {
            //random data
            for (i in 0..25) {
                val random = Random.nextInt(25)
                data.add(random)
            }
        }

        initPaint()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            if (data.count() > 0) {
                initLocalVars()
                if (!isSplitLine) {
                    drawLine(it)
                } else {
                    drawSplitLine(it)
                }
                drawMarkers(it)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = min(result, specSize)
            }
        }

        if (result < desiredSize) {
            Log.e("SparkLineLayout", "Представление слишком маленькое")
        }

        return result
    }

    private fun initPaint() {
        paintSparkLine.color = sparkLineColor
        paintSparkLine.strokeWidth = sparkLineThickness
        paintSparkLine.strokeCap = Paint.Cap.ROUND
        paintSparkLine.style = Paint.Style.STROKE

        paintMarker.style = Paint.Style.FILL
        paintMarker.color = markerBackgroundColor

        paintMarkerStroke.style = Paint.Style.STROKE
        paintMarkerStroke.color = markerBorderColor
        paintMarkerStroke.strokeWidth = markerBorderSize

        paintLineLeft.color = Color.RED
        paintLineLeft.strokeWidth = sparkLineThickness
        paintLineLeft.strokeCap = Paint.Cap.ROUND
        paintLineLeft.style = Paint.Style.STROKE

        paintLineRight.color = Color.GREEN
        paintLineRight.strokeWidth = sparkLineThickness
        paintLineRight.strokeCap = Paint.Cap.ROUND
        paintLineRight.style = Paint.Style.STROKE
    }

    private fun initLocalVars() {
        dataMax = data.max() ?: 0
        dataMin = data.min() ?: 0
        xStep = (measuredWidth / (data.count() - 1)).toFloat()
        yStep = (measuredHeight / (dataMax - dataMin)).toFloat()

        if (lineRatio < 0F) {
            lineRatio = 0F
        } else if (lineRatio > 1F) {
            lineRatio = 1F
        }
    }

    private fun drawLine(canvas: Canvas) {
        if (data.size < 2) {
            return
        }

        pathSparkLine = Path().apply {
            var xStart = 0f

            moveTo(xStart, measuredHeight - ((data.first() - dataMin) * yStep))

            for (index in 0 until data.size) {
                val prevVal: Float = (if (index > 0) {
                    data[index - 1] - dataMin
                } else {
                    data[index] - dataMin
                }).toFloat()

                val nextVal: Float = (if (index < data.size - 1) {
                    data[index + 1] - dataMin
                } else {
                    data[index] - dataMin
                }).toFloat()

                val prevD = PointF(
                    (xStart - (xStart - xStep)) * sparkLineBezier,
                    ((data[index] - dataMin) - prevVal) * sparkLineBezier
                )

                val curD = PointF(
                    ((xStart + xStep) - xStart) * sparkLineBezier,
                    (nextVal - (data[index] - dataMin)) * sparkLineBezier
                )

                val controlPoint1 = PointF(
                    (xStart - xStep) + prevD.x,
                    ((measuredHeight - (prevVal * yStep)) - prevD.y)
                )
                val controlPoint2 = PointF(
                    xStart - curD.x,
                    (measuredHeight - ((data[index] - dataMin) * yStep)) + curD.y
                )

                val currentPoint =
                    PointF(xStart, measuredHeight - ((data[index] - dataMin) * yStep))

                this.cubicTo(
                    controlPoint1,
                    controlPoint2,
                    currentPoint
                )

                xStart += xStep
            }
        }

        canvas.drawPath(pathSparkLine, paintSparkLine)
        invalidate()
    }

    private fun drawMarkers(canvas: Canvas) {
        for (i in 0 until data.size) {
            val x: Float = i * xStep
            val y: Float = measuredHeight - yStep * (data[i] - dataMin)

            drawMarker(canvas, x, y)
        }
    }

    private fun drawMarker(canvas: Canvas, x: Float, y: Float) {
        if (markerIsCircleStyle) {
            canvas.drawCircle(x, y, markerWidth / 2, paintMarker)
            if (markerBorderSize > 0) {
                canvas.drawCircle(x, y, markerWidth / 2, paintMarkerStroke)
            }
        } else {
            canvas.drawRect(
                x - (markerWidth / 2),
                y - (markerHeight / 2),
                x + (markerWidth / 2),
                y + (markerHeight / 2),
                paintMarker
            )
            if (markerBorderSize > 0) {
                canvas.drawRect(
                    x - (markerWidth / 2),
                    y - (markerHeight / 2),
                    x + (markerWidth / 2),
                    y + (markerHeight / 2),
                    paintMarkerStroke
                )
            }
        }
    }

    private fun calcProgressPoint(
        p1: Float,
        p2: Float,
        p3: Float,
        p4: Float,
        t: Float
    ): Float {
        val x1 = (1 - t).pow(3.0F) * p1
        val x2 = 3 * t * (1 - t).pow(2.0F) * p2
        val x3 = 3 * t.pow(2.0F) * (1 - t) * p3
        val x4 = t.pow(3.0F) * p4

        return x1 + x2 + x3 + x4
    }

    private fun drawSplitLine(canvas: Canvas) {
        var xStart = 0F
        val numPoint = calculateSplitNumPoint()
        val t: Float = calculateRationBetweenPoints()

        pathLineLeft.moveTo(xStart, measuredHeight - ((data.first() - dataMin) * yStep))

        for (index in 0 until data.size) {
            val prevVal: Float = (if (index > 0) {
                data[index - 1] - dataMin
            } else {
                data[index] - dataMin
            }).toFloat()

            val nextVal: Float = (if (index < data.size - 1) {
                data[index + 1] - dataMin
            } else {
                data[index] - dataMin
            }).toFloat()

            val prevD = PointF(
                (xStart - (xStart - xStep)) * sparkLineBezier,
                ((data[index] - dataMin) - prevVal) * sparkLineBezier
            )

            val curD = PointF(
                ((xStart + xStep) - xStart) * sparkLineBezier,
                (nextVal - (data[index] - dataMin)) * sparkLineBezier
            )

            val controlPoint1 = PointF(
                (xStart - xStep) + prevD.x,
                ((measuredHeight - (prevVal * yStep)) - prevD.y)
            )
            val controlPoint2 = PointF(
                xStart - curD.x,
                (measuredHeight - ((data[index] - dataMin) * yStep)) + curD.y
            )

            val currentPoint =
                PointF(xStart, measuredHeight - ((data[index] - dataMin) * yStep))

            when {
                index < numPoint -> pathLineLeft.cubicTo(
                    controlPoint1,
                    controlPoint2,
                    currentPoint
                )
                index == numPoint -> {
                    val prevPoint: PointF = if (numPoint != 0) {
                        PointF(
                            xStart - xStep,
                            measuredHeight - ((data[index - 1] - dataMin) * yStep)
                        )
                    } else {
                        currentPoint
                    }

                    val curveLeftPoints = splitCurve(
                        prevPoint,
                        controlPoint1,
                        controlPoint2,
                        currentPoint,
                        t,
                        false
                    )

                    val curveRightPoints = splitCurve(
                        prevPoint,
                        controlPoint1,
                        controlPoint2,
                        currentPoint,
                        t,
                        true
                    )

                    pathLineLeft.cubicTo(
                        curveLeftPoints.cp1,
                        curveLeftPoints.cp2,
                        curveLeftPoints.p2
                    )

                    pathLineRight.moveTo(curveRightPoints.p1.x, curveRightPoints.p1.y)

                    pathLineRight.cubicTo(
                        curveRightPoints.cp1,
                        curveRightPoints.cp2,
                        curveRightPoints.p2
                    )
                }
                else -> pathLineRight.cubicTo(
                    controlPoint1,
                    controlPoint2,
                    currentPoint
                )
            }

            xStart += xStep
        }

        canvas.drawPath(pathLineLeft, paintLineLeft)
        canvas.drawPath(pathLineRight, paintLineRight)
    }

    private fun calculateSplitNumPoint(): Int {
        if (lineRatio == 1.0F) {
            return data.size - 1
        }
        if (lineRatio == 0.0F) {
            return 0
        }
        val widthRatio = measuredWidth * lineRatio
        return ceil(widthRatio / xStep).toInt()
    }

    private fun calculateRationBetweenPoints(): Float {
        if (lineRatio == 1.0F) {
            return 1F
        }
        if (lineRatio == 0.0F) {
            return 0F
        }
        val widthRatio = measuredWidth * lineRatio
        return (widthRatio % xStep) / xStep
    }

    private fun splitCurve(
        p1: PointF,
        cp1: PointF,
        cp2: PointF,
        p2: PointF,
        t: Float,
        reverse: Boolean
    ): CurvePoints {
        val p12 = calculateDistance(p1, cp1, t)

        val p23 = calculateDistance(cp1, cp2, t)

        val p34 = calculateDistance(cp2, p2, t)

        val p123 = calculateDistance(p12, p23, t)

        val p234 = calculateDistance(p23, p34, t)

        val p1234 = calculateDistance(p123, p234, t)

        return if (!reverse) {
            CurvePoints(p1, p12, p123, p1234)
        } else {
            CurvePoints(p1234, p234, p34, p2)
        }
    }

    private fun calculateDistance(point1: PointF, point2: PointF, t: Float): PointF {
        return PointF(
            (point2.x - point1.x) * t + point1.x,
            (point2.y - point1.y) * t + point1.y
        )
    }
}