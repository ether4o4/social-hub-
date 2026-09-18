package com.example.socialhub

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.TypedValue
import android.view.View
import kotlin.math.abs

/**
 * The floating edge tab. Drawn as a rounded gradient pill with a grip line.
 * Drag logic (vertical reposition + tap toggle + drag-out to open) is wired by the service.
 */
class HandleView(context: Context) : View(context) {

    private val density = resources.displayMetrics.density
    private val wPx = dp(30)
    private val hPx = dp(84)

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gripPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 90
        strokeWidth = dp(3).toFloat()
        strokeCap = Paint.Cap.ROUND
    }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(40, 236, 72, 153)
        strokeWidth = dp(2).toFloat()
        style = Paint.Style.STROKE
    }
    private val rect = RectF()
    private val clipPath = Path()

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        updateShader()
    }

    private fun updateShader() {
        bgPaint.shader = LinearGradient(
            0f, 0f, wPx.toFloat(), 0f,
            intArrayOf(Color.parseColor("#EC4899"), Color.parseColor("#8B5CF6"), Color.parseColor("#3B82F6")),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    fun dp(v: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics
    ).toInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(wPx, hPx)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
        clipPath.reset()
        val r = dp(14).toFloat()
        clipPath.addRoundRect(rect, floatArrayOf(r, r, 0f, 0f, 0f, 0f, r, r), Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(clipPath)
        bgPaint.alpha = 235
        canvas.drawRect(rect, bgPaint)
        canvas.restore()

        // subtle outer glow
        canvas.drawRoundRect(rect, dp(14).toFloat(), dp(14).toFloat(), glowPaint)

        // grip dots
        val cx = wPx / 2f
        val cy = hPx / 2f
        val seg = dp(7)
        for (i in -1..1) {
            canvas.drawPoint(cx, cy + i * seg, gripPaint)
        }
    }

    @Suppress("unused")
    fun peekAmount(): Float = abs(translationX)
}
