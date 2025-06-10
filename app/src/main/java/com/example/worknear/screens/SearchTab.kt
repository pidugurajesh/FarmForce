package com.example.worknear.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.worknear.network.Job
import com.example.worknear.network.RetrofitInstance
import com.example.worknear.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchTab(navController: NavHostController, profileViewModel: ProfileViewModel) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var minWage by remember { mutableStateOf(0) }
    var maxWage by remember { mutableStateOf(10000) }
    var selectedLocation by remember { mutableStateOf("") }
    var jobList by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // List of random farmer names to assign to postedBy IDs
    val randomNames = listOf("Ramesh", "Sita", "Rajesh", "Anita", "Vikram", "Lakshmi")

    // Map postedBy ID -> Random farmer name (remembered across recompositions)
    val postedByNameMap = remember { mutableStateMapOf<String, String>() }

    // Fetch jobs on first composition
    LaunchedEffect(Unit) {
        isLoading = true
        scope.launch {
            try {
                val response = RetrofitInstance.apiService.getJobs()
                if (response.isSuccessful) {
                    jobList = response.body() ?: emptyList()

                    // Assign random farmer names to postedBy IDs if not already assigned
                    jobList.forEach { job ->
                        val postedById = job.postedBy ?: ""
                        if (postedById.isNotEmpty() && !postedByNameMap.containsKey(postedById)) {
                            postedByNameMap[postedById] = randomNames.random()
                        }
                    }
                } else {
                    errorMessage = "Failed to load jobs"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
                Log.e("SearchTab", "Error fetching jobs", e)
            }
            isLoading = false
        }
    }

    val query = searchQuery.text.lowercase()

    val filteredJobs = jobList.filter { job ->
        (job.postedBy?.lowercase()?.contains(query) ?: false)
                || (job.title?.lowercase()?.contains(query) ?: false)
                || (job.location?.lowercase()?.contains(query) ?: false)
                || (job.ageRestriction?.lowercase()?.contains(query) ?: false)
                || (job.postedTime?.lowercase()?.contains(query) ?: false)
                || job.vacancies.toString().contains(query)
                || job.wage.toString().contains(query)
    }.filter { job ->
        (job.wage in minWage..maxWage) &&
                (selectedLocation.isEmpty() || (job.location?.contains(selectedLocation, ignoreCase = true) ?: false))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search jobs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { showFilterDialog = true }) {
                Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            errorMessage.isNotEmpty() -> {
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            filteredJobs.isEmpty() -> {
                Text("No jobs found", modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredJobs) { job ->
                        // Get friendly name from map or fallback to postedBy ID or "Unknown"
                        val randomName = remember(job.id) {
                            randomNames.random()
                        }
                        val displayName =randomName
                        val context= LocalContext.current
                        JobCard(
                            job = job,
                            displayName = displayName,
                            onAccept = {
                                profileViewModel.acceptJob(job)
                                Toast.makeText(context, "Accepted job: ${job.title ?: "Untitled"}", Toast.LENGTH_SHORT).show()
                                Log.d("SearchTab", "Accepted job: ${job.title}")

                            },
                            onIgnore = {
                                jobList = jobList.filterNot { it.id == job.id }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            confirmButton = {
                Button(onClick = { showFilterDialog = false }) {
                    Text("Apply")
                }
            },
            title = { Text("Filter Jobs") },
            text = {
                Column {
                    Text(text = "Min Wage:")
                    OutlinedTextField(
                        value = minWage.toString(),
                        onValueChange = { input ->
                            minWage = input.toIntOrNull()?.coerceAtLeast(0) ?: 0
                        },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Max Wage:")
                    OutlinedTextField(
                        value = maxWage.toString(),
                        onValueChange = { input ->
                            maxWage = input.toIntOrNull()?.coerceAtMost(1_000_000) ?: 10000
                        },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Location:")
                    OutlinedTextField(
                        value = selectedLocation,
                        onValueChange = { selectedLocation = it },
                        singleLine = true
                    )
                }
            }
        )
    }
}
