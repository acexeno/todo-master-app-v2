package com.example.todomanager

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todomanager.adapter.TodoAdapter
import com.example.todomanager.api.RetrofitClient
import com.example.todomanager.databinding.ActivityMainBinding
import com.example.todomanager.dialog.AddTodoDialog
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoAdapter: TodoAdapter
    private val apiService = RetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupFab()
        loadTodos()
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(
            onTodoClick = { todo ->
                // Handle todo click - show edit dialog
                showEditTodoDialog(todo)
            },
            onTodoToggle = { todo ->
                // Handle todo toggle
                toggleTodoStatus(todo.id)
            },
            onTodoDelete = { todo ->
                // Handle todo delete
                deleteTodo(todo.id)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = todoAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddTodo.setOnClickListener {
            showAddTodoDialog()
        }
    }

    private fun loadTodos() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = apiService.getTodos()
                if (response.isSuccessful) {
                    response.body()?.let { todos ->
                        todoAdapter.submitList(todos)
                    }
                } else {
                    showError("Failed to load todos")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showAddTodoDialog() {
        AddTodoDialog(this) { title, description, dueDate, priority ->
            createTodo(title, description, dueDate, priority)
        }.show()
    }

    private fun showEditTodoDialog(todo: Todo) {
        AddTodoDialog(this, todo) { title, description, dueDate, priority ->
            updateTodo(todo.id, title, description, dueDate, priority)
        }.show()
    }

    private fun createTodo(title: String, description: String, dueDate: String, priority: Int) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val newTodo = Todo(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    priority = priority
                )
                val response = apiService.createTodo(newTodo)
                if (response.isSuccessful) {
                    loadTodos()
                } else {
                    showError("Failed to create todo")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateTodo(id: Int, title: String, description: String, dueDate: String, priority: Int) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val updatedTodo = Todo(
                    id = id,
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    priority = priority
                )
                val response = apiService.updateTodo(id, updatedTodo)
                if (response.isSuccessful) {
                    loadTodos()
                } else {
                    showError("Failed to update todo")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun toggleTodoStatus(id: Int) {
        lifecycleScope.launch {
            try {
                val response = apiService.toggleTodoStatus(id)
                if (response.isSuccessful) {
                    loadTodos()
                } else {
                    showError("Failed to toggle todo status")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun deleteTodo(id: Int) {
        lifecycleScope.launch {
            try {
                val response = apiService.deleteTodo(id)
                if (response.isSuccessful) {
                    loadTodos()
                } else {
                    showError("Failed to delete todo")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
} 