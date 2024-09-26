package com.jop.task.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jop.task.data.model.Todo
import com.jop.task.data.model.User
import com.jop.task.local.database.dao.TodoDao
import com.jop.task.local.database.dao.UserDao

@Database(entities = [User::class, Todo::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDbLocal : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun todoDao(): TodoDao
}