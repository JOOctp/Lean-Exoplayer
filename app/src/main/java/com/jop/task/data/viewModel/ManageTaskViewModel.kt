package com.jop.task.data.viewModel

import androidx.lifecycle.ViewModel
import com.jop.task.data.model.Todo
import com.jop.task.data.model.User
import com.jop.task.data.repository.TodoRepository
import com.jop.task.data.repository.UserRepository
import com.jop.task.local.AppData

class ManageTaskViewModel(private val appData: AppData, private val repo: TodoRepository): ViewModel(){

    fun insertTodo(todo: Todo){
        todo.createdBy = appData.getToken() ?: ""
        repo.insertTodoLocal(todo)
    }

    fun updateTodo(todo: Todo){
        todo.createdBy = appData.getToken() ?: ""
        repo.updateTodo(todo)
    }

    fun deleteTodo(todo: Todo){
        repo.deleteTodo(todo)
    }
}