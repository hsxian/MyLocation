package com.myloaction.services

import android.content.Context
import android.icu.text.SimpleDateFormat
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
    private var filename = ""
    private lateinit var fileOfData: File
    private var lastLocation: Location? = null
    @RequiresApi(Build.VERSION_CODES.N)
    var outputsdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")


    fun start() {
        if (timer != null) return
        initTimerTask()
        timer = Timer()
        timer?.schedule(mTimerTask, 0, 1000)

    }

    fun stop() {
        if (timer == null) return
        timer?.cancel()
        timer?.purge()
        timer = null

        mTimerTask?.cancel()
        mTimerTask = null

    }

    private fun equalsLocation(l1: Location, l2: Location): Boolean {
        if (l1 == null || l2 == null) return false

        if (l1.longitude == l2.longitude
            && l1.latitude == l2.latitude
            && l1.altitude == l2.altitude
        ) return true

        return false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkFile() {
        var name = "${outputsdf.format(Date())}.data.json"
        if (filename.isNullOrBlank() || filename != name) {
            fileOfData = File("$fileDir/$name")
            filename = name
        }
    }

    private fun initTimerTask() {
        mTimerTask = object : TimerTask() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                val lc = LocationUtil.getLocation(mainContext) as Location
                var str = Gson().toJson(lc) + ",\n"
                if (lastLocation == null || !equalsLocation(lc, lastLocation as Location)) {
                    checkFile()
                    fileOfData.appendText(str)
                }
                lastLocation = lc
            }
        }
    }
}