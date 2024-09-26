package com.jop.task.data.repository

import android.util.Log
import com.jop.task.data.model.Todo
import com.jop.task.local.database.dao.TodoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoRepository(private val todoDao: TodoDao) {

    fun getTodoDetail(id: Int) = runBlocking {
        this.launch(Dispatchers.IO){
            todoDao.getTodoDetail(id)
        }
    }

    fun getTodoLocal(token: String) = todoDao.getTodoLocal(token)

    fun insertTodoLocal(todo: Todo) = runBlocking {
        this.launch(Dispatchers.IO){
            todoDao.insert(todo)
        }
    }

    fun deleteALl() = runBlocking {
        this.launch(Dispatchers.IO){
            todoDao.deleteALl()
        }
    }

    fun updateTodo(todo: Todo) = runBlocking {
        this.launch(Dispatchers.IO){
            todoDao.updateTodo(todo)
            Log.e("TEST", "${todo}")
        }
    }

    fun deleteTodo(todo: Todo) = runBlocking {
        this.launch(Dispatchers.IO){
            todoDao.delete(todo)
        }
    }
}