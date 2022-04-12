/*
 * Copyright 2018, The Android Open Source Project
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

package com.example.android.trackmysleepquality.ui.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.data.database.SleepNight
import com.example.android.trackmysleepquality.data.database.dao.SleepDatabaseDao
import com.example.android.trackmysleepquality.utils.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application) :
    AndroidViewModel(application) {

    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var tonight = MutableLiveData<SleepNight?>()
    private val nights = database.getAllNights()

    val nightsString  = Transformations.map(nights){nights->
        formatNights(nights, application.resources)
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        uiScope.launch { 
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO){
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli){
                night = null
            }
            night
        }
    }

    fun onStartTracking(){
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }
    private suspend fun insert(night: SleepNight){
        withContext(Dispatchers.IO){
            database.insert(night)
        }
    }

    fun onStopTracking(){
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch

            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)
        }
    }
    private suspend fun update(oldNight: SleepNight){
        withContext(Dispatchers.IO){
            database.update(night=oldNight)
        }
    }

    fun onClear(){
        uiScope.launch {
            clear()
            tonight.value = null
        }
    }
    private suspend fun clear(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }


    /*
    * O job nos permite cancelar todas as corroutines iniciadas neste view model
    * quando o view model for destruido - line 32.37
    * //
    * O dispatchers.Main indica que as corrotinas iniciadas pelo UI
    * serão executadas na main thread - line 41
    *
    * Variável tonight responsável por armazenar a noite atual
    * que está sendo computada - line 42
    *
    *Constante que irá obter tofas as noites no banco de dados - line 43
    *
    * Criando e inicializando a função que será responsável
    * por iniciar a noite - line 46.52
    *
    * Dentro da função estamos utilizando uma corroutines para obter os dados
    * sem bloquear a main thread
    * */

}

