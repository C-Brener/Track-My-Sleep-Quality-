package com.example.android.trackmysleepquality.data.repository.implementation

import com.example.android.trackmysleepquality.data.database.dao.SleepDatabaseDao
import com.example.android.trackmysleepquality.data.database.toSleepEntity
import com.example.android.trackmysleepquality.data.model.SleepQualityModel
import com.example.android.trackmysleepquality.data.repository.interfaces.SleepQualityInterface

class SleepQualityImplementation(private val sleepDatabaseDao: SleepDatabaseDao):
    SleepQualityInterface {

    override suspend fun createSleepNight(sleepQualityModel: SleepQualityModel) {
        val sleepSave = sleepQualityModel.toSleepEntity()

        sleepDatabaseDao.insert(sleepSave)
    }

}