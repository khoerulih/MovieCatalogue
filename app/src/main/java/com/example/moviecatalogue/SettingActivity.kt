package com.example.moviecatalogue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        supportActionBar?.title = "Settings"
        supportFragmentManager.beginTransaction().add(R.id.setting, SettingPreference()).commit()
    }
}
