package com.example.android.trackmysleepquality.data.repository.interfaces

import com.example.android.trackmysleepquality.data.model.SleepQualityModel

interface SleepQualityInterface {

    suspend fun createSleepNight(sleepQualityModel: SleepQualityModel)
}