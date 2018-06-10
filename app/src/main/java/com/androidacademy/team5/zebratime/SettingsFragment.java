package com.androidacademy.team5.zebratime;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.preference);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        Preference workTime = getPreferenceScreen().getPreference(0);
        String valueWorkTime = sharedPreferences.getString(workTime.getKey(), getString(R.string.pref_default_work_time));
        workTime.setSummary(valueWorkTime);

        Preference shortBreak = getPreferenceScreen().getPreference(1);
        String valueShortBreak = sharedPreferences.getString(shortBreak.getKey(), getString(R.string.pref_default_short_rest));
        shortBreak.setSummary(valueShortBreak);

        Preference longBreak = getPreferenceScreen().getPreference(2);
        String valueLongBreak = sharedPreferences.getString(longBreak.getKey(), getString(R.string.pref_default_long_rest));
        longBreak.setSummary(valueLongBreak);

        Preference longBreakInterval = getPreferenceScreen().getPreference(3);
        String valueLongBreakInterval = sharedPreferences.getString(longBreakInterval.getKey(), getString(R.string.pref_default_long_break_interval));
        longBreakInterval.setSummary(valueLongBreakInterval);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference workTime = findPreference(key);
        if (workTime != null){
            String valueWorkTime = sharedPreferences.getString(key,getString(R.string.pref_default_work_time));
            workTime.setSummary(valueWorkTime);
        }

        Preference shortBreak = findPreference(key);
        if (shortBreak != null){
            String valueShortBreak = sharedPreferences.getString(key,getString(R.string.pref_default_short_rest));
            shortBreak.setSummary(valueShortBreak);
        }

        Preference longBreak = findPreference(key);
        if (longBreak != null){
            String valueLongBreak = sharedPreferences.getString(key,getString(R.string.pref_default_long_rest));
            longBreak.setSummary(valueLongBreak);
        }

        Preference longBreakInterval = findPreference(key);
        if (longBreakInterval != null){
            String valueLongBreakInterval = sharedPreferences.getString(key,getString(R.string.pref_default_long_break_interval));
            longBreakInterval.setSummary(valueLongBreakInterval);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
