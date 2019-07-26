package com.majorik.sparklinelibrary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
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

    /*
    Paint
     */
    var paintSparkLine: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var paintMarker: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var paintMarkerStroke: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /*
    Path
     */
    var pathSparkLine: Path = Path()

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
                drawLine(it)
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
        paintSparkLine.style = Paint.Style.STROKE
        paintSparkLine.color = sparkLineColor
        paintSparkLine.strokeWidth = sparkLineThickness
        paintSparkLine.strokeCap = Paint.Cap.ROUND

        paintMarker.style = Paint.Style.FILL
        paintMarker.color = markerBackgroundColor

        paintMarkerStroke.style = Paint.Style.STROKE
        paintMarkerStroke.color = markerBorderColor
        paintMarkerStroke.strokeWidth = markerBorderSize
    }

    private fun drawLine(canvas: Canvas) {
        if (data.size < 2) {
            return
        }

        val max: Int = data.max() ?: 0
        val min: Int = data.min() ?: 0

        val xStep: Float = (measuredWidth / (data.count() - 1)).toFloat()
        val yStep: Float = (measuredHeight / (max - min)).toFloat()

        pathSparkLine = Path().apply {
            measuredHeight
            var xStart = 0f

            moveTo(xStart, measuredHeight - ((data.first() - min) * yStep))

            for (index in 0 until data.size) {
                val prevVal: Float = (if (index > 0) {
                    data[index - 1] - min
                } else {
                    data[index] - min
                }).toFloat()

                val nextVal: Float = (if (index < data.size - 1) {
                    data[index + 1] - min
                } else {
                    data[index] - min
                }).toFloat()

                val prevD = PointF(
                    (xStart - (xStart - xStep)) * sparkLineBezier,
                    ((data[index] - min) - prevVal) * sparkLineBezier
                )

                val curD = PointF(
                    ((xStart + xStep) - xStart) * sparkLineBezier,
                    (nextVal - (data[index] - min)) * sparkLineBezier
                )

                val controlPoint1 = PointF(
                    (xStart - xStep) + prevD.x,
                    ((measuredHeight - (prevVal * yStep)) - prevD.y)
                )
                val controlPoint2 = PointF(
                    xStart - curD.x,
                    (measuredHeight - ((data[index] - min) * yStep)) + curD.y
                )

                val currentPoint = PointF(xStart, measuredHeight - ((data[index] - min) * yStep))

                this.cubicTo(
                    controlPoint1.x,
                    controlPoint1.y,
                    controlPoint2.x,
                    controlPoint2.y,
                    currentPoint.x,
                    currentPoint.y
                )

                xStart += xStep
            }
        }

        canvas.drawPath(pathSparkLine, paintSparkLine)
        invalidate()
    }

    private fun drawMarkers(canvas: Canvas) {

        val max: Int = data.max() ?: 0
        val min: Int = data.min() ?: 0

        val xStep: Float = (measuredWidth / (data.count() - 1)).toFloat()
        val yStep: Float = (measuredHeight / (max - min)).toFloat()

        for (i in 0 until data.size) {
            val x: Float = i * xStep
            val y: Float = measuredHeight - yStep * (data[i] - min)

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
}