package com.keplersegg.myself.Interfaces

import com.keplersegg.myself.Room.AppDatabase
import com.keplersegg.myself.Room.Entity.TaskEntry

interface IGetTasksHost : IHttpProvider {

    fun onGetTasksSuccess(items: List<TaskEntry>)

    fun onGetTasksError(message: String)

    fun getAppDB() : AppDatabase
}