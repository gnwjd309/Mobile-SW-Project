package com.example.mobilepjapp.record

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class SoundVisualizerView(
    context: Context,
    attrs: AttributeSet? = null): View(context, attrs) {

    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(android.R.color.holo_orange_light)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }

    var onRequestCurrentAmplitude: (() -> Int)? = null

    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0

    private var drawingAmplitudeList: List<Int> = emptyList()

    private var isReplaying: Boolean = false

    private var replayingPosition: Int = 0

    private val visualizeRepeatAction: Runnable = object: Runnable {
        override fun run() {

            if (!isReplaying) {
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                drawingAmplitudeList = listOf(currentAmplitude) + drawingAmplitudeList
            }
            else {
                replayingPosition++
            }
            invalidate() // onDraw를 호출함

            handler?.postDelayed(this, ACTION_INTERVAL)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2F
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudeList.let { amplitude ->
            if (isReplaying) {
                amplitude.takeLast(replayingPosition)
            }
            else {
                amplitude
            }
        }
            .forEach { amplitude ->
                val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F

                offsetX = offsetX - LINE_SPACE

                if (offsetX < 0) return@forEach

                canvas.drawLine(offsetX,centerY - lineLength / 2F,
                    offsetX, centerY + lineLength / 2F,
                    amplitudePaint)
            }
    }

    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }

    fun stopVisualizing() {
        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun clearVisualization() {
        drawingAmplitudeList = emptyList()
        invalidate()
    }

    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 20F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 20L
    }

}