package com.example.todomanager.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.example.todomanager.R
import com.example.todomanager.data.Todo
import com.example.todomanager.databinding.DialogAddTodoBinding
import java.text.SimpleDateFormat
import java.util.*

class AddTodoDialog(
    private val context: Context,
    private val existingTodo: Todo? = null,
    private val onSave: (title: String, description: String, dueDate: String, priority: Int) -> Unit
) {
    private val binding = DialogAddTodoBinding.inflate(LayoutInflater.from(context))
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        setupViews()
        if (existingTodo != null) {
            populateFields(existingTodo)
        }
    }

    private fun setupViews() {
        // Setup priority spinner
        val priorities = arrayOf("Low", "Medium", "High")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriority.adapter = adapter

        // Setup date picker
        binding.buttonDate.setOnClickListener {
            showDatePicker()
        }

        // Set initial date
        updateDateButton()
    }

    private fun populateFields(todo: Todo) {
        binding.apply {
            editTextTitle.setText(todo.title)
            editTextDescription.setText(todo.description)
            calendar.time = dateFormatter.parse(todo.dueDate) ?: Date()
            updateDateButton()
            spinnerPriority.setSelection(todo.priority - 1)
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                updateDateButton()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateButton() {
        binding.buttonDate.text = dateFormatter.format(calendar.time)
    }

    fun show() {
        AlertDialog.Builder(context)
            .setTitle(if (existingTodo == null) "Add Todo" else "Edit Todo")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val title = binding.editTextTitle.text.toString()
                val description = binding.editTextDescription.text.toString()
                val dueDate = dateFormatter.format(calendar.time)
                val priority = binding.spinnerPriority.selectedItemPosition + 1

                if (title.isNotBlank()) {
                    onSave(title, description, dueDate, priority)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
} 