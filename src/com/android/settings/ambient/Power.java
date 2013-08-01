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
    private static final String KEY_EXPANDED_DESKTOP = "power_menu_expanded_desktop";

    private CheckBoxPreference mReboot;
    private ListPreference mExpandedDesktop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ambient_power_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mReboot = (CheckBoxPreference) findPreference(REBOOT_IN_POWER_MENU);
        mReboot.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.Secure.REBOOT_IN_POWER_MENU, 0) == 1));

        mExpandedDesktop = (ListPreference) prefSet.findPreference(KEY_EXPANDED_DESKTOP);
        mExpandedDesktop.setOnPreferenceChangeListener(this);
        int expandedDesktopValue = Settings.System.getInt(getContentResolver(),
                Settings.System.EXPANDED_DESKTOP_MODE, 0);
        mExpandedDesktop.setValue(String.valueOf(expandedDesktopValue));
        mExpandedDesktop.setSummary(mExpandedDesktop.getEntries()[expandedDesktopValue]);

        updateReboot();

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mExpandedDesktop) {
            int expandedDesktopValue = Integer.valueOf((String) objValue);
            int index = mExpandedDesktop.findIndexOfValue((String) objValue);
            if (expandedDesktopValue == 0) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 0);
                // Disable expanded desktop if enabled
                Settings.System.putInt(getContentResolver(),
                        Settings.System.EXPANDED_DESKTOP_STATE, 0);
            } else {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
            }
            Settings.System.putInt(getContentResolver(),
                    Settings.System.EXPANDED_DESKTOP_MODE, expandedDesktopValue);
            mExpandedDesktop.setSummary(mExpandedDesktop.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mReboot) {
            updateRebootOptions();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateRebootOptions() {
        Settings.Secure.putInt(getActivity().getContentResolver(),
                Settings.Secure.REBOOT_IN_POWER_MENU,
                mReboot.isChecked() ? 1 : 0);
    }

    private void updateReboot() {
        mReboot.setChecked(Settings.Secure.getInt(
            getActivity().getContentResolver(), Settings.Secure.REBOOT_IN_POWER_MENU, 0) != 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateReboot();
    }

}
