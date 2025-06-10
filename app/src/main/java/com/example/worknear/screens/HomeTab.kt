package com.example.worknear.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.worknear.R
import com.example.worknear.network.Job
import com.example.worknear.network.RetrofitInstance
import com.example.worknear.viewmodel.ProfileViewModel

@Composable
fun HomeTab(navController: NavHostController, profileViewModel: ProfileViewModel) {
    val context = LocalContext.current

    // List of random farmer names to display instead of IDs
    val randomNames = listOf("Ramesh", "Sita", "Rajesh", "Anita", "Vikram", "Lakshmi")

    var jobList by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val acceptedJobs = remember { mutableStateListOf<String>() }

    // Fetch jobs from backend
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.apiService.getJobs()
            if (response.isSuccessful) {
                response.body()?.let {
                    jobList = it
                }
            }
        } catch (e: Exception) {
            // You can log or handle error here
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            items(jobList, key = { it.id }) { job ->
                // Pick a random name for each job uniquely using job.id
                val randomName = remember(job.id) {
                    randomNames.random()
                }

                JobCard(
                    job = job,
                    displayName = randomName,
                    onAccept = {
                        if (!acceptedJobs.contains(job.id)) {
                            acceptedJobs.add(job.id)
                            profileViewModel.acceptJob(job)

                            Toast.makeText(
                                context,
                                "Job accepted: ${job.title}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onIgnore = {
                        jobList = jobList.filterNot { it.id == job.id }
                    }
                )
            }
        }
    }
}

@Composable
fun JobCard(
    job: Job,
    displayName: String,
    onAccept: () -> Unit,
    onIgnore: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.farmer),
                    contentDescription = "Farmer Profile",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    // Show random farmer name instead of postedBy ID
                    Text(text = displayName, fontSize = 18.sp, color = Color.Black)
                    Text(text = job.postedTime ?: "", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = job.title ?: "", fontSize = 20.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Location: ${job.location ?: ""}", fontSize = 16.sp, color = Color.DarkGray)
            Text(text = "Vacancies: ${job.vacancies}", fontSize = 16.sp, color = Color.DarkGray)
            Text(text = "Wage: ₹${job.wage}", fontSize = 16.sp, color = Color.DarkGray)
            Text(text = "Age Restriction: ${job.ageRestriction ?: "N/A"}", fontSize = 16.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(text = "Accept", color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onIgnore,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text(text = "Ignore", color = Color.White)
                }
            }
        }
    }
}
