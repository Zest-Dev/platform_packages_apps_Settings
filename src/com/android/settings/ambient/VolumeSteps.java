/*
 * Copyright (C) 2013 AMBIENT
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

import android.app.AlertDialog;
import android.content.res.Resources;
import android.content.DialogInterface;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;

public class VolumeSteps extends SettingsPreferenceFragment implements
                   OnPreferenceChangeListener {
    private static final String TAG = "VolumeSteps";

    private static final String VOLUME_STEPS_MEDIA = "volume_steps_media";
    private static final String VOLUME_STEPS_CALL = "volume_steps_call";

    private ListPreference mVolumeStepsMedia;
    private ListPreference mVolumeStepsCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ambient_volume_steps);

        PreferenceScreen prefSet = getPreferenceScreen();

        mVolumeStepsMedia = (ListPreference) findPreference(VOLUME_STEPS_MEDIA);
        mVolumeStepsMedia.setOnPreferenceChangeListener(this);
        int volumeStepsMedia = Settings.System.getInt(getContentResolver(),
                Settings.System.AUDIO_VOLUME_STEPS_MEDIA, 15);
        mVolumeStepsMedia.setValue(Integer.toString(volumeStepsMedia));
        mVolumeStepsMedia.setSummary(mVolumeStepsMedia.getEntry());

        mVolumeStepsCall = (ListPreference) findPreference(VOLUME_STEPS_CALL);
        mVolumeStepsCall.setOnPreferenceChangeListener(this);
        int volumeStepsCall = Settings.System.getInt(getContentResolver(),
                Settings.System.AUDIO_VOLUME_STEPS_CALL, 5);
        mVolumeStepsCall.setValue(Integer.toString(volumeStepsCall));
        mVolumeStepsCall.setSummary(mVolumeStepsCall.getEntry());

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (preference == mVolumeStepsMedia) {
            final int value = Integer.valueOf((String) objValue);
            final int index = mVolumeStepsMedia.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.AUDIO_VOLUME_STEPS_MEDIA, value);
            mVolumeStepsMedia.setSummary(mVolumeStepsMedia.getEntries()[index]);
            PowerManager pm = (PowerManager) getActivity()
                    .getSystemService(Context.POWER_SERVICE);
            pm.reboot("Resetting Media Volume...");
        } else if (preference == mVolumeStepsCall) {
            final int value = Integer.valueOf((String) objValue);
            final int index = mVolumeStepsCall.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.AUDIO_VOLUME_STEPS_CALL, value);
            mVolumeStepsCall.setSummary(mVolumeStepsCall.getEntries()[index]);
            PowerManager pm = (PowerManager) getActivity()
                    .getSystemService(Context.POWER_SERVICE);
            pm.reboot("Resetting Call Volume...");
        }
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
