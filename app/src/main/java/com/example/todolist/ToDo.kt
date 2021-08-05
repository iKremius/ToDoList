package com.ikremius.todolist

data class ToDo(
    val title: String,
    var isChecked: Boolean = false
)