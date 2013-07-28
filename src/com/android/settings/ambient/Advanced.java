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

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class Advanced extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String KILL_APP_LONGPRESS_BACK = "kill_app_longpress_back";
    private static final String VOLUME_STEPS = "volume_steps";

    private CheckBoxPreference mKillAppLongpressBack;
    private ListPreference mVolumeSteps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ambient_advanced_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mKillAppLongpressBack = (CheckBoxPreference) findPreference(KILL_APP_LONGPRESS_BACK);

        mVolumeSteps = (ListPreference) findPreference(VOLUME_STEPS);
        mVolumeSteps.setOnPreferenceChangeListener(this);
        int volumeSteps = Settings.System.getInt(getContentResolver(),
                Settings.System.AUDIO_VOLUME_STEPS, 15);
        mVolumeSteps.setValue(Integer.toString(volumeSteps));
        mVolumeSteps.setSummary(mVolumeSteps.getEntry());

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mKillAppLongpressBack) {
            writeKillAppLongpressBackOptions();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (preference == mVolumeSteps) {
            final int value = Integer.valueOf((String) objValue);
            final int index = mVolumeSteps.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.AUDIO_VOLUME_STEPS, value);
            mVolumeSteps.setSummary(mVolumeSteps.getEntries()[index]);
            PowerManager pm = (PowerManager) getActivity()
                    .getSystemService(Context.POWER_SERVICE);
            pm.reboot("Resetting Media Volume...");
        }

        return true;
    }

    private void writeKillAppLongpressBackOptions() {
        Settings.Secure.putInt(getActivity().getContentResolver(),
                Settings.Secure.KILL_APP_LONGPRESS_BACK,
                mKillAppLongpressBack.isChecked() ? 1 : 0);
    }

    private void updateKillAppLongpressBackOptions() {
        mKillAppLongpressBack.setChecked(Settings.Secure.getInt(
            getActivity().getContentResolver(), Settings.Secure.KILL_APP_LONGPRESS_BACK, 0) != 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateKillAppLongpressBackOptions();
    }
}
