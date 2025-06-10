package com.example.worknear.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.worknear.network.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class ProfileViewModel(private val apiService: ApiService, private val userId: String) : ViewModel() {

    private val _userName = mutableStateOf("")
    val userName: State<String> = _userName

    private val _acceptedJobs = mutableStateOf<List<Job>>(emptyList())
    val acceptedJobs: State<List<Job>> = _acceptedJobs

    private val _doneJobs = mutableStateOf<List<Job>>(emptyList())
    val doneJobs: State<List<Job>> = _doneJobs

    private val _earnings = mutableStateOf(0)
    val earnings: State<Int> = _earnings

    init {
        loadUserProfile()
    }

    private suspend fun fetchJobsByIds(jobIds: List<String>): List<Job> {
        // Parallel fetch all jobs by IDs
        return jobIds.map { id ->
            viewModelScope.async {
                try {
                    val response = apiService.getJobById(id)
                    if (response.isSuccessful) {
                        response.body()?.job
                    } else {
                        Log.e("ProfileViewModel", "Failed to get job $id: ${response.code()}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("ProfileViewModel", "Exception fetching job $id", e)
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val response = apiService.getUserProfile(userId)
                if (response.isSuccessful) {
                    val user = response.body()!!
                    _userName.value = user.userName

                    // fetch full Job objects for accepted and done jobs
                    val acceptedJobList = fetchJobsByIds(user.acceptedJobs)
                    val doneJobList = fetchJobsByIds(user.completedJobs)

                    _acceptedJobs.value = acceptedJobList
                    _doneJobs.value = doneJobList

                    _earnings.value = user.earnings.sumOf { it.amount }
                } else {
                    Log.e("ProfileViewModel", "User profile load failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile", e)
            }
        }
    }

    fun setUserName(newName: String) {
        _userName.value = newName

        // Optional: Call backend API to update username
        viewModelScope.launch {
            try {
                val response = apiService.updateUserName(userId, newName)
                if (!response.isSuccessful) {
                    Log.e("ProfileViewModel", "Failed to update username on server")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating username", e)
            }
        }
    }

    fun getTotalEarnings(): Int {
        return _earnings.value
    }

    fun acceptJob(job: Job) {
        if (_acceptedJobs.value.any { it.id == job.id }) return // Avoid duplicates
        _acceptedJobs.value = _acceptedJobs.value + job
    }

    fun markJobAsDone(job: Job) {
        viewModelScope.launch {
            try {
                val request = CompleteJobRequest(userId, job.id, job.wage)
                val response = apiService.completeJob(request)
                if (response.isSuccessful) {
                    val user = response.body()!!
                    // fetch updated job lists
                    val acceptedJobList = fetchJobsByIds(user.acceptedJobs)
                    val doneJobList = fetchJobsByIds(user.completedJobs)

                    _acceptedJobs.value = acceptedJobList
                    _doneJobs.value = doneJobList

                    _earnings.value = user.earnings.sumOf { it.amount }
                } else {
                    Log.e("ProfileViewModel", "Complete job failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Complete job failed", e)
            }
        }
    }

    fun logout(context: Context, navController: NavController) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }
}
