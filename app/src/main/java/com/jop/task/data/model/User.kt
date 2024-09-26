package com.jop.task.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "token")
    val token: String = "",
    @ColumnInfo(name = "email")
    val email: String = "",
    @ColumnInfo(name = "photo", typeAffinity = ColumnInfo.BLOB)
    val photo: ByteArray = byteArrayOf()): Serializable