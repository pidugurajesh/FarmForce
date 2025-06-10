import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.worknear.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel) {
    val context = LocalContext.current

    val userName by viewModel.userName

    // Local states initialized from ViewModel
    var acceptedJobs by remember { mutableStateOf(viewModel.acceptedJobs.value.toMutableList()) }
    var doneJobs by remember { mutableStateOf(viewModel.doneJobs.value.toMutableList()) }
    var totalEarnings by remember { mutableStateOf(viewModel.earnings.value) }

    var showEditDialog by remember { mutableStateOf(false) }
    var showAcceptedDialog by remember { mutableStateOf(false) }
    var showDoneDialog by remember { mutableStateOf(false) }
    var showEarningsDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Welcome, $userName", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        ProfileButton("Edit Profile") { showEditDialog = true }
        Spacer(modifier = Modifier.height(8.dp))

        ProfileButton("Jobs Accepted") { showAcceptedDialog = true }
        Spacer(modifier = Modifier.height(8.dp))

        ProfileButton("Jobs Done") { showDoneDialog = true }
        Spacer(modifier = Modifier.height(8.dp))

        ProfileButton("Total Earnings") { showEarningsDialog = true }
        Spacer(modifier = Modifier.height(8.dp))

        ProfileButton("Log Out") { showLogoutDialog = true }
    }

    // Dialog: Edit Profile
    if (showEditDialog) {
        var newName by remember { mutableStateOf(userName) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                Button(onClick = {
                    viewModel.setUserName(newName)
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Edit Username") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New Username") }
                )
            }
        )
    }

    // Dialog: Accepted Jobs
    if (showAcceptedDialog) {
        AlertDialog(
            onDismissRequest = { showAcceptedDialog = false },
            confirmButton = {
                TextButton(onClick = { showAcceptedDialog = false }) { Text("Close") }
            },
            title = { Text("Jobs Accepted") },
            text = {
                Box(modifier = Modifier.heightIn(max = 300.dp)) {
                    if (acceptedJobs.isEmpty()) {
                        Text("No accepted jobs yet.", modifier = Modifier.padding(8.dp))
                    } else {
                        LazyColumn {
                            items(acceptedJobs) { job ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Title: ${job.title ?: "N/A"}")
                                        Text("Wage: ₹${job.wage ?: 0}")
                                    }
                                    Button(onClick = {
                                        // Move job from accepted to done
                                        acceptedJobs = acceptedJobs.toMutableList().also { it.remove(job) }
                                        doneJobs = doneJobs.toMutableList().also { it.add(job) }
                                        totalEarnings += (job.wage ?: 0)
                                        Toast.makeText(context, "'${job.title}' marked as done!", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Text("Done")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    // Dialog: Completed Jobs
    if (showDoneDialog) {
        AlertDialog(
            onDismissRequest = { showDoneDialog = false },
            confirmButton = {
                TextButton(onClick = { showDoneDialog = false }) { Text("Close") }
            },
            title = { Text("Jobs Completed") },
            text = {
                Box(modifier = Modifier.heightIn(max = 300.dp)) {
                    if (doneJobs.isEmpty()) {
                        Text("No completed jobs yet.", modifier = Modifier.padding(8.dp))
                    } else {
                        LazyColumn {
                            items(doneJobs) { job ->
                                Text("• ${job.title ?: "N/A"} - ₹${job.wage ?: 0}")
                            }
                        }
                    }
                }
            }
        )
    }

    // Dialog: Earnings
    if (showEarningsDialog) {
        AlertDialog(
            onDismissRequest = { showEarningsDialog = false },
            confirmButton = {
                TextButton(onClick = { showEarningsDialog = false }) { Text("Close") }
            },
            title = { Text("Total Earnings") },
            text = {
                Column {
                    Text("You have earned ₹$totalEarnings from completed jobs.")
                    Spacer(modifier = Modifier.height(8.dp))
                    if (doneJobs.isEmpty()) {
                        Text("No earning details available.")
                    } else {
                        doneJobs.forEach {
                            Text("- ${it.title ?: "N/A"}: ₹${it.wage ?: 0}")
                        }
                    }
                }
            }
        )
    }

    // Dialog: Logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logout(context, navController)
                    showLogoutDialog = false
                }) {
                    Text("Yes, Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") }
        )
    }
}

@Composable
fun ProfileButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
    ) {
        Text(text, color = Color.White)
    }
}
