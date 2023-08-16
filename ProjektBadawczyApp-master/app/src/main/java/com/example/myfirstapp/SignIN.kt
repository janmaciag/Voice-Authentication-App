package com.example.myfirstapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.android.synthetic.main.activity_register_window.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SignIN : AppCompatActivity() {

    private var pathToVoice = Array<String>(3){""}
    private lateinit var waveRecorder: WaveRecorder
    private lateinit var filePath: String
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var permissionGranted = false
    private lateinit var recordingBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        recordingBtn = findViewById<Button>(R.id.recordingBtn3)
        var helper = SQLite(applicationContext)
        var db = helper.readableDatabase
        val loginButton = findViewById<Button>(R.id.loginbnt)

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm_ss")
        val date: String = simpleDateFormat.format(Date())
        filePath = externalCacheDir?.absolutePath + "/audioFile" + date + ".wav"

        waveRecorder = WaveRecorder(filePath)

        waveRecorder.onTimeElapsed = {
            if(it>3){                               // czas trwania próbki dźwięku - 3 sekundy
                waveRecorder.stopRecording()
                recordedText4.visibility = View.VISIBLE
                recordedText5.visibility = View.INVISIBLE
                loginButton.isEnabled = true
            }
        }

        recordingBtn.setOnClickListener{
            recordedText5.visibility = View.VISIBLE
            getVoice()
        }

        loginButton.setOnClickListener {
            var args = listOf<String>(nameregister.text.toString(), surnameregister.text.toString()).toTypedArray()
            var rs = db.rawQuery(
                "SELECT pathToVoice1, pathToVoice2, pathToVoice3 FROM UserTable WHERE name = ? AND surname = ?", args)
            try {
                rs.moveToFirst()
                pathToVoice[0] = rs.getString(0)
                pathToVoice[1] = rs.getString(1)
                pathToVoice[2] = rs.getString(2)
                if (!Python.isStarted()) {
                    Python.start(AndroidPlatform(this))
                }
                val py = Python.getInstance()
                val pyobj = py.getModule("script")
                var isWelcome = pyobj.callAttr("main", pathToVoice, filePath)
                if (isWelcome.toBoolean()) {
                    Toast.makeText(applicationContext, "You are logged in", Toast.LENGTH_LONG).show()
                }
                else
                    Toast.makeText(applicationContext, "Sorry. You have not been recognized", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show()
                Log.e("catch", e.toString())
            } finally {
                rs.close()
            }
        }

        val exStoragePermissionResult = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val recordAudioPermissionResult = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.RECORD_AUDIO
        )

        permissionGranted = exStoragePermissionResult == PackageManager.PERMISSION_GRANTED && recordAudioPermissionResult == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
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

        waveRecorder.startRecording()
        recordingBtn.isEnabled = false

    }
}