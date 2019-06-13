package com.example.mylocation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.google.android.material.snackbar.Snackbar
import com.myloaction.services.LocationGatherers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var instance: MainActivity
    }

    private lateinit var wakeLock: PowerManager.WakeLock

    @SuppressLint("InvalidWakeLockTag")
    private fun wakeLockInit() {
        var powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyTag")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        wakeLockInit()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        start.setOnClickListener {

            if (checkLocationPermissionGranted() && checkStoragePermissionGranted()) {
                wakeLock.acquire()
                LocationGatherers.instance.start()
                textView.text = "Location collection service is running in the background..."
            } else {
                alertPermissionGranted()
            }
        }
        stop.setOnClickListener {
            if (wakeLock.isHeld)
                wakeLock.release()
            LocationGatherers.instance.stop()
            textView.text = "The location acquisition service has been stopped."
        }

        instance = this
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.repeatCount == 0 && wakeLock.isHeld) {
            // 最小化应用

//            val intent = Intent(Intent.ACTION_MAIN)
//
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//            intent.addCategory(Intent.CATEGORY_HOME)
//
//            startActivity(intent)
            moveTaskToBack(true)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun alertPermissionGranted() {
        val tipDialog = AlertDialog.Builder(this)
            .setMessage("需要定位和存储权限，前往开启？")
            .setTitle("权限提示")
            .setPositiveButton("确定") { d, i ->
                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                var uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNeutralButton("取消", null)
            .create()

        tipDialog.show()
    }

    private fun checkLocationPermissionGranted(): Boolean {
        var coarse = PermissionChecker.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        var fine = PermissionChecker.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (coarse == PackageManager.PERMISSION_GRANTED || fine == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun checkStoragePermissionGranted(): Boolean {
        var storage = PermissionChecker.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (storage == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }
}
