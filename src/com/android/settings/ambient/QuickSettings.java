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
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.DialogInterface;
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

public class QuickSettings extends SettingsPreferenceFragment implements
                   OnPreferenceChangeListener {
    private static final String TAG = "QuickSettings";

    private static final String QUICK_PULLDOWN = "quick_pulldown";

    ListPreference mQuickPulldown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ambient_quick_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        ContentResolver resolver = getActivity().getContentResolver();

        mQuickPulldown = (ListPreference) prefSet.findPreference(QUICK_PULLDOWN);
        mQuickPulldown.setOnPreferenceChangeListener(this);
        int statusQuickPulldown = Settings.System.getInt(resolver, Settings.System.QS_QUICK_PULLDOWN, 0);
        mQuickPulldown.setValue(String.valueOf(statusQuickPulldown));
        updatePulldownSummary();

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mQuickPulldown) {
            int statusQuickPulldown = Integer.valueOf((String) objValue);
            Settings.System.putInt(resolver, Settings.System.QS_QUICK_PULLDOWN,
                    statusQuickPulldown);
            updatePulldownSummary();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updatePulldownSummary() {
        ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
        int summaryId;
        int directionId;
        summaryId = R.string.summary_quick_pulldown;
        String value = Settings.System.getString(resolver, Settings.System.QS_QUICK_PULLDOWN);
        String[] pulldownArray = getResources().getStringArray(R.array.quick_pulldown_values);
        if (pulldownArray[0].equals(value)) {
            directionId = R.string.quick_pulldown_off;
            mQuickPulldown.setValueIndex(0);
            mQuickPulldown.setSummary(getResources().getString(directionId));
        } else if (pulldownArray[1].equals(value)) {
            directionId = R.string.quick_pulldown_position;
            mQuickPulldown.setValueIndex(1);
            mQuickPulldown.setSummary(getResources().getString(directionId)
                    + " " + getResources().getString(summaryId));
        } else {
            directionId = R.string.quick_pulldown_position;
            mQuickPulldown.setValueIndex(2);
            mQuickPulldown.setSummary(getResources().getString(directionId)
                    + " " + getResources().getString(summaryId));
        }
    }

}
