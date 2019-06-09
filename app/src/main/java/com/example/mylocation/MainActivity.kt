package com.example.mylocation

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.google.android.material.snackbar.Snackbar
import com.myloaction.services.LocationGatherers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        LocationGatherers.instance.mainActivity = this
        start.setOnClickListener {

            if (checkLocationPermissionGranted() && checkStoragePermissionGranted()) {
                LocationGatherers.instance.start()
                textView.text="Location collection service is running in the background..."
            } else {
                alertPermissionGranted()
            }
        }
        stop.setOnClickListener {
            LocationGatherers.instance.stop()
            textView.text="The location acquisition service has been stopped."
        }
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
