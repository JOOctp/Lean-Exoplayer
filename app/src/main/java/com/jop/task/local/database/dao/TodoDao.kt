package com.jop.task.local.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jop.task.data.model.Todo

@Dao
interface TodoDao {
    @Query("SELECT * FROM `todo` WHERE `created_by` =:tokenUser ORDER BY `created_at` DESC")
    fun getTodoLocal(tokenUser: String): LiveData<MutableList<Todo>>

    @Query("SELECT * FROM `todo` WHERE id =:id")
    fun getTodoDetail(id: Int): LiveData<Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTodo(todo: Todo)

    @Query("DELETE FROM `todo`")
    suspend fun deleteALl()

    @Delete
    fun delete(product: Todo)
}