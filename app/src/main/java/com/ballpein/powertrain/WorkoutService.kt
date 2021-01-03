package com.ballpein.powertrain

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class WorkoutService : Service() {
    private val TAG = "WorkoutService"


    // TODO :: remove temporary method to do a send data
    private lateinit var mHandler: android.os.Handler
    private lateinit var mRunnable: Runnable

    private var prevSpdRotations: UInt = 0.toUInt()
    private var prevSpdTime: UShort = 0.toUShort()
    private var prevCadRotations: UShort = 0.toUShort()
    private var prevCadTime: UShort = 0.toUShort()

    companion object {
        val PWR_BROADCAST: String = WorkoutService::class.java.getName() + "Power"
        val EXTRA_PWR_DATA: String = "PowerData"
        val SPD_BROADCAST: String = WorkoutService::class.java.getName() + "Speed"
        val EXTRA_SPD_DATA: String = "SpeedData"
        val CAD_BROADCAST: String = WorkoutService::class.java.getName() + "Cadence"
        val EXTRA_CAD_DATA: String = "CadenceData"
    }

    override fun onCreate() {
        Log.i(TAG, "Service onCreate")
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    processSpeedData(context, intent)
                }
            }, IntentFilter(BleService.SPD_RAW_BROADCAST)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    processCadenceData(context, intent)
                }
            }, IntentFilter(BleService.CAD_RAW_BROADCAST)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i(TAG, "Service onStartCommand " + startId)

        // TODO :: remove temporary method to do a send data
        mHandler = android.os.Handler()
        mRunnable = Runnable { showPowerNumber() }
        mHandler.postDelayed(mRunnable, 5000)

        Log.i(TAG, "Service running")
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "Service onBind")
        return null
    }

    override fun onDestroy() {
        Log.i(TAG, "Service onDestroy")
    }

    // TODO :: remove temporary method to do a send data
    private fun showPowerNumber() {
        val rand = java.util.Random()
        val number = kotlin.math.abs(rand.nextInt() % 1000)
        Log.i(TAG, "random (power) number = $number")
        val intent = Intent(PWR_BROADCAST)
        intent.putExtra(EXTRA_PWR_DATA, number)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        mHandler.postDelayed(mRunnable, 2250)
    }


    private fun processSpeedData(context: Context, intent: Intent) {
        val spdRotations: UInt =
            intent.getIntExtra(BleService.EXTRA_SPD_RAW_DATA, 0).toUInt()
        val spdTime: UShort =
            intent.getIntExtra(BleService.EXTRA_TIME_RAW_DATA, 0).toUShort()
        if (prevSpdRotations == 0.toUInt()) {
            // no previous data, therefore no calculation
            prevSpdRotations = spdRotations
            prevSpdTime = spdTime
            return
        }
        if (prevSpdTime == spdTime) {
            // no change in data, therefore no calculation
            return
        }
        // TODO :: collect data and calculate over a "window" (15 seconds? 30 seconds?)
        val rpm: Float = calcRPM(spdRotations, spdTime, prevSpdRotations, prevSpdTime)
        // TODO :: add setting to convert to KPH (rather than MPH)
        // TODO :: add setting to identify wheel/tire size!!
        val speed: Float = 7.21F * rpm.toFloat() * 60F / 5280F
        // 700x??: Circumference = 700mm x 3.1416 = 2199.12mm
        // (700mm wheels/tires are not all 700mm!?!?; but close enough?)
        //                         2199.12mm / 25.4 mm/in = 86.58in
        //                         86.58in / 12 in/ft = 7.21ft (per revolution)
        //         MPH = ft/rev x rev/min x 60 min/hr x 1 mi/5280 ft
        Log.i(TAG, "Speed: $speed")
        val intent = Intent(SPD_BROADCAST)
        intent.putExtra(EXTRA_SPD_DATA, speed)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun processCadenceData(context: Context, intent: Intent) {
        val cadRotations: UShort =
            intent.getIntExtra(BleService.EXTRA_CAD_RAW_DATA, 0).toUShort()
        val cadTime: UShort =
            intent.getIntExtra(BleService.EXTRA_TIME_RAW_DATA, 0).toUShort()
        if (prevCadRotations == 0.toUShort()) {
            // no previous data, therefore no calculation
            prevCadRotations = cadRotations
            prevCadTime = cadTime
            return
        }
        if (prevCadTime == cadTime) {
            // no change in data, therefore no calculation
            return
        }
        // TODO :: collect data and calculate over a "window" (15 seconds? 30 seconds?)
        val rpm: Float = calcRPM(cadRotations.toUInt(), cadTime,
            prevCadRotations.toUInt(), prevCadTime)
        val cadence: Int = (rpm+0.5).toInt()
        Log.i(TAG, "Cadence: $cadence")
        val intent = Intent(CAD_BROADCAST)
        intent.putExtra(EXTRA_CAD_DATA, cadence)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    private fun calcRPM(currRotations: UInt, currTime: UShort,
                        prevRotations: UInt, prevTime: UShort): Float {
        val timediff = if (currTime > prevTime) {
            currTime - prevTime
        } else {
            (kotlin.UShort.MAX_VALUE - prevTime) + currTime
        }
        val rotdiff = if (currRotations > prevRotations) {
            currRotations - prevRotations
        } else {
            (kotlin.UInt.MAX_VALUE - prevRotations) + currRotations
        }
        val rpm: Float = (1 / ((timediff.toFloat() / 1024) / rotdiff.toFloat())) * 60
        return rpm
    }
}