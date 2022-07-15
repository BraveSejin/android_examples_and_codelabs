package com.example.android.marsphotos.network

import com.squareup.moshi.Json

// 이게 있어야 파싱결과 저장 가능
// json 파일 키 이름름과 변수명을 다르게 사용할 경ㅇ우
data class MarsPhoto(val id: String, @Json(name= "img_src") val imgSrcUrl: String)
