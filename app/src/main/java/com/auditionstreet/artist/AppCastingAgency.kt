package com.auditionstreet.artist

import android.app.Application
import com.auditionstreet.artist.storage.preference.Preferences
import com.facebook.FacebookSdk
import dagger.hilt.android.HiltAndroidApp

@Suppress("DEPRECATION")
@HiltAndroidApp
class AppCastingAgency : Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext)
    }
}






