package com.example.jahyunkim.blecommunication.BLE

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.os.Environment
import android.os.Handler
import android.os.ParcelUuid
import android.support.constraint.R.id.parent
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.example.jahyunkim.blecommunication.R
import com.example.jahyunkim.blecommunication.SharedPreferenceController
import java.io.File
import java.io.FileOutputStream
import java.sql.Time
import java.util.*

class BLECentral(ctx: Activity, mBluetoothManager: BluetoothManager, SERVICE_UUID: UUID){
    val TAG: String = "myTag_BLEPeripheral"
    val SERVICE_UUID: UUID = SERVICE_UUID
    val SCAN_PERIOD: Long = 5000
    val ctx: Activity = ctx
    var mStopScanHandler: Handler? = null
    var mScanning: Boolean = false

    var bluetoothManager: BluetoothManager = mBluetoothManager
    var bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    var mBLEScanner: BluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    // var mBLEScanner: BluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    lateinit var mScanResult: TreeMap<String, ScanResult>
    private var mScanCallback: BLEScanCallback? = null

    private inner class BLEScanCallback: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            addScanResult(result)
        }
        override fun onBatchScanResults(results: List<ScanResult>) {
            for (result in results) {
                addScanResult(result)
            }
        }
        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "BLE Scan Failed with code $errorCode")
        }

        private fun addScanResult(result: ScanResult) {
            val device = result.device
            val deviceAddress = device.address
            mScanResult.put(deviceAddress, result)
        }
    }


    fun startScan(){
        if(mScanning){
            Toast.makeText(ctx, "Already scanning", Toast.LENGTH_SHORT).show()
            return
        }
        Log.i(TAG, "Scan Start")
        mScanning = true
        var filters: MutableList<ScanFilter> = mutableListOf<ScanFilter>()
        var sccanFilter: ScanFilter = ScanFilter.Builder().setServiceUuid(ParcelUuid(SERVICE_UUID)).build()
        filters.add(sccanFilter)
        var settings: ScanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
        mScanResult = TreeMap<String, ScanResult>()
        mScanCallback = BLEScanCallback()

        mBLEScanner.startScan(filters, settings, mScanCallback)

        mStopScanHandler = Handler()
        mStopScanHandler!!.postDelayed(this::recordScan, SCAN_PERIOD)
    }

    fun recordScan(){
        val counter: Int = SharedPreferenceController.getCounter(ctx)
        if(mScanning && bluetoothAdapter != null && bluetoothAdapter!!.isEnabled() && mBLEScanner != null){
            Log.i(TAG, "Record Scan")
            var txtScanResult = ctx.findViewById(R.id.txtScanResult) as TextView
            var txtCounter = ctx.findViewById(R.id.txtCounter) as TextView
            txtCounter.text = SharedPreferenceController.getCounter(ctx).toString()
            txtScanResult.text = ""
            for (deviceAddress in mScanResult.keys) {
                var device = mScanResult[deviceAddress]
                var deviceRSSI = device!!.rssi
                Log.d(TAG, "Found device: $deviceAddress $deviceRSSI")
                txtScanResult.text = txtScanResult.text.toString() + "$deviceAddress $deviceRSSI \n"
            }

            var createResult: Boolean = true
            val dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val fileName = counter.toString()

            var writeFile:File = File(dirPath, "BLEScan")
            if(!writeFile.exists()) {
                Log.i(TAG, "DIR Created: " + writeFile.absolutePath)
                createResult = writeFile.mkdirs()
            }
            if(createResult) {
                writeFile = File(writeFile.absolutePath + "/" + counter.toString() + ".txt")
                FileOutputStream(writeFile).use{
                    it.write(txtScanResult.text.toString().toByteArray())
                }

                if(mStopScanHandler!=null) {
                    // Toast.makeText(ctx, "#" + counter.toString() + " Record Complete", Toast.LENGTH_SHORT).show()
                    mScanResult.clear()
                    SharedPreferenceController.setCounter(ctx, counter + 1)
                    mStopScanHandler!!.postDelayed(this::recordScan, SCAN_PERIOD)
                }
            }
            else {
                Log.i(TAG, "DIR Create Failed: " + writeFile.absolutePath)
            }
        }
    }

    fun stopScan(){
        mBLEScanner.stopScan(mScanCallback)
        mScanCallback = null
        mScanning = false
        mStopScanHandler = null
        Log.i(TAG, "Scan Stop")
    }
}