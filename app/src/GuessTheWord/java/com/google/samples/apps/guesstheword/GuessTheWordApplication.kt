package com.google.samples.apps.guesstheword

import android.app.Application
import android.content.res.Resources
import timber.log.Timber

class GuessTheWordApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}