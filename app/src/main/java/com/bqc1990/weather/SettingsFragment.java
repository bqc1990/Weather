package com.bqc1990.weather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.weather_preference);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        int count = preferenceScreen.getPreferenceCount();
        for(int i = 0;i < count;i++){
            Preference p = preferenceScreen.getPreference(i);
            if(p instanceof EditTextPreference){
                EditTextPreference editTextPreference = (EditTextPreference) p;
                String value = sharedPreferences.getString(editTextPreference.getKey(), "");
                editTextPreference.setSummary(value);
            }else if(p instanceof ListPreference){
                ListPreference listPreference = (ListPreference) p;
                String value = sharedPreferences.getString(listPreference.getKey(), "");
                int index = listPreference.findIndexOfValue(value);
                listPreference.setSummary(listPreference.getEntries()[index]);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();
        Weather.startImmediately(activity);
        Preference p = findPreference(key);
        if(p instanceof EditTextPreference){
            EditTextPreference editTextPreference = (EditTextPreference) p;
            String value = sharedPreferences.getString(editTextPreference.getKey(), "");
            editTextPreference.setSummary(value);
        }else if(p instanceof ListPreference){
            ListPreference listPreference = (ListPreference) p;
            String value = sharedPreferences.getString(listPreference.getKey(), "");
            int index = listPreference.findIndexOfValue(value);
            listPreference.setSummary(listPreference.getEntries()[index]);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
