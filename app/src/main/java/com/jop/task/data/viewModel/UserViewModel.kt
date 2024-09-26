package com.jop.task.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jop.task.data.model.Todo
import com.jop.task.data.model.User
import com.jop.task.data.repository.UserRepository
import com.jop.task.local.AppData

class UserViewModel(private val appData: AppData, private val repo: UserRepository): ViewModel(){

    fun insertUser(user: User){
        appData.setToken(user.token)
        repo.insertUserLocal(user)
    }

    fun getDetailUser(): LiveData<MutableList<User>> {
        return repo.getDetailUser(appData.getToken() ?: "")
    }

    fun updateUser(name: String, photo: ByteArray = byteArrayOf()){
        repo.updateUser(appData.getToken() ?: "", name, photo)
    }
}