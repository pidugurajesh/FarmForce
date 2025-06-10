package com.example.worknear.screens.auth

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
import com.example.worknear.network.ApiResponse
import com.example.worknear.network.PhoneNumberRequest
import com.example.worknear.network.OtpResponse
import com.example.worknear.network.RetrofitInstance
import com.example.worknear.network.UserRequest
import com.example.worknear.network.VerifyOtpRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegistrationPage(navController: NavHostController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }  // New variable for username
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    var showPhoneDialog by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    var showOtpDialog by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf("") }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF00B4DB), Color(0xFF08338A), Color(0xFF00C9A7))
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
                    text = "FarmForce Register",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0083B0)
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

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))
                // After username field, before password field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))


                if (error.isNotEmpty()) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty() || username.isEmpty()) {
                            error = "All fields are required"
                        } else if (password != confirm) {
                            error = "Passwords do not match"
                        } else {
                            error = ""
                            val userRequest = UserRequest(email, username, password, phoneNumber)
                            RetrofitInstance.apiService.registerUser(userRequest).enqueue(object : Callback<ApiResponse> {
                                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                    if (response.isSuccessful) {
                                      //  Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                        // navController.navigate("otpVerification")  // Navigate to OTP verification
                                        sendOtpToServer(
                                            phoneNumber = phoneNumber,
                                            context = context,
                                            onOtpSent = { showOtpDialog = true })
                                    } else if (response.code() == 409) {  // 409 Conflict
                                        Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                    Toast.makeText(context, "Network Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xFF00B4DB), Color(0xFF04073F)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Register", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Already have an account? Login", color = Color(0xFF00B4DB))
                }
            }
        }
    }

    // The phone number and OTP dialog remains the same...



// 📱 2nd Popup: Enter OTP
    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = { Text("Enter OTP") },
            text = {
                Column {
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("Enter 6-digit OTP") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (otpError.isNotEmpty()) {
                        Text(otpError, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (otpCode.length != 6) {
                        otpError = "Please enter a valid 6-digit OTP"
                    } else {
                        otpError = ""
                        verifyOtpFromServer(
                            phoneNumber = phoneNumber,
                            otpCode = otpCode,
                            context = context,
                            onSuccess = {
                                Toast.makeText(context, "Registration Complete!", Toast.LENGTH_SHORT).show()
                                showOtpDialog = false
                                navController.navigate("login") // ✅ Navigate to HomeScreen after success
                            },
                            onFailure = {
                                otpError = it
                            }
                        )
                    }
                }) {
                    Text("Verify OTP")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
fun sendOtpToServer(
    phoneNumber: String,
    context: Context,
    onOtpSent: () -> Unit
) {
    val phoneNumberRequest = PhoneNumberRequest(phoneNumber = phoneNumber)

    RetrofitInstance.apiService.sendOtp(phoneNumberRequest)
        .enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "OTP Sent Successfully!", Toast.LENGTH_SHORT).show()
                    onOtpSent() // 👉 Open OTP Pop-up now
                } else {
                    Toast.makeText(context, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                Toast.makeText(context, "Network Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
}

fun verifyOtpFromServer(
phoneNumber: String,
otpCode: String,
context: android.content.Context,
onSuccess: () -> Unit,
onFailure: (String) -> Unit
) {
    if (phoneNumber.isEmpty() || otpCode.isEmpty()) {
        onFailure("Phone number and OTP code are required")
        return
    }

    // Log the values to verify they are correct
    println("Verifying OTP for phone: $phoneNumber with code: $otpCode")

    val verifyRequest = VerifyOtpRequest(phoneNumber = phoneNumber, code = otpCode)

    RetrofitInstance.apiService.verifyOtp(verifyRequest)
        .enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        onSuccess()
                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                    } else {
                        onFailure(body?.message ?: "Invalid OTP")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() // Capture the error body
                    onFailure("Server Error: $errorBody") // Show error message
                    println("API Error: $errorBody") // Log to console
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                onFailure("Network Error: ${t.localizedMessage}")
                println("Network Error: ${t.localizedMessage}") // Log network error
            }
        })
}
