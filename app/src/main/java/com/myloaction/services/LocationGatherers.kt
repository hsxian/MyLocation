package com.myloaction.services

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.example.mylocation.MainActivity
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.util.*


class LocationGatherers private constructor() {
    companion object {
        val instance: LocationGatherers by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LocationGatherers()
        }
    }

    private var timer: Timer? = null
    private var mTimerTask: TimerTask? = null
    private var fileDir = Environment.getExternalStorageDirectory().absolutePath + "/locations"
    private var filename = ""
    private lateinit var fileOfData: File
    private var lastLocation: Location? = null
    @RequiresApi(Build.VERSION_CODES.N)
    var outputsdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    @RequiresApi(Build.VERSION_CODES.N)
    var dateTimeSdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


    fun start() {
        if (timer != null) return
        initTimerTask()
        timer = Timer()
        timer?.schedule(mTimerTask, 0, 1000)
        LocationUtil.startListenerLocation(MainActivity.instance)

    }

    fun stop() {
        if (timer == null) return
        timer?.cancel()
        timer?.purge()
        timer = null

        mTimerTask?.cancel()
        mTimerTask = null
        LocationUtil.stopListenerLocation(MainActivity.instance)
    }

    private fun equalsLocation(l1: Location, l2: Location): Boolean {
        if (l1 == null || l2 == null) return true

        if (l1.longitude == l2.longitude
            && l1.latitude == l2.latitude
            && l1.altitude == l2.altitude
        ) return true

        return false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkFile() {
        var name = "${outputsdf.format(Date())}.data.csv"
        if (filename.isNullOrBlank() || filename != name) {
            fileOfData = File("$fileDir/$name")
            filename = name
        }
    }

    private fun initTimerTask() {
        mTimerTask = object : TimerTask() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                try {
                    if (!LocationUtil.isOPen(MainActivity.instance)) return
                    var lc: Location = LocationUtil.bestLocation ?: return
                    if (outputsdf.format(System.currentTimeMillis()) != outputsdf.format(lc.time)) return
                    MainActivity.instance.runOnUiThread {
                        MainActivity.instance.textView.text =
                            "now:${dateTimeSdf.format(System.currentTimeMillis())}\n" +
                                    "lon:${lc.longitude}\n" +
                                    "lat:${lc.latitude}\n" +
                                    "alt:${lc.altitude}\n" +
                                    "bear:${lc.bearing}\n" +
                                    "speed:${lc.speed}m/s\t${lc.speed * 3.6}km/h\n" +
                                    "acc:${lc.accuracy}\n" +
                                    "provider:${lc.provider}"
                    }

                    var str = "${dateTimeSdf.format(System.currentTimeMillis())}," +
                            "${lc.longitude}," +
                            "${lc.latitude}," +
                            "${lc.altitude}," +
                            "${lc.bearing}," +
                            "${lc.speed}," +
                            "${lc.accuracy}," +
                            "${lc.provider}\n"

                    if (lastLocation == null || !equalsLocation(lc, lastLocation as Location)) {
                        checkFile()
                        fileOfData.appendText(str)
                    }
                    lastLocation = lc
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}