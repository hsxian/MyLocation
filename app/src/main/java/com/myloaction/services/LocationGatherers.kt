package com.myloaction.services

import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import java.io.File
import java.util.*


class LocationGatherers private constructor() {
    companion object {
        val instance: LocationGatherers by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LocationGatherers()
        }
    }

    lateinit var mainContext: Context
    private var timer: Timer? = null
    private var mTimerTask: TimerTask? = null
    private var fileDir = Environment.getExternalStorageDirectory().absolutePath + "/locations"
    private var filename = "$fileDir/data.csv"
    private var fileOfData: File = File(filename)


    fun start() {
        if (timer != null) return
        csvTableHead()
        initTimerTask()
        timer = Timer()
        timer?.schedule(mTimerTask, 0, 60000)

    }

    fun stop() {
        if (timer == null) return
        timer?.cancel()
        timer?.purge()
        timer = null

        mTimerTask?.cancel()
        mTimerTask = null

    }

    fun csvTableHead() {
        var file = File(fileDir)
        if (!file.exists()) {
            file.mkdir()
        }
    }

    fun initTimerTask() {
        mTimerTask = object : TimerTask() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                val lc = LocationUtil.getLocation(mainContext) as Location
                var str = Gson().toJson(lc) + ",\n"
                fileOfData.appendText(str)
            }
        }
    }
}