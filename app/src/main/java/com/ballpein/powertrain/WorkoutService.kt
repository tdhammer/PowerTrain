package com.ballpein.powertrain

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager




class WorkoutService : Service() {
    private val TAG = "WorkoutService"


    // TODO :: remove temporary method to do a send data
    private lateinit var mHandler: android.os.Handler
    private lateinit var mRunnable: Runnable

    companion object {
        val PWR_BROADCAST: String = WorkoutService::class.java.getName() + "Power"
        val EXTRA_PWR_DATA: String = "PowerData"
    }

    override fun onCreate() {
        Log.i(TAG, "Service onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i(TAG, "Service onStartCommand " + startId)

        // TODO :: remove temporary method to do a send data
        mHandler = android.os.Handler()
        mRunnable = Runnable { showRandomNumber() }
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
    private fun showRandomNumber() {
        val rand = java.util.Random()
        val number = kotlin.math.abs(rand.nextInt() % 1000)
        Log.i(TAG, "random (power) number = $number")
        val intent = Intent(PWR_BROADCAST)
        intent.putExtra(EXTRA_PWR_DATA, number)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        mHandler.postDelayed(mRunnable, 2250)
    }
}