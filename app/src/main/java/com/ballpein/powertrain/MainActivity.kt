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
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


// TODO !! logcat still shows app as velopower!?!? (android:authorities?)
// TODO :: start recording data @ START/RESUME, stop @ PAUSE
// TODO :: save (upload?) data @ SAVE (&& clear!!!)
// TODO :: record HRM data to "file" (format?)
// TODO :: record Speed/Cadence data to "file" (format?)
// TODO :: validate Speed (Cadence?)
// TODO :: calculate/report Power (Watts) data to workout activity
// TODO :: record Power (Watts) data to "file" (format?)
// TODO :: strings cleanup
// TODO :: general cleanup
// TODO :: tests!!!
// TODO :: must have sensors ready to connect when app is started...
// TODO :: sensor connection indicators
// TODO :: app icon
// TODO :: HRV test
// TODO :: History activity?

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {

    /*******************************************
     * Properties
     *******************************************/

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    /*******************************************
     * Activity function overrides
     *******************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // !?!?! onResume was not running so added here,
        // but then it started running and these were started twice!!
        startService(Intent(this, WorkoutService::class.java))
        startService(Intent(this, BleService::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            val intent = Intent(this, SettingsActivity::class.java).apply {}
            startActivity(intent)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
        // !?!?! onResume was not running so added to onStart,
        // but then it started running and these were started twice!!
//        startService(Intent(this, BleService::class.java))
//        startService(Intent(this, WorkoutService::class.java))
    }

    override fun onPause() {
        super.onPause()
        // !?!?! onPause was not running so did not see this do anything,
        // but then it started running and these were stopped!!!
//        stopService(Intent(this, BleService::class.java))
//        stopService(Intent(this, WorkoutService::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    /*******************************************
     * Private app support functions
     *******************************************/

    fun gotoWorkout(view: View) {
        val intent = Intent(this, WorkoutActivity::class.java).apply{}
        startActivity(intent)
    }

    fun gotoHRVTest(view: View) {}

    /*******************************************
     * Private ble support functions
     *******************************************/

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    /*******************************************
     * Extension functions
     *******************************************/

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

}