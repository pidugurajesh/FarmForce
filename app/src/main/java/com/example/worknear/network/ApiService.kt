package com.example.worknear.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

 // Add this data class if not defined yet

interface ApiService {

    @POST("/api/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/otp/send")
    fun sendOtp(@Body request: PhoneNumberRequest): Call<OtpResponse>

    @POST("/api/otp/verify")
    fun verifyOtp(@Body request: VerifyOtpRequest): Call<OtpResponse>

    @POST("/api/register")
    fun registerUser(@Body userRequest: UserRequest): Call<ApiResponse>

    @POST("/api/jobs")
    fun postJob(@Body job: Job): Call<JobResponse>

    @GET("/api/jobs")
    suspend fun getJobs(): Response<List<Job>>

    @GET("/api/jobs/{id}")            // <-- Added this function
    suspend fun getJobById(@Path("id") jobId: String): Response<JobResponse>

    @POST("/api/job/complete")
    suspend fun completeJob(@Body request: CompleteJobRequest): Response<UserResponse>

    @POST("/api/job/accept")
    suspend fun acceptJob(@Body request: AcceptJobRequest): Response<UserResponse>

    @GET("/api/user/{email}/data")
    suspend fun getUserProfile(@Path("email") email: String): Response<UserResponse>

    @PUT("/api/user/{userId}/username")
    suspend fun updateUserName(
        @Path("userId") userId: String,
        @Body request: String
    ): Response<ApiResponse>

}
