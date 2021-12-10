package com.example.mobilepjapp.record

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.example.mobilepjapp.R

class RecordButton(context: Context, attrs: AttributeSet): AppCompatImageButton(context, attrs) {

    init {
        setBackgroundResource(R.drawable.ic_record_button_background)
    }

    fun updateIconWithState(state: State) {
        when(state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECODING -> {
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECODING -> {
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop)
            }
        }
    }
}