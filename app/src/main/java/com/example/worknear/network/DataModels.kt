package com.example.worknear.network

import com.google.gson.annotations.SerializedName

data class PhoneNumberRequest(val phoneNumber: String)
data class VerifyOtpRequest(val phoneNumber: String, val code: String)
data class OtpResponse(val success: Boolean, val message: String?=null)

data class LoginRequest(val email: String, val password: String)

data class UserRequest(
    val email: String,
    val username: String,
    val password: String,
    val phoneNumber: String,
)

data class AcceptedJobModel(val id: String, val title: String, val wage: Int)

data class EarningEntry(val jobTitle: String, val amount: Int, val date: Long)

data class LoginResponse(
    val message: String,
    val token: String?, // If your backend sends token
    val userId: String? // Optional
)

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val username: String? = null // nullable, since not all responses may have username
)

data class JobResponse(
    val success: Boolean,
    val message: String,
    val job: Job? = null
)

data class Job(
    @SerializedName("_id")
    val id: String,
    val postedBy: String,
    val title: String,
    val location: String,
    val vacancies: Int,
    val wage: Int,
    val ageRestriction: String?,
    val postedTime: String
)

data class CompleteJobRequest(val userId: String, val jobId: String, val amount: Int)
data class AcceptJobRequest(val userId: String, val jobId: String)

// **UPDATED HERE: acceptedJobs and completedJobs as List<String> instead of List<Job>**
data class UserResponse(
    val userName: String,
    val acceptedJobs: List<String>,    // changed to List<String>
    val completedJobs: List<String>,   // changed to List<String>
    val earnings: List<Earning>
)

data class Earning(val jobId: String, val amount: Int, val date: String)

data class UpdateUsernameRequest(
    val username: String
)
