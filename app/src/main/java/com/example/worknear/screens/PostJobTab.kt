package com.example.worknear.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.worknear.network.Job
import com.example.worknear.network.JobResponse
import com.example.worknear.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostJobTab(navController: NavHostController) {
    var jobTitle by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var vacancies by remember { mutableStateOf("") }
    var wage by remember { mutableStateOf("") }
    var ageRestriction by remember { mutableStateOf("") }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)

    if (userId == null) {
        Log.e("PostJob", "User ID not found")
        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        return
    }

    val currentDate = remember {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Post a Job",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 26.sp,
                color = Color(0xFF08338A)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = jobTitle,
            onValueChange = { jobTitle = it },
            label = { Text("Job Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = vacancies,
                onValueChange = { vacancies = it },
                label = { Text("Vacancies") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = wage,
                onValueChange = { wage = it },
                label = { Text("Wage (₹)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = ageRestriction,
            onValueChange = { ageRestriction = it },
            label = { Text("Age Restriction (Optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )

        Text(
            text = "Posting Date: $currentDate",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Basic input validation
                if (jobTitle.isBlank() || location.isBlank()
                    || vacancies.isBlank() || wage.isBlank()
                ) {
                    Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val job = Job(
                    id = "", // placeholder, backend assigns _id
                    postedBy = userId,
                    title = jobTitle,
                    location = location,
                    vacancies = vacancies.toIntOrNull() ?: 0,
                    wage = wage.toIntOrNull() ?: 0,
                    ageRestriction = ageRestriction,
                    postedTime = currentDate
                )

                RetrofitInstance.apiService.postJob(job).enqueue(object : Callback<JobResponse> {
                    override fun onResponse(call: Call<JobResponse>, response: Response<JobResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(context, "Job posted successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") {
                                popUpTo("post_job") { inclusive = true }
                            }
                        } else {
                            Log.e("PostJob", "Error: ${response.errorBody()?.string()}")
                            Toast.makeText(context, "Failed to post job", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<JobResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("PostJob", "Failure", t)
                    }
                })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4DB))
        ) {
            Text(text = "Post Job", color = Color.White, fontSize = 18.sp)
        }
    }
}
