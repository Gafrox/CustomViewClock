package ru.gustavo.customviewclock.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import java.lang.Math.toRadians
import java.util.*
import kotlin.math.*

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var center = PointF(0F, 0F)
    private var radius = 0F

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF000000.toInt()
        style = Paint.Style.STROKE
        strokeWidth = dp(context, 4F)
    }

    private val secondaryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF000000.toInt()
        style = Paint.Style.FILL
        textSize = 50F
        strokeWidth = dp(context, 2F)
    }

    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xFFFFFFFF.toInt()
        strokeWidth = dp(context, 4F)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        center = PointF(w / 2F, h / 2F)
        radius = min(w, h) / 2F - dp(context, 5F) / 2F
    }

    override fun onDraw(canvas: Canvas) {
        drawClockFace(canvas)
        drawNumbers(canvas)
        drawClockHands(canvas)
        invalidate()
    }

    private fun drawClockFace(canvas: Canvas) {
        canvas.drawCircle(center.x, center.y, radius, whitePaint)
        canvas.drawCircle(center.x, center.y, radius, paint)
        canvas.drawCircle(center.x, center.y, dp(context, 4F), secondaryPaint)
        for (number in 1..12) {
            canvas.drawLine(
                center.x + (0.9 * radius * sin(toRadians(number * 30.toDouble()))).toFloat(),
                center.y - (0.9 * radius * cos(toRadians(number * 30.toDouble()))).toFloat(),
                center.x + (radius * sin(toRadians(number * 30.toDouble()))).toFloat(),
                center.y - (radius * cos(toRadians(number * 30.toDouble()))).toFloat(),
                paint
            )
        }
        for (number in 1..60) {
            canvas.drawLine(
                center.x + (0.95 * radius * sin(toRadians(number * 6.toDouble()))).toFloat(),
                center.y - (0.95 * radius * cos(toRadians(number * 6.toDouble()))).toFloat(),
                center.x + (radius * sin(toRadians(number * 6.toDouble()))).toFloat(),
                center.y - (radius * cos(toRadians(number * 6.toDouble()))).toFloat(),
                secondaryPaint
            )
        }
    }

    private fun drawNumbers(canvas: Canvas) {
        for (number in 1..12) {
            val angle = Math.PI / 6 * (number - 3)
            val numb = number.toString()
            val textSize = secondaryPaint.textSize
            canvas.drawText(
                numb,
                ((center.x + cos(angle) * (radius * 1.6) / 2) - (textSize / 3.5 * numb.length)).toFloat(),
                ((center.y + sin(angle) * (radius * 1.6) / 2) + (textSize / 2.8)).toFloat(),
                secondaryPaint
            )
        }
    }

    private fun drawClockHands(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        var hour = calendar[Calendar.HOUR_OF_DAY]
        hour = if (hour > 12) hour - 12 else hour
        drawHand(canvas, (((hour + calendar[Calendar.MINUTE] / 60) * 5)), bolt = true, long = false)
        drawHand(canvas, calendar[Calendar.MINUTE], bolt = true, long = true)
        drawHand(canvas, calendar[Calendar.SECOND], bolt = false, long = true)
    }

    private fun drawHand(canvas: Canvas, time: Int, bolt: Boolean, long: Boolean) {
        val angle = (PI * time / 30 - PI / 2).toFloat()
        val length = if (long) radius * 0.9F else radius * 0.6F
        val paintHand = if (bolt) paint else secondaryPaint
        canvas.drawLine(
            center.x, center.y,
            (center.x + cos(angle) * length),
            (center.y + sin(angle) * length),
            paintHand
        )
    }

    private fun dp(context: Context, dp: Float) =
        ceil(context.resources.displayMetrics.density * dp)
}