package com.example.advizors

import android.app.Application
import android.content.Context

class Advizors: Application() {
    object Globals {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.appContext = applicationContext
    }
}