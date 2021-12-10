package com.example.mobilepjapp.record;

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpView (
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var startTimeStamp: Long = 0L

    private val countUpAction: Runnable = object: Runnable {
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime()
            val countTimeSec = ((currentTimeStamp - startTimeStamp) / 1000L).toInt()
            updateCountTime(countTimeSec)

            handler?.postDelayed(this, 1000L)
        }
    }

    fun startCountUp() {
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }

    fun stopCountUp() {
        handler?.removeCallbacks(countUpAction)
    }

    fun clearCountUpView() {
        updateCountTime(0)
    }

    private fun updateCountTime(countTimeSec: Int) {
        val minutes = countTimeSec / 60
        val seconds = countTimeSec % 60

        text = "%02d:%02d".format(minutes, seconds)
    }
}
