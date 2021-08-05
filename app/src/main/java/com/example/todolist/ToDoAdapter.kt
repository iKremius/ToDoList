package com.example.todolist

import android.annotation.SuppressLint
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.*

class ToDoAdapter(
    val todos: MutableList<ToDo>
) : RecyclerView.Adapter<ToDoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        return  ToDoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false))
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentToDo = todos[position]
        holder.itemView.apply {
            tvToDoTitle.text = currentToDo.title
            cbDone.isChecked = currentToDo.isChecked
            toogleStrikeThrough(tvToDoTitle, currentToDo.isChecked)
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toogleStrikeThrough(tvToDoTitle, isChecked)
                currentToDo.isChecked = !currentToDo.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    fun addToDo(todo: ToDo) {
        todos.add(todo)
        notifyItemInserted(todos.size -1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteDoneToDos() {
        todos.removeAll { todo ->
            todo.isChecked
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearItems() {
        todos.removeAll(todos)
        notifyDataSetChanged()
    }

    private fun toogleStrikeThrough(tvToDoTitle: TextView, isChecked: Boolean) {
        if(isChecked)
        {
            tvToDoTitle.paintFlags = tvToDoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tvToDoTitle.paintFlags = tvToDoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}