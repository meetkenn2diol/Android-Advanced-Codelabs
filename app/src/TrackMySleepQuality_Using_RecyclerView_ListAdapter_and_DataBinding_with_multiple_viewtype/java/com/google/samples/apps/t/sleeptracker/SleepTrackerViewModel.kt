/*
 * Copyright 2019, The Android Open Source Project
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

package com.google.samples.apps.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.*
import com.google.samples.apps.trackmysleepquality.database.SleepDatabaseDao
import com.google.samples.apps.trackmysleepquality.database.SleepNight
import com.google.samples.apps.trackmysleepquality.formatNights
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    /**
     * [tonight] is the object that holds the most recent [SleepNight]
     */
    private var tonight = MutableLiveData<SleepNight?>()

    init {
        initializeTonight()
    }

    /**
     * Get all the nights from the database
     */
    private val _nights = database.getAllNights()
    val nights: LiveData<List<SleepNight>>
        get() = _nights
    val nightsString = Transformations.map(_nights) { nights ->
        formatNights(nights, application.resources)
    }

    /**a LiveData that changes when you want the app to navigate to the SleepQualityFragment
     */
    private val _navigateToSleepQuality = MutableLiveData<SleepNight?>()
    val navigateToSleepQuality: LiveData<SleepNight?>
        get() = _navigateToSleepQuality

    /**
     * A liveData property used to navigate to the SleepDetail Screen
     */
    private val _navigateToSleepDetail = MutableLiveData<Long?>()
    val navigateToSleepDetail
        get() = _navigateToSleepDetail

    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        var night = database.getTonight()
        if (night?.endTimeMilli != night?.startTimeMilli) {
            // If the start and end times are not the same, meaning that the night has already been completed
            night = null
        }
        return night
    }

    /**
     * Function to start tracking a new SleepNight
     */
    fun onStartTracking() {
        viewModelScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            //assign newNight to tonight as the most recent SleepNight
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun insert(night: SleepNight) {
        database.insert(night)
    }

    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)

            _navigateToSleepQuality.value = oldNight
        }
    }

    private suspend fun update(night: SleepNight) {
        database.update(night)
    }

    /**
     * Resets the variable that triggers navigation.
     */
    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    /**
     * Property used to dynamically changed the enabled state of the startButton
     */
    val startButtonVisible = Transformations.map(tonight) {
        it == null
    }
    val stopButtonVisible = Transformations.map(tonight) {
        it != null
    }
    val clearButtonVisible = Transformations.map(_nights) {
        it?.isNotEmpty()
    }

    /**
     * Property used to determine when to show a SnackBar
     */
    private var _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    /**
     * Trigger Navigation to the SleepDetail Screen
     */
    fun onSleepNightClicked(id: Long) {
        _navigateToSleepDetail.value = id
    }

    fun onSleepDetailNavigated() {
        _navigateToSleepDetail.value = null
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
            tonight.value = null
            _showSnackbarEvent.value = true
        }
    }

    private suspend fun clear() {
        database.clear()
    }
}


