package com.example.myfirstapp

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_register_window.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



const val REQUEST_CODE = 200
class RegisterWindow : AppCompatActivity(), Timer.OnTimerTickListener {

    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var permissionGranted = false

    private lateinit var waveRecorder: WaveRecorder
    private lateinit var filePath: String
    private var recordCounter = 1
    private var pathToVoice = Array<String>(3){""}
    private lateinit var vibrator: Vibrator
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_window)

        val recordingBtn = findViewById<Button>(R.id.recordingBtn)
        recordingBtn.setOnClickListener{
            getVoice()
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        val finishBnt = findViewById<Button>(R.id.finishBtn)
        finishBnt.setOnClickListener {

            addRecord()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)


        }


        val exStoragePermissionResult = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val recordAudioPermissionResult = ContextCompat.checkSelfPermission(applicationContext, RECORD_AUDIO)

        permissionGranted = exStoragePermissionResult == PackageManager.PERMISSION_GRANTED && recordAudioPermissionResult == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)

        timer = Timer(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE && grantResults.isNotEmpty()) {
            val permissionToRecord = grantResults[0] === PackageManager.PERMISSION_GRANTED
            val permissionToStore = grantResults[1] === PackageManager.PERMISSION_GRANTED
            if (permissionToRecord && permissionToStore) {
                permissionGranted = true
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getVoice() {
        if(!permissionGranted){
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }

        // start recording

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm_ss")
        val date: String = simpleDateFormat.format(Date())
        filePath = externalCacheDir?.absolutePath + "/audioFile" + date + ".wav"

        waveRecorder = WaveRecorder(filePath)

        waveRecorder.startRecording()

        timer.start()
        timerText.visibility = View.VISIBLE
        recordingBtn.isEnabled = false

    }

    private fun addRecord() {
        val name = findViewById<EditText>(R.id.name).text.toString()
        val surname = findViewById<EditText>(R.id.surname).text.toString()
        val databaseHandler: SQLite = SQLite(this)
        if (name.isNotEmpty() && surname.isNotEmpty() ) {
            val status =
                databaseHandler.addUser(UsrModelClass(0, name, surname, pathToVoice[0],
                                                        pathToVoice[1], pathToVoice[2]))
            if (status > -1) {
                Log.i("Success", "User added")
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Name or Surname cannot be blank",
                Toast.LENGTH_LONG
            ).show()
        }



    }

    private fun stopRecorder(){
        timer.restart()
        waveRecorder.stopRecording()
        Toast.makeText(this, "Record saved!", Toast.LENGTH_SHORT).show()
    }

    override fun onTimerTick(duration: String) {
        timerText.text = duration

        if(duration=="00:00"){
            stopRecorder()
            timerText.visibility = View.INVISIBLE
            when (recordCounter) {
                1 -> {
                    recordedText1.visibility = View.VISIBLE
                    recordingBtn.isEnabled = true
                    pathToVoice[0] = filePath
                    recordCounter = 2
                }
                2 -> {
                    recordedText2.visibility = View.VISIBLE
                    recordingBtn.isEnabled = true
                    pathToVoice[1] = filePath
                    recordCounter = 3
                }
                3 -> {
                    recordedText3.visibility = View.VISIBLE
                    pathToVoice[2] = filePath
                    finishBtn.isEnabled = true
                    recordCounter = -1
                }
            }
        }
    }


}





