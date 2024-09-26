package com.jop.task.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "subject")
    val subject: String = "",
    @ColumnInfo(name = "desc")
    val desc: String = "",
    @ColumnInfo(name = "status")
    val status: String = "",
    @ColumnInfo(name = "remainder")
    val remainder: Int = 0,
    @ColumnInfo(name = "created_by")
    var createdBy: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    @ColumnInfo(name = "photo", typeAffinity = ColumnInfo.BLOB)
    val photo: ByteArray = byteArrayOf()): Serializable