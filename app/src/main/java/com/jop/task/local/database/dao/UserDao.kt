package com.jop.task.local.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jop.task.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM `user` WHERE `token` =:token")
    fun getDetailUser(token: String): LiveData<MutableList<User>>

    @Query("UPDATE `user` SET `name` =:name, `photo` =:photo WHERE `token` =:token")
    fun updateUser(token: String, name: String, photo: ByteArray)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("DELETE FROM `user`")
    suspend fun deleteALl()

    @Delete
    fun deleteUser(customer: User)
}