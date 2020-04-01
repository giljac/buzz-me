package com.technologil21.buzzme

import android.app.Application

class BuzzMeApp : Application() {

    override fun onCreate() {
//        Log.d("toto", "app")
        super.onCreate()
        AppPreferences.init(this)
    }
}