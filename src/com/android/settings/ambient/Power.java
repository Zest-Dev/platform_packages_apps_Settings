/*
 * Copyright (C) 2012 AMBIENT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.ambient;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManagerGlobal;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Power extends SettingsPreferenceFragment implements
                   OnPreferenceChangeListener {
    private static final String TAG = "Power";

    private static final String REBOOT_IN_POWER_MENU = "reboot_in_power";

    private CheckBoxPreference mReboot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ambient_power_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mReboot = (CheckBoxPreference) findPreference(REBOOT_IN_POWER_MENU);
        mReboot.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.Secure.REBOOT_IN_POWER_MENU, 0) == 1));

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preference == mReboot) {
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.REBOOT_IN_POWER_MENU,
                    mReboot.isChecked() ? 1 : 0);
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }

}
