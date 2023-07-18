package com.example.retrofitlesson.network

import com.example.retrofitlesson.models.TodoGetResponse
import com.example.retrofitlesson.models.TodoPostRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {
    @GET("plan/")
    fun getData():Call<List<TodoGetResponse>>

    @POST("plan/")
    fun postData(@Body request: TodoPostRequest):Call<TodoGetResponse>

    @PUT("plan/{id}/")
    fun editTodo(@Path("id") id:Int,@Body todoGetResponse: TodoGetResponse):Call<TodoGetResponse>

    @DELETE("plan/{id}/")
    fun deleteTodo(@Path("id")id: Int):Call<TodoGetResponse>

}