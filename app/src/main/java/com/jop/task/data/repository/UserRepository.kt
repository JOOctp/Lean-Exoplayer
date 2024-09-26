package com.jop.task.data.repository

import com.jop.task.data.model.User
import com.jop.task.local.database.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserRepository(private val userDao: UserDao) {

    fun insertUserLocal(user: User) = runBlocking {
        this.launch(Dispatchers.IO){
            userDao.insert(user)
        }
    }

    fun getDetailUser(token: String) = userDao.getDetailUser(token)

    fun updateUser(token: String, name: String, photo: ByteArray) = runBlocking {
        this.launch(Dispatchers.IO){
            userDao.updateUser(token, name, photo)
        }
    }

    fun deleteUser(user: User) = runBlocking {
        this.launch(Dispatchers.IO){
            userDao.deleteUser(user)
        }
    }
}