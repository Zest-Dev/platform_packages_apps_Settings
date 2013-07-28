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
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;

public class NavbarDimenSettings extends SettingsPreferenceFragment implements
                   OnPreferenceChangeListener {
    private static final String TAG = "NavbarDimenSettings";

    private static final String PREF_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String PREF_NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";
    private static final String PREF_NAVIGATION_BAR_WIDTH = "navigation_bar_width";
    private static final String KEY_DIMEN_OPTIONS = "navbar_dimen";

    private static final int MENU_RESET = Menu.FIRST;

    private boolean mCheckPreferences;

    ListPreference mNavigationBarHeight;
    ListPreference mNavigationBarHeightLandscape;
    ListPreference mNavigationBarWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    private PreferenceScreen refreshSettings() {
        mCheckPreferences = false;
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.ambient_navbar_dimen_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mNavigationBarHeight = (ListPreference) findPreference(PREF_NAVIGATION_BAR_HEIGHT);
        mNavigationBarHeight.setOnPreferenceChangeListener(this);

        mNavigationBarHeightLandscape = (ListPreference) findPreference(PREF_NAVIGATION_BAR_HEIGHT_LANDSCAPE);
        mNavigationBarHeightLandscape.setOnPreferenceChangeListener(this);

        mNavigationBarWidth = (ListPreference) findPreference(PREF_NAVIGATION_BAR_WIDTH);
        PreferenceCategory dimenCategory = (PreferenceCategory) findPreference(KEY_DIMEN_OPTIONS);
        mNavigationBarWidth.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
        mCheckPreferences = true;
        return prefs;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.navbar_reset)
                .setIcon(R.drawable.ic_settings_backup) // use the backup icon
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.navbar_reset);
        alertDialog.setMessage(R.string.navbar_reset_dimensions_message);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int height = mapChosenDpToPixels(48);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE,
                        height);
                height = mapChosenDpToPixels(48);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NAVIGATION_BAR_HEIGHT,
                        height);
                height = mapChosenDpToPixels(42);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NAVIGATION_BAR_WIDTH,
                        height);
                mNavigationBarHeight.setValue("48");
                mNavigationBarHeightLandscape.setValue("48");
                mNavigationBarWidth.setValue("42");
                refreshSettings();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
         final String key = preference.getKey();
         if (preference == mNavigationBarWidth) {
            String objVal = (String) objValue;
            int dp = Integer.parseInt(objVal);
            int width = mapChosenDpToPixels(dp);
            Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_WIDTH,
                    width);
            return true;
        } else if (preference == mNavigationBarHeight) {
            String objVal = (String) objValue;
            int dp = Integer.parseInt(objVal);
            int height = mapChosenDpToPixels(dp);
            Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_HEIGHT,
                    height);
            return true;
        } else if (preference == mNavigationBarHeightLandscape) {
            String objVal = (String) objValue;
            int dp = Integer.parseInt(objVal);
            int height = mapChosenDpToPixels(dp);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE,
                    height);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public int mapChosenDpToPixels(int dp) {
        switch (dp) {
            case 48:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_48);
            case 44:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_44);
            case 42:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_42);
            case 40:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_40);
            case 36:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_36);
            case 30:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_30);
            case 24:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_24);
            case 0:
                return 0;
        }
        return -1;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
