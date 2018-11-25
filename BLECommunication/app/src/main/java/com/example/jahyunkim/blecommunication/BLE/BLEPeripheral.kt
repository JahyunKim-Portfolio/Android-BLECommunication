package com.example.jahyunkim.blecommunication.BLE

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import java.util.*

class BLEPeripheral(ctx: Context, mBluetoothManager: BluetoothManager, SERVICE_UUID: UUID){
    val TAG: String = "myTag_BLEPeripheral"
    val ctx: Context = ctx
    val SERVICE_UUID: UUID = SERVICE_UUID

    var bluetoothManager: BluetoothManager = mBluetoothManager
    var bluetoothAdapter: BluetoothAdapter = mBluetoothManager.adapter
    var BLEAdvertiser: BluetoothLeAdvertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
    // var BLEAdvertiser: BluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser

    var gattServerCallback: GattServerCallback = GattServerCallback()
    var bluetoothGattServer: BluetoothGattServer? = null

    inner class GattServerCallback: BluetoothGattServerCallback(){}

    fun startAdvertise(){
        Log.i(TAG, "Start Advertising")
        bluetoothGattServer = bluetoothManager.openGattServer(ctx, gattServerCallback)
        Log.i(TAG, "BLE Server Opened")
        setupServer()


        if(BLEAdvertiser == null){
            Log.i(TAG, "Null BLE Advertiser")
            Toast.makeText(ctx, "Null BLE Advertiser", Toast.LENGTH_SHORT)
            return
        }
        var advertiseSettings: AdvertiseSettings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()

        var parcelUUID: ParcelUuid = ParcelUuid(SERVICE_UUID)
        var advertiseData: AdvertiseData = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addServiceUuid(parcelUUID)
            .build()

        BLEAdvertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.d(TAG, "Peripheral advertising started.")
        }

        override fun onStartFailure(errorCode: Int) {
            Log.d(TAG, "Peripheral advertising failed: $errorCode")
        }
    }

    fun setupServer(){
        var bluetoothGattService: BluetoothGattService = BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        bluetoothGattServer!!.addService(bluetoothGattService)
        Log.i(TAG, "BLE Service add to Server")
    }

    fun stopAdvertise(){
        Log.i(TAG, "Stop Advertising")
        if(BLEAdvertiser == null){
            Log.i(TAG, "Null BLE Advertiser")
            Toast.makeText(ctx, "Null BLE Advertiser", Toast.LENGTH_SHORT)
        }
        else{
            BLEAdvertiser.stopAdvertising(advertiseCallback)
        }
        if(bluetoothGattServer == null){
            Log.i(TAG, "Null Bluetooth Gatt Server")
            Toast.makeText(ctx, "Null Bluetooth Gatt Server", Toast.LENGTH_SHORT)
        }
        else{
            bluetoothGattServer!!.close()
        }
    }
}