package com.myloaction.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.*
import android.os.Bundle
import com.example.mylocation.MainActivity
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

object LocationUtil {

    private var gpsLocation: Location? = null
    private var netLocation: Location? = null
    var bestLocation: Location? = null
    /**
     * 获取经纬度
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getLocation(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var providers = locationManager.allProviders
        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                return location
            }
        }
        return null
    }

    @SuppressLint("MissingPermission")
    fun startListenerLocation(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val cr = Criteria()
//        cr.accuracy = Criteria.ACCURACY_FINE
//        cr.isAltitudeRequired = true
//        cr.isBearingRequired = true
//        cr.isCostAllowed = false
//        cr.powerRequirement = Criteria.POWER_LOW
//        var bestProvider = locationManager.getBestProvider(cr, true)
        locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 10F, gpsListener)
        locationManager.requestLocationUpdates(NETWORK_PROVIDER, 1000, 0F, netListener)
    }

    fun stopListenerLocation(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(gpsListener)
        locationManager.removeUpdates(netListener)
    }

    fun isNearNowTime(time: Date): Boolean {
        var before = GregorianCalendar()
        before.time = time
        before.add(Calendar.MINUTE, -1)
        var after = GregorianCalendar()
        after.time = time
        after.add(Calendar.MINUTE, 1)

        if (before.time < Date() && Date() < after.time) {
            return true
        }
        return false
    }

    var gpsListener: LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            if (location != null && isNearNowTime(Date(location.time))) {
                gpsLocation = location
                bestLocation = gpsLocation
            }
        }

        override fun onProviderDisabled(provider: String) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        }
    }
    var netListener: LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            if (location != null && isNearNowTime(Date(location.time))) {
                netLocation = location
                if (gpsLocation == null || !isNearNowTime(Date(gpsLocation?.time ?: 0))) {
                    bestLocation = netLocation
                }
            }
        }

        override fun onProviderDisabled(provider: String) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        }
    }


    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    fun isOPen(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        val gps = locationManager.isProviderEnabled(GPS_PROVIDER)
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        val network = locationManager.isProviderEnabled(NETWORK_PROVIDER)
        if (gps || network) {
            return true
        }

        return false
    }
}
