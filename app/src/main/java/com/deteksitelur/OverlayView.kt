package com.deteksitelur


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results = listOf<BoundingBox>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var bounds = Rect()

    init {
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.teal_200)
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    @SuppressLint("DefaultLocale")
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        results.forEach {
            val left = it.x1 * width
            val top = it.y1 * height
            val right = it.x2 * width
            val bottom = it.y2 * height

            // Set warna box berdasarkan nama kelas
            boxPaint.color = when (it.clsName.lowercase()) {
                "telur" -> ContextCompat.getColor(context!!, R.color.lime)
                "bukan_telur" -> ContextCompat.getColor(context!!, R.color.danger)
                else -> Color.YELLOW
            }

            canvas.drawRect(left, top, right, bottom, boxPaint)

            canvas.drawRect(left, top, right, bottom, boxPaint)
            val formattedClassName = when (it.clsName.lowercase()) {
                "telur" -> "Telur"
                "bukan_telur" -> "Bukan Telur"
                else -> it.clsName.replaceFirstChar { c -> c.uppercase() }
            }

            val confidencePercent = (it.cnf * 100).coerceAtMost(100f)
            val drawableText = "$formattedClassName (${String.format("%.1f", confidencePercent)}%)"

            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawRect(
                left,
                top,
                left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                textBackgroundPaint
            )
            canvas.drawText(drawableText, left, top + bounds.height(), textPaint)

        }
    }

    fun setResults(boundingBoxes: List<BoundingBox>) {
        results = boundingBoxes
        invalidate()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}