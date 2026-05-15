package com.example.nammasantheledger

import android.app.Application
import com.example.nammasantheledger.ui.ThemePreferenceManager

class NammaSantheApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemePreferenceManager(this).applySavedTheme()
    }
}
