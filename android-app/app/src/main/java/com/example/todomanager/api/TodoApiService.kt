package com.example.todomanager.api

import com.example.todomanager.data.Todo
import retrofit2.Response
import retrofit2.http.*

interface TodoApiService {
    @GET("test/")
    suspend fun testConnection(): Response<Map<String, String>>

    @GET("todos/")
    suspend fun getTodos(): Response<List<Todo>>
    
    @GET("todos/{id}")
    suspend fun getTodo(@Path("id") id: Int): Response<Todo>
    
    @POST("todos/")
    suspend fun createTodo(@Body todo: Todo): Response<Todo>
    
    @PUT("todos/{id}")
    suspend fun updateTodo(@Path("id") id: Int, @Body todo: Todo): Response<Todo>
    
    @DELETE("todos/{id}")
    suspend fun deleteTodo(@Path("id") id: Int): Response<Unit>
    
    @PATCH("todos/{id}/toggle")
    suspend fun toggleTodoStatus(@Path("id") id: Int): Response<Todo>
} 