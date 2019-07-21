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
        private val MARKER_BORDER_COLOR = Color.parseColor("#FFFFFF")
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
            data = arrayListOf(1, 2, 3, 2, 6, 4, 5, 9, 1, 3, 6, 5, 4, 7, 2, 1, 9, 4)
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
        val xStep = measuredWidth / data.count()
        val yStep = measuredHeight / data.max()!!

        pathSparkLine = Path().apply {
            val chartMaxHeight = (data.max()!! * yStep)
            var xStart = (0f + (xStep / 2F))

            moveTo(xStep / 4.0F, chartMaxHeight - (data.first() * yStep).toFloat())

            for (index in 0 until data.count()) {
                val prevDx: Float = (xStart - (xStart - xStep)) * sparkLineBezier
                val prevDy: Float
                val curDx: Float = ((xStart + xStep) - xStart) * sparkLineBezier
                val curDy: Float

                val prevVal: Float = if (index > 0) {
                    data[index - 1].toFloat()
                } else {
                    data[index].toFloat()
                }

                val nextVal: Float = if (index < data.size - 1) {
                    data[index + 1].toFloat()
                } else {
                    data[index].toFloat()
                }

                prevDy = (data[index] - prevVal) * sparkLineBezier
                curDy = (nextVal - data[index]) * sparkLineBezier

                this.cubicTo(
                    (xStart - xStep) + prevDx,
                    ((chartMaxHeight - (prevVal * yStep)) - prevDy),
                    xStart - curDx,
                    ((chartMaxHeight - (data[index] * yStep)) + curDy),
                    xStart,
                    (chartMaxHeight - (data[index] * yStep)).toFloat()
                )

                xStart += xStep
            }
        }

        canvas.drawPath(pathSparkLine, paintSparkLine)
        invalidate()
    }

    private fun drawMarkers(canvas: Canvas) {
        val xStep = measuredWidth / data.count()
        val yStep = measuredHeight / data.max()!!
        val xStepPadding = xStep / 2F

        for (i in 0 until data.size) {
            drawMarker(
                canvas, (i * xStep + xStepPadding),
                (data.max()!! * yStep) - (data[i] * yStep).toFloat()
            )
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
}