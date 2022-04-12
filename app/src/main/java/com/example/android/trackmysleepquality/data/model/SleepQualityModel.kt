package com.example.android.trackmysleepquality.data.model

data class SleepQualityModel(

    var nightId: Long,


    val startTimeMilli: Long,


    var endTimeMilli: Long,


    var sleepQuality: Int
)