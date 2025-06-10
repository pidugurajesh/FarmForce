package com.example.worknear.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.worknear.network.ApiService
import com.example.worknear.network.LoginRequest
import com.example.worknear.network.LoginResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun LoginPage(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val context = LocalContext.current

    val apiService = Retrofit.Builder()
        .baseUrl("https://backend-server-g4uw.onrender.com") // Replace with your backend URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF00B4DB),
            Color(0xFF08338A),
            Color(0xFF00C9A7)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "FarmForce Login",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0083B0)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            error = "Please enter both email and password"
                        } else {
                            error = ""

                            val loginRequest = LoginRequest(email, password)
                            apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
                                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                    if (response.isSuccessful && response.body() != null) {
                                        val loginResponse = response.body()!!

                                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()

                                        // Save login session and userId in SharedPreferences
                                        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        sharedPreferences.edit().apply {
                                            putBoolean("is_logged_in", true)
                                            putString("email", email)
                                            putString("userId", loginResponse.userId ?: "")
                                            apply()
                                        }

                                        navController.navigate("homepage") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        error = errorBody ?: "Invalid credentials or server error"
                                    }
                                }

                                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                    error = "Network Error: ${t.message}"
                                }
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF00B4DB), Color(0xFF04073F))
                                )
                            )
                    ) {
                        Text("Login", color = Color.White, modifier = Modifier.align(Alignment.Center))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Don't have an account? Register", color = Color(0xFF0083B0))
                }
            }
        }
    }
}
