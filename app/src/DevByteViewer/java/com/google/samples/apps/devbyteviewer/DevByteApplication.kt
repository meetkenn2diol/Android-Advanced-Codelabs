/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.devbyteviewer

import android.app.Application
import android.os.Build
import androidx.work.*
import com.google.samples.apps.devbyteviewer.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via WorkManager.
 * WorkManager is initialized only when needed using Configuration.Provider.
 */
class DevByteApplication : Application(), Configuration.Provider {
    /**
     *  Performing a long-running operation in onCreate() might block the UI thread and cause a delay in loading the app. To avoid this problem, run tasks such as initializing Timber and scheduling WorkManager off the main thread, inside a coroutine.
     */
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    /**
     * A method called delayedInit() to start a coroutine.
     */
    private fun delayedInit() {
        applicationScope.launch {
            WorkManager.initialize(
                this@DevByteApplication,
                this@DevByteApplication.workManagerConfiguration
            )
            Timber.plant(Timber.DebugTree())
            setupRecurringWork()
        }
    }

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }

    /**
     * Setup WorkManager background job to 'fetch' new network data daily.
     */
    private fun setupRecurringWork() {
        val constraints =
              Constraints.Builder()
                  .setRequiredNetworkType(NetworkType.CONNECTED)
                  .setRequiresBatteryNotLow(true)
                  .setRequiresCharging(true)
                  .apply {
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                          setRequiresDeviceIdle(true)
                      }
                  }
                  .build()

        //create and initialize a periodic work request to run once a day
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints).build()


       // WorkManager.getInstance(applicationContext).cancelAllWork()
        Timber.d("Periodic Work request for sync is scheduled")
        //schedule the work using the enqueueUniquePeriodicWork() method
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

}
