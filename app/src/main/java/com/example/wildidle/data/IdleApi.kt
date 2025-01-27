package com.example.wildidle.data

import com.example.wildidle.model.SignInDTO
import com.example.wildidle.model.StringResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IdleApi {
    @POST("/sign-up")
    suspend fun signUp(@Body signInDTO: SignInDTO): Response<StringResponse>

    @POST("/sign-in")
    suspend fun signIn(@Body signInDTO: SignInDTO): Response<StringResponse>

    @GET("/login")
    suspend fun login(): Response<StringResponse>
}