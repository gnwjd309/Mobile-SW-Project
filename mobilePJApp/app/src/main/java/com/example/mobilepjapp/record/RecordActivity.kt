package com.example.mobilepjapp.record

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.mobilepjapp.R
import com.example.mobilepjapp.calender.CalenderActivity


class RecordActivity : AppCompatActivity() {

    private val soundVisualizerView: SoundVisualizerView by lazy {
        findViewById<SoundVisualizerView>(R.id.soundVisualizerView)
    }

    private val countUpView: CountUpView by lazy {
        findViewById<CountUpView>(R.id.recordTimeTextView)
    }

    private val recordButton: RecordButton by lazy {
        findViewById<RecordButton>(R.id.recordButton)
    }

    private val resetButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.resetButton)
    }

    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private var recorder: MediaRecorder? = null

    private var player: MediaPlayer? = null

    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    private var state = State.BEFORE_RECORDING
        set(value) {
            field = value
            resetButton.isEnabled = value == State.AFTER_RECODING || value == State.ON_PLAYING

            recordButton.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        CalenderActivity.intentMode = 1

        requestAudioPermission()

        initViews()

        initRecordButton()

        //initResetButton()

        soundVisualizerView.onRequestCurrentAmplitude = {recorder?.maxAmplitude ?: 0}

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
            grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {

        }
        else {
            Toast.makeText(this, "음성녹음 권한이 반드시 필요합니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:$packageName"))
            startActivity(intent)
            finish()
        }
    }

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initViews() {
        recordButton.updateIconWithState(state)
    }

    private fun initRecordButton() {
        recordButton.setOnClickListener {
            when(state) {
                State.BEFORE_RECORDING -> startRecording()
                State.ON_RECODING -> stopRecording()
                State.AFTER_RECODING -> startPlaying()
                State.ON_PLAYING -> stopPlaying()
            }
        }
    }


    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
            start()
        }
        soundVisualizerView.startVisualizing(false)
        countUpView.startCountUp()
        state = State.ON_RECODING
    }

    private fun stopRecording() {
        val builder = AlertDialog.Builder(this@RecordActivity)

        builder.setTitle("안내")
        builder.setMessage("녹음을 저장하시겠습니까?")

        builder.setPositiveButton("저장") { dialog, which ->
            Toast.makeText(this@RecordActivity, "시간이 짧아, 저장할 수 없습니다.", Toast.LENGTH_LONG).show()

            val subintent = Intent(this, CalenderActivity::class.java)
            startActivity(subintent)

            //recorder!!.stop()
            //recorder!!.release()
            recorder = null

            state = State.AFTER_RECODING

        }

        builder.setNegativeButton("삭제") { dialog, which ->
            Toast.makeText(this@RecordActivity, "녹음을 삭제합니다.", Toast.LENGTH_LONG).show()

            val subintent = Intent(this, CalenderActivity::class.java)
            startActivity(subintent)

            //recorder!!.stop()
            //recorder!!.release()
            recorder = null

            state = State.AFTER_RECODING

        }

        /*
        builder.setNegativeButton("취소", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

                //Toast.makeText(this@RecordActivity, "취소 버튼이 눌렸습니다", Toast.LENGTH_SHORT).show()
                val builder = AlertDialog.Builder(this@RecordActivity)

                run {
                    builder.setTitle("안내")
                    builder.setMessage("녹음을 삭제하시겠습니까?")

                    builder.setPositiveButton("삭제") { dialog, which ->
                        Toast.makeText(this@RecordActivity, "삭제 후 홈으로 돌아갑니다", Toast.LENGTH_SHORT).show()

                        stopPlaying()
                        stopRecording()
                        soundVisualizerView.clearVisualization()
                        countUpView.clearCountUpView()
                        //state = State.AFTER_RECODING
                        //initViews()

                        val subintent = Intent(this@RecordActivity, CalenderActivity::class.java)
                        startActivity(subintent)

                        //recorder!!.stop()
                        //recorder!!.release()
                        //recorder = null

                        // state = State.AFTER_RECODING

                    }
                }

                builder.setNegativeButton("뒤로") { dialog, which ->
                    Toast.makeText(this@RecordActivity, "뒤로 버튼이 눌렸습니다", Toast.LENGTH_SHORT).show()
                }
                builder.create().show()
            }

        })
        */
        builder.create().show();
        recorder?.stop()
        recorder?.release()
        recorder = null

        soundVisualizerView.stopVisualizing()
        countUpView.stopCountUp()
        state = State.AFTER_RECODING
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare() // 스트리밍 등 Uri를 가져올때 : prepareAsync()
        }
        // 재생이 완료되었을때 호출됨
        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECODING
        }
        player?.start()
        soundVisualizerView.startVisualizing(true)
        countUpView.startCountUp()
        state = State.ON_PLAYING
    }

    private fun stopPlaying() {
        player?.release()
        player = null

        soundVisualizerView.stopVisualizing()
        countUpView.stopCountUp()
        state = State.AFTER_RECODING
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}