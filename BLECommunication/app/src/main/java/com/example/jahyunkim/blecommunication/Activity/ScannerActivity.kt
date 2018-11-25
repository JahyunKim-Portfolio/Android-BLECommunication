package com.example.jahyunkim.blecommunication.Activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.example.jahyunkim.blecommunication.BLE.BLECentral
import com.example.jahyunkim.blecommunication.R
import com.example.jahyunkim.blecommunication.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_scanner.*
import java.io.File
import java.util.*

class ScannerActivity : AppCompatActivity() {

    val TAG: String = "myTag_ScannerActivity"
    val REQUEST_ENABLE_BT: Int = 1
    val REQUEST_FINE_LOCATION: Int = 2
    val WRITE_EXTERNAL_STORAGE: Int = 3

    lateinit var SERVICE_UUID: UUID

    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bleCentral: BLECentral

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        SERVICE_UUID =  UUID.fromString(getString(R.string.uuid))

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        bleCentral = BLECentral(this, bluetoothManager, SERVICE_UUID)

        txtCounter.text = SharedPreferenceController.getCounter(this).toString()

        btn_clear_log.setOnClickListener {
            SharedPreferenceController.setCounter(this, 0)
            txtCounter.text = ""
            txtScanResult.text = "Scan Result"
            val file: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "BLEScan")
            val deleteResult: Boolean = file.deleteRecursively()
            if(deleteResult){
                Log.i(TAG, "Delete DIR")
                Toast.makeText(this, "Delete Log Files", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.i(TAG, "Delete DIR Failed")
                Toast.makeText(this, "Cannot Delete Log Files", Toast.LENGTH_SHORT).show()
            }
        }

        btn_scan_ble.setOnClickListener{
            if(!hasPermission()){
                Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show()
            }
            else{
                btn_scan_ble.isEnabled = false
                btn_stop_scan_ble.isEnabled = true
                bleCentral.startScan()
            }
        }

        btn_stop_scan_ble.setOnClickListener {
            btn_scan_ble.isEnabled = true
            btn_stop_scan_ble.isEnabled = false
            bleCentral.stopScan()
        }
    }

    fun hasPermission():Boolean{
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            Log.i(TAG, "Request Bluetooth Enable")
            requestBluetoothEnable()
            return false
        }
        else if(!hasLocationPermission()){
            Log.i(TAG, "Request Location Permission")
            requestLocationPermission()
            return false
        }
        else if(!hasStoragePermission()){
            Log.i(TAG, "Request Storage Permission")
            requestStoragePermission()
            return false
        }
        return true
    }

    fun requestBluetoothEnable(){
        Log.i(TAG, "Request user to enable Bluetooth.")
        var enableBluetoothIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT)
    }

    fun hasLocationPermission(): Boolean{
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    fun hasStoragePermission(): Boolean{
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION)
    }
    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_FINE_LOCATION -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Log.i(TAG, "ACCESS_FINE_LOCATION Permission Denied")
                }
            }
        }
    }
}
