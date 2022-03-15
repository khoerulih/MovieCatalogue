package com.example.moviecatalogue

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

class SettingPreference: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var DAILY: String
    private lateinit var RELEASE: String

    private lateinit var isDailyPreference: SwitchPreference
    private lateinit var isReleasePreference: SwitchPreference

    private lateinit var alarmReceiver: AlarmReceiver

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting)

        init()
        setSummaries()

        alarmReceiver = AlarmReceiver()
    }

    private fun init(){
        DAILY = resources.getString(R.string.key_daily)
        RELEASE = resources.getString(R.string.key_release)

        isDailyPreference = findPreference<SwitchPreference>(DAILY) as SwitchPreference
        isReleasePreference = findPreference<SwitchPreference>(RELEASE) as SwitchPreference
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun setSummaries(){
        val sh = preferenceManager.sharedPreferences
        isDailyPreference.isChecked = sh.getBoolean(DAILY, false)
        isReleasePreference.isChecked = sh.getBoolean(RELEASE, false)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == DAILY){
            isDailyPreference.isChecked = sharedPreferences.getBoolean(DAILY, false)
            if(isDailyPreference.isChecked){
               Log.d("Debug : ", "Daily Checked")
                alarmReceiver.setDailyReminder(requireContext(), AlarmReceiver.TYPE_DAILY, "07:00")
            }else{
                Log.d("Debug : ", "Daily Unchecked")
                alarmReceiver.cancelReminder(requireContext(), AlarmReceiver.TYPE_DAILY)
            }
        }

        if (key == RELEASE){
            isReleasePreference.isChecked = sharedPreferences.getBoolean(RELEASE, false)
            if(isReleasePreference.isChecked){
                Log.d("Debug : ", "Release Checked")
                alarmReceiver.setReleaseReminder(requireContext(), AlarmReceiver.TYPE_RELEASE, "08:00")
            }else{
                Log.d("Debug : ", "Release Unchecked")
                alarmReceiver.cancelReminder(requireContext(), AlarmReceiver.TYPE_RELEASE)
            }
        }
    }

}