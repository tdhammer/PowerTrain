/*
 * Copyright 2021 Tim D. Hammer (aka Ball Pein) tdhammer@acm.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ballpein.powertrain

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ballpein.powertrain.ui.main.SettingsPagerAdapter

private const val LOCATION_PERMISSION_REQUEST_CODE = 2

// TODO -- currently always scanning on app start (BleService); need to scan & save!
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setTitle("Settings")
        // TODO :: title (action?) bar w/ title & 'back' arrow
        val sectionsPagerAdapter = SettingsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    // TODO ?? cannot get this method (or setting text in isScanning) to work
    fun onClick(view: View?) {
        when(view?.id){
            R.id.btnBleScan->{
                    if (isScanning) {
                        stopBleScan()
                    } else {
                        startBleScan()
                    }
                }
            }
        }


    /*******************************************
     * ble support
     *******************************************/
    // Bluetooth Base UUID = 00000000-0000-1000-8000-00805F9B34FB
    // Bluetooth LE Heart Rate Service UUID = 180D
    private val HEARTRATE_SERVICE_UUID = "0000180D-0000-1000-8000-00805F9B34FB"
    // TODO :: add Speed & Cadence sensors

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    // From the previous section:
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val hrmFilter = ScanFilter.Builder().setServiceUuid(
        ParcelUuid.fromString(HEARTRATE_SERVICE_UUID)
    ).build()

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    public val scanResults = mutableListOf<ScanResult>()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                with(result.device) {
                    Log.i("ScanCallback", "Found same device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanResults[indexQuery] = result
            } else {
                with(result.device) {
                    Log.i("ScanCallback", "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanResults.add(result)
            }
            // TODO :: scan for fixed time? until all three sensors found? other?
//            stopBleScan()
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("ScanCallBack", "onScanFailed: code $errorCode")
        }
    }

    // TODO ?? cannot get setting text to work (nor changing onClick function)
    private var isScanning = false
//        set(value) {
//            field = value
//            runOnUiThread { scan_button.text = if (value) "Stop Scan" else "Start Scan" }
//        }

    fun startBleScan(view: View) {
        requestLocationPermission()
//        scanResults.clear()
//        scanResultAdapter.notifyDataSetChanged()
        bleScanner.startScan(listOf(hrmFilter), scanSettings, scanCallback)
        isScanning = true
    }

    fun startBleScan() {
//        requestLocationPermission()
//        bleScanner.startScan(null, scanSettings, scanCallback)
//        isScanning = true
    }

    private fun stopBleScan() {
//        bleScanner.stopScan(scanCallback)
//        isScanning = false
    }

    // TODO :: need to "exit" settings and reenter to get scan to run?!?!
    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
/*        runOnUiThread {
            alert {
                title = "Location permission required"
                message = "Starting from Android M (6.0), the system requires apps to be granted " +
                        "location access in order to scan for BLE devices."
                isCancelable = false
                positiveButton(android.R.string.ok) {
                    requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }.show()
        }*/
        requestPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

    val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }}