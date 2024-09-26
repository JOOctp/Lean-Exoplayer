package com.jop.task.data.model

import java.io.Serializable

data class DataCalendar(
    val date: String = "",
    var todo: List<Todo> = mutableListOf()
): Serializable