package com.example.retrofitlesson.network

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL="https://plans1.pythonanywhere.com/"

    //val BASE_URL="https://jsonplaceholder.typicode.com/posts"
    private fun getRetrofit(context: Context): Retrofit {
        //logda ko'rib turish uchun
        val httpLoginInterceptor= HttpLoggingInterceptor()

        //log da body qismini chiqarip turadi
        httpLoginInterceptor.level= HttpLoggingInterceptor.Level.BODY

        val chuckInterceptor= ChuckInterceptor(context)

        val okHttpClient= OkHttpClient.Builder()
            .addInterceptor(httpLoginInterceptor)
            .build()


        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    fun getRetrofitService(context: Context):ApiService{
        return getRetrofit(context).create(ApiService::class.java)
    }
}