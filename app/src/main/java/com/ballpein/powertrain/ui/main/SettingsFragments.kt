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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ballpein.powertrain.R



class SettingsConfigFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // TODO :: app settings screen (trainer levels, HR & Power zones)
        val root = inflater.inflate(R.layout.settings_config, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        return root
    }
}

class SettingsSensorsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // TODO :: sensors settings screen (connect)
        val root = inflater.inflate(R.layout.settings_sensors, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        return root
    }
}

class settingsUploadFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // TODO :: upload settings screen (where to upload, format?)
        val root = inflater.inflate(R.layout.settings_upload, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        return root
    }
}
