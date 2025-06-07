package com.example.todomanager

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todomanager.api.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response

class TestConnectionActivity : AppCompatActivity() {
    private lateinit var textStatus: TextView
    private lateinit var buttonTest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_connection)

        textStatus = findViewById(R.id.textStatus)
        buttonTest = findViewById(R.id.buttonTest)

        buttonTest.setOnClickListener {
            testConnection()
        }
    }

    private fun testConnection() {
        textStatus.text = "Testing connection..."
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.testConnection()
                if (response.isSuccessful) {
                    val result = response.body()
                    textStatus.text = "Connection successful!\n\n" +
                            "Message: ${result?.get("message")}\n" +
                            "Timestamp: ${result?.get("timestamp")}"
                } else {
                    textStatus.text = "Connection failed: ${response.code()}"
                }
            } catch (e: Exception) {
                textStatus.text = "Error: ${e.message}"
                Toast.makeText(this@TestConnectionActivity, 
                    "Connection error: ${e.message}", 
                    Toast.LENGTH_LONG).show()
            }
        }
    }
} 