import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.worknear.network.ApiService
import com.example.worknear.screens.HomeTab
import com.example.worknear.screens.PostJobTab
//import com.example.worknear.screens.ProfileScreen
import com.example.worknear.screens.SearchTab
import com.example.worknear.viewmodel.ProfileViewModel
import com.example.worknear.viewmodel.ProfileViewModelFactory

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    apiService: ApiService,
    userId: String
) {
    val factory = remember(apiService, userId) {
        ProfileViewModelFactory(apiService, userId)
    }

    val profileViewModel: ProfileViewModel = viewModel(factory = factory)

    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeTab(navController, profileViewModel)
        }
        composable("search") {
            SearchTab(navController,profileViewModel)
        }
        composable("post") {
            PostJobTab(navController)
        }
        composable("profile") {
            // ✅ Now using the same already-created `profileViewModel`
            ProfileScreen(navController, profileViewModel)
        }
    }
}
