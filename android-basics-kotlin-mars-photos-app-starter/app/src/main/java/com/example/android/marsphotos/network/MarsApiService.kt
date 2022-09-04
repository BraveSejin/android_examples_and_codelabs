package com.example.android.marsphotos.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com"

private val moshi = Moshi.Builder() // json converter
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi)) // 얻은 데이터로 할일을 retrofit에 알림.
    .baseUrl(BASE_URL)
    .build()

interface MarsApiService {
    @GET("photos")
    suspend fun getPhotos(): Response<List<MarsPhoto>> // suspend를 붙이면 코루틴 내에서 호출 가능
}

object MarsApi {
    // retrofit 객체 만들기. create()가 비싼 함수.
    val retrofitService: MarsApiService by lazy { retrofit.create(MarsApiService::class.java) }
}