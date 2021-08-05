package com.example.todolist

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.lang.Exception
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private lateinit var todoAdapter: ToDoAdapter

    private var gson : Gson = Gson()
    private var storage : File = File("data/data/com.example.todolist/files/")
    private var itemsFile : File = File(storage, "items.json")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkPermission()) {
            createStorageDir()
            createItemsFile()
            runApp()
        } else {
            requestPermission()
        }
    }

    private fun runApp() {
        todoAdapter = ToDoAdapter(mutableListOf())

        rvToDoItems.adapter = todoAdapter
        rvToDoItems.layoutManager = LinearLayoutManager(this)

        btAddToDo.setOnClickListener {
            val todoTitle = etToDoTitle.text.toString()
            if (todoTitle.isNotEmpty()) {
                val todo = ToDo(todoTitle)
                todoAdapter.addToDo(todo)
                etToDoTitle.text.clear()
            }
        }

        btDeleteDoneTodos.setOnClickListener {
            todoAdapter.deleteDoneToDos()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), 200)
    }

    private fun createStorageDir() {
        try {
            if (!storage.exists()) {
                val success = storage.mkdirs()
                if (!success) {
                    toast(getString(R.string.create_data_dir_warning))
                }
            }
        } catch (ex : Exception) {
            toast(getString(R.string.create_data_dir_warning))
        }
    }

    private fun createItemsFile() {
        try {
            if (!itemsFile.exists()) {
                val success = itemsFile.createNewFile()
                if (!success) {
                    toast(getString(R.string.create_data_file_warning))
                }
            }
        } catch (ex : Exception) {
            toast(getString(R.string.create_data_file_warning))
        }
    }

    override fun onPause() {
        super.onPause()
        saveItems(gson.toJson(todoAdapter.todos))
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }

    private fun saveItems(json: String?) {
        try {
            PrintWriter(itemsFile).use { out -> out.println(json) }
        } catch (ex: Exception) {
            toast(getString(R.string.items_save_warning))
        }
    }

    private fun loadItems() {
        val items : MutableList<ToDo> = try {
            val json = FileReader(itemsFile).readText()
            val type = 	object : TypeToken<MutableList<ToDo>>() {}.type
            gson.fromJson(json, type)
        }  catch (ex : Exception) {
            toast(getString(R.string.items_load_warning))
            mutableListOf()
        }

        todoAdapter.clearItems()
        for (item in items) {
            todoAdapter.addToDo(item)
        }
    }

    private fun Context.toast(message: CharSequence) {
        if (message.isNotEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}