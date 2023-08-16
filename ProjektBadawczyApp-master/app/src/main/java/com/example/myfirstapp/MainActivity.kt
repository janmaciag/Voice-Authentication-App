package com.example.myfirstapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.util.ArrayList

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerButton = findViewById<Button>(R.id.button2)
        registerButton.setOnClickListener{
            val intent = Intent(this, RegisterWindow::class.java)
            startActivity(intent)


        }
        val applyButton = findViewById<Button>(R.id.button)
        applyButton.setOnClickListener {
            val intent = Intent(this, SignIN::class.java)
            val dbHandler: SQLite = SQLite(this)
            startActivity(intent)
        }


    }

    fun checkDB(view: View) {
        val message = getItemList().toString()
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }

    private fun getItemList(): ArrayList<UsrModelClass> {
        val dbHandler: SQLite = SQLite(this)
        return dbHandler.viewUser()
    }
}