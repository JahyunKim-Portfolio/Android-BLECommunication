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
import com.example.jahyunkim.blecommunication.BLE.BLEPeripheral
import com.example.jahyunkim.blecommunication.R
import kotlinx.android.synthetic.main.activity_advertiser.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class AdvertiserActivity : AppCompatActivity() {
    val TAG: String = "myTag_AdvertiseActivity"
    val REQUEST_ENABLE_BT: Int = 1
    val REQUEST_FINE_LOCATION: Int = 2

    lateinit var SERVICE_UUID: UUID

    lateinit var blePeripheral: BLEPeripheral
    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advertiser)

        SERVICE_UUID = UUID.fromString(getString(R.string.uuid))

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        blePeripheral = BLEPeripheral(this, bluetoothManager, SERVICE_UUID)

        swc_broadcast_1m.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                if(!hasPermission()){
                    Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show()
                    buttonView.isChecked = false
                }
                else {
                    txtServiceUUID.text = SERVICE_UUID.toString()
                    blePeripheral.startAdvertise()
                }
            }
            else{
                txtServiceUUID.text = "<Service UUID>"
                blePeripheral.stopAdvertise()
            }
        }
    }

    override fun onDestroy() {
        blePeripheral.stopAdvertise()
        super.onDestroy()
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

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION)
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
