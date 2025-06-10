

package com.example.worknear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.worknear.network.ApiService

class ProfileViewModelFactory(
    private val apiService: ApiService,
    private val userId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(apiService, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
