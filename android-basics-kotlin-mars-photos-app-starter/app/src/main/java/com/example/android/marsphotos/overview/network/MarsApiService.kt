package com.example.android.marsphotos.overview.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create()) // 얻은 데이터로 할일을 retrofit에 알림.
    .baseUrl(BASE_URL)
    .build()

interface MarsApiService {
    @GET("photos")
    suspend fun getPhotos(): String // suspend를 붙이면 코루틴 내에서 호출 가능
}

object MarsApi {
    // retrofit 객체 만들기. create()가 비싼 함수.
    val retrofitService: MarsApiService by lazy { retrofit.create(MarsApiService::class.java) }
}