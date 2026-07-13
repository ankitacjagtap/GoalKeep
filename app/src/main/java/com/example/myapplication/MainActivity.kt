package com.example.myapplication

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.local.GoalDatabase
import com.example.myapplication.data.repository.FirebaseGoalRepository
import com.example.myapplication.data.repository.RoomGoalRepository
import com.google.firebase.FirebaseApp
import com.example.myapplication.notifications.ReminderManager
import com.example.myapplication.ui.goals.AddGoalScreen
import com.example.myapplication.ui.goals.GoalDetailScreen
import com.example.myapplication.ui.goals.GoalDetailViewModel
import com.example.myapplication.ui.goals.GoalListScreen
import com.example.myapplication.ui.goals.GoalViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                
                // Initialize Room Database and Repository
                val database = remember { GoalDatabase.getDatabase(this) }
                val reminderManager = remember { ReminderManager(this) }
                val repository = remember { FirebaseGoalRepository(database.goalDao(), reminderManager) }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                NavHost(navController = navController, startDestination = "goal_list") {
                    composable("goal_list") {
                        val viewModel: GoalViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return GoalViewModel(repository) as T
                                }
                            }
                        )
                        GoalListScreen(
                            viewModel = viewModel,
                            onGoalClick = { goalId ->
                                navController.navigate("goal_detail/$goalId")
                            },
                            onAddGoalClick = {
                                navController.navigate("add_goal")
                            }
                        )
                    }
                    composable(
                        "goal_detail/{goalId}",
                        arguments = listOf(navArgument("goalId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
                        val detailViewModel: GoalDetailViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return GoalDetailViewModel(repository, goalId) as T
                                }
                            }
                        )
                        GoalDetailScreen(
                            viewModel = detailViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable("add_goal") {
                        val viewModel: GoalViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return GoalViewModel(repository) as T
                                }
                            }
                        )
                        AddGoalScreen(
                            onGoalAdded = { title, desc ->
                                viewModel.addGoal(title, desc, System.currentTimeMillis() + (86400000 * 7))
                                navController.popBackStack()
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
