package com.example.jahyunkim.blecommunication.Activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.example.jahyunkim.blecommunication.BLE.BLECentral
import com.example.jahyunkim.blecommunication.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG: String = "myTag_MainActivity"
    lateinit var bluetoothAdapter:BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        btn_activity_advertiser.setOnClickListener {
            var intent: Intent = Intent(this, AdvertiserActivity::class.java)
            startActivity(intent)
        }
        btn_activity_scanner.setOnClickListener {
            var intent: Intent = Intent(this, ScannerActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "MainActivity onResume")
        var available: Boolean = true
        if(!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, "This device cannot use BLE functions", Toast.LENGTH_LONG)
            Log.w(TAG, "This device cannot use BLE functions")
            available = false
        }
        if(!bluetoothAdapter.isMultipleAdvertisementSupported()){
            Toast.makeText(this, "This device cannot use MultipleAdvertisement functions", Toast.LENGTH_LONG)
            Log.i(TAG, "This device cannot use MultipleAdvertisement functions")
            available = false
        }
        if(!available) finish()
    }
}
