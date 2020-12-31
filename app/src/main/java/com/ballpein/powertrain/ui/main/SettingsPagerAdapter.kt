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

package com.ballpein.powertrain.ui.main

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ballpein.powertrain.R

private val TAB_TITLES = arrayOf(
    R.string.settings_tab_text_1,
    R.string.settings_tab_text_2,
    R.string.settings_tab_text_3,
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SettingsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = when (position) {
        0 -> SettingsConfigFragment()
        1 -> SettingsSensorsFragment()
        2 -> settingsUploadFragment()
        else -> error("Unknown")
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return TAB_TITLES.size
    }
}