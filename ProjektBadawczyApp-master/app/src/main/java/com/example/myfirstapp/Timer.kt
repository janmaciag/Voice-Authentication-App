package com.example.myfirstapp

import android.os.Handler
import android.os.Looper

class Timer(listener: OnTimerTickListener) {

    interface OnTimerTickListener{
        fun onTimerTick(duration: String)
    }

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private val initial_duration = 3*1000L // ile czasu trwa nagranie
    private var duration = initial_duration
    private var delay = 100L

    init{
        runnable = Runnable{
            duration -= delay
            handler.postDelayed(runnable, delay)
            listener.onTimerTick(format())
        }
    }

    fun start(){
        handler.postDelayed(runnable, delay)
    }

    fun restart(){
        handler.removeCallbacks(runnable)
        duration = initial_duration
    }

    private fun format(): String{
        val millis:Long = duration % 1000
        val seconds:Long = (duration/1000)



        return "%02d:%02d".format(seconds, millis/10)
    }
}