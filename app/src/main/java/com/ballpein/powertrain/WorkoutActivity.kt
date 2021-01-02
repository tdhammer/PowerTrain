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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.text.DecimalFormat

/*******************************************
 * Activity function overrides
 *******************************************/

class WorkoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("WorkoutActivity", "created")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(findViewById(R.id.tool_bar))
        // TODO :: add 'back' arrow to action bar
        init()

        val tvHR = findViewById<TextView>(R.id.tvHR)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val heartRate =
                        intent.getIntExtra(BleService.EXTRA_HR_RAW_DATA, 0)
//                    val dec = DecimalFormat("0##")
//                    tvHR.text = dec.format(heartRate)
                    tvHR.text = heartRate.toString()
                }
            }, IntentFilter(BleService.HR_RAW_BROADCAST)
        )
        // temporary speed value
        val tvSpd = findViewById<TextView>(R.id.tvSpeed)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val speed =
                        intent.getFloatExtra(BleService.EXTRA_SPD_RAW_DATA, 0.0F)
                    val dec = DecimalFormat("#.00")
                    tvSpd.text = dec.format(speed)
                }
            }, IntentFilter(BleService.SPD_RAW_BROADCAST)
        )
        // temporary power value
        val tvWatts = findViewById<TextView>(R.id.tvWatts)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val watts =
                        intent.getIntExtra(WorkoutService.EXTRA_PWR_DATA, 0)
//                    val dec = DecimalFormat("000")
//                    tvWatts.text = dec.format(watts)
                    tvWatts.text = watts.toString()
                }
            }, IntentFilter(WorkoutService.PWR_BROADCAST)
        )
        // temporary cadence value
        val tvCadence = findViewById<TextView>(R.id.tvCadence)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val cadence =
                        intent.getIntExtra(BleService.EXTRA_CAD_RAW_DATA, 0)
//                    val dec = DecimalFormat("000")
 //                   tvCadence.text = dec.format(cadence)
                    tvCadence.text = cadence.toString()
                }
            }, IntentFilter(BleService.CAD_RAW_BROADCAST)
        )
    }

    override fun onResume() {
        Log.i("WorkoutActivity", "resumed")
        super.onResume()
    }

    override fun onPause() {
        Log.i("WorkoutActivity", "paused")
        super.onPause()
    }

    override fun onStop() {
        Log.i("WorkoutActivity", "stopped")
        super.onStop()
    }

    override fun onDestroy() {
        Log.i("WorkoutActivity", "destroyed")
        super.onDestroy()
    }

    /*******************************************
     * Private app support functions
     *******************************************/

    private fun init() {
        var btn_start = findViewById<Button>(R.id.btnStart)
        btn_start.setVisibility(View.VISIBLE)
        var btn_stop = findViewById<Button>(R.id.btnStop)
        btn_stop.setVisibility(View.GONE)
        var btn_resume = findViewById<Button>(R.id.btnResume)
        btn_resume.setVisibility(View.GONE)
        var btn_pause = findViewById(R.id.btnPause) as Button
        btn_pause.setVisibility(View.GONE)
        var btn_save = findViewById<Button>(R.id.btnSave)
        btn_save.setVisibility(View.GONE)

        var tv_trainerlvl = findViewById<TextView>(R.id.tvTrainerLevel)
        tv_trainerlvl.text = "0"
    }

    fun startRide(view: View) {
        var btn_start = findViewById<Button>(R.id.btnStart)
        btn_start.setVisibility(View.GONE)
        var btn_pause = findViewById<Button>(R.id.btnPause)
        btn_pause.setVisibility(View.VISIBLE)
    }
    fun pauseRide(view: View) {
        var btn_pause = findViewById<Button>(R.id.btnPause)
        btn_pause.setVisibility(View.GONE)
        var btn_resume = findViewById<Button>(R.id.btnResume)
        btn_resume.setVisibility(View.VISIBLE)
        var btn_stop = findViewById<Button>(R.id.btnStop)
        btn_stop.setVisibility(View.VISIBLE)
    }
    fun resumeRide(view: View) {
        var btn_resume = findViewById<Button>(R.id.btnResume)
        btn_resume.setVisibility(View.GONE)
        var btn_stop = findViewById<Button>(R.id.btnStop)
        btn_stop.setVisibility(View.GONE)
        var btn_pause = findViewById<Button>(R.id.btnPause)
        btn_pause.setVisibility(View.VISIBLE)
    }
    fun stopRide(view: View) {
        var btn_resume = findViewById<Button>(R.id.btnResume)
        btn_resume.setVisibility(View.GONE)
        var btn_stop = findViewById<Button>(R.id.btnStop)
        btn_stop.setVisibility(View.GONE)
        var btn_save = findViewById<Button>(R.id.btnSave)
        btn_save.setVisibility(View.VISIBLE)
    }
    fun saveRide(view: View) {
        var btn_save = findViewById<Button>(R.id.btnSave)
        btn_save.setVisibility(View.GONE)
        var btn_start = findViewById<Button>(R.id.btnStart)
        btn_start.setVisibility(View.VISIBLE)
    }

    fun incrementTrainerLevel(view: View) {
        var tv_trainerlvl = findViewById<TextView>(R.id.tvTrainerLevel)
        var lvl = tv_trainerlvl.text.toString().toInt()
        if (lvl < 6)
            lvl++
        tv_trainerlvl.text = lvl.toString()
    }
    fun decrementTrainerLevel(view: View) {
        var tv_trainerlvl = findViewById<TextView>(R.id.tvTrainerLevel)
        var lvl = tv_trainerlvl.text.toString().toInt()
        if (lvl > 0)
            lvl--
        tv_trainerlvl.text = lvl.toString()
    }

}