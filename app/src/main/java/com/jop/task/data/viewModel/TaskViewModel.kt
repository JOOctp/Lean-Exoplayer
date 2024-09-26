package com.jop.task.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jop.task.data.model.Todo
import com.jop.task.data.model.User
import com.jop.task.data.repository.TodoRepository
import com.jop.task.data.repository.UserRepository
import com.jop.task.local.AppData

class TaskViewModel(private val appData: AppData, private val repo: TodoRepository): ViewModel(){

    fun getTodoLocal(): LiveData<MutableList<Todo>> {
        return repo.getTodoLocal(appData.getToken() ?: "")
    }

    fun deleteALl() {
        repo.deleteALl()
    }

}