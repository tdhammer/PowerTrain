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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

/*******************************************
 * Activity function overrides
 *******************************************/

class WorkoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(findViewById(R.id.tool_bar))
        // TODO :: add 'back' arrow to action bar
        init()
    }

    /*******************************************
     * Private app support functions
     *******************************************/

    private fun init() {
        var btn_start = findViewById(R.id.btnStart) as Button
        btn_start.setVisibility(View.VISIBLE)
        var btn_stop = findViewById(R.id.btnStop) as Button
        btn_stop.setVisibility(View.GONE)
        var btn_resume = findViewById(R.id.btnResume) as Button
        btn_resume.setVisibility(View.GONE)
        var btn_pause = findViewById(R.id.btnPause) as Button
        btn_pause.setVisibility(View.GONE)
        var btn_save = findViewById(R.id.btnSave) as Button
        btn_save.setVisibility(View.GONE)

        var tv_trainerlvl = findViewById(R.id.tvTrainerLevel) as TextView
        tv_trainerlvl.text = "0"
    }

    fun startRide(view: View) {
        var btn_start = findViewById(R.id.btnStart) as Button
        btn_start.setVisibility(View.GONE)
        var btn_pause = findViewById(R.id.btnPause) as Button
        btn_pause.setVisibility(View.VISIBLE)
    }
    fun pauseRide(view: View) {
        var btn_pause = findViewById(R.id.btnPause) as Button
        btn_pause.setVisibility(View.GONE)
        var btn_resume = findViewById(R.id.btnResume) as Button
        btn_resume.setVisibility(View.VISIBLE)
        var btn_stop = findViewById(R.id.btnStop) as Button
        btn_stop.setVisibility(View.VISIBLE)
    }
    fun resumeRide(view: View) {
        var btn_resume = findViewById(R.id.btnResume) as Button
        btn_resume.setVisibility(View.GONE)
        var btn_stop = findViewById(R.id.btnStop) as Button
        btn_stop.setVisibility(View.GONE)
        var btn_pause = findViewById(R.id.btnPause) as Button
        btn_pause.setVisibility(View.VISIBLE)
    }
    fun stopRide(view: View) {
        var btn_resume = findViewById(R.id.btnResume) as Button
        btn_resume.setVisibility(View.GONE)
        var btn_stop = findViewById(R.id.btnStop) as Button
        btn_stop.setVisibility(View.GONE)
        var btn_save = findViewById(R.id.btnSave) as Button
        btn_save.setVisibility(View.VISIBLE)
    }
    fun saveRide(view: View) {
        var btn_save = findViewById(R.id.btnSave) as Button
        btn_save.setVisibility(View.GONE)
        var btn_start = findViewById(R.id.btnStart) as Button
        btn_start.setVisibility(View.VISIBLE)
    }

    fun incrementTrainerLevel(view: View) {
        var tv_trainerlvl = findViewById(R.id.tvTrainerLevel) as TextView
        var lvl = tv_trainerlvl.text.toString().toInt()
        if (lvl < 6)
            lvl++
        tv_trainerlvl.text = lvl.toString()
    }
    fun decrementTrainerLevel(view: View) {
        var tv_trainerlvl = findViewById(R.id.tvTrainerLevel) as TextView
        var lvl = tv_trainerlvl.text.toString().toInt()
        if (lvl > 0)
            lvl--
        tv_trainerlvl.text = lvl.toString()
    }

}