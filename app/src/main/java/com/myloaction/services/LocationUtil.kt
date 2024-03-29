package com.myloaction.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
import android.os.SystemClock


object LocationUtil {

    private var isGPSFix: Boolean = false
    private var mLastLocationMillis: Long = 0
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
        locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 10F, gpsListener)
        locationManager.requestLocationUpdates(NETWORK_PROVIDER, 3000, 0F, netListener)
    }

    fun stopListenerLocation(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(gpsListener)
        locationManager.removeUpdates(netListener)
    }

    fun isNearNowTime(timeMillis: Long): Boolean {
        var before = System.currentTimeMillis() - 10000
        var after = System.currentTimeMillis() + 10000
        if (timeMillis in before until after) {
            return true
        }
        return false
    }

    private var gpsListener: LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            if (location == null) return
            mLastLocationMillis = SystemClock.elapsedRealtime()
            bestLocation = location
        }

        override fun onProviderDisabled(provider: String) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        }
    }
    private var netListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (location == null) return
            isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000
            if (isGPSFix) return
            bestLocation = location
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
