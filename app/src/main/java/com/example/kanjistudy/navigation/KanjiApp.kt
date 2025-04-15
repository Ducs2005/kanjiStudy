package com.example.kanjistudy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kanjistudy.ui.screens.GameScreen
import com.example.kanjistudy.ui.screens.HomeScreen
import com.example.kanjistudy.ui.screens.KanjiInputScreen
import com.example.kanjistudy.ui.screens.KanjiListScreen
import com.example.kanjistudy.ui.screens.StudyUnitInputScreen
import com.example.kanjistudy.ui.screens.StudyUnitListScreen
import com.example.kanjistudy.ui.screens.UnitSelectionScreen

@Composable
fun KanjiApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToUnitInput = { navController.navigate("unitInput") },
                onNavigateToUnitSelection = { navController.navigate("unitSelection") },
                onNavigateToUnitList = { navController.navigate("unitList") }
            )
        }
        composable("unitInput") {
            StudyUnitInputScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("unitSelection") {
            UnitSelectionScreen(
                onBack = { navController.popBackStack() },
                onUnitsSelected = { unitIds ->
                    // Encode the list of unit IDs as a comma-separated string
                    val unitIdsString = unitIds.joinToString(",")
                    navController.navigate("game/$unitIdsString")
                }
            )
        }
        composable(
            route = "game/{unitIds}",
            arguments = listOf(navArgument("unitIds") { type = NavType.StringType })
        ) { backStackEntry ->
            val unitIdsString = backStackEntry.arguments?.getString("unitIds") ?: ""
            // Decode the comma-separated string back into a list
            val unitIds = if (unitIdsString.isNotEmpty()) unitIdsString.split(",") else emptyList()
            GameScreen(
                unitIds = unitIds,
                onBack = { navController.popBackStack() }
            )
        }
        composable("unitList") {
            StudyUnitListScreen(
                onBack = { navController.popBackStack() },
                onUnitClick = { unitId ->
                    navController.navigate("kanjiList/$unitId")
                }
            )
        }
        composable(
            route = "kanjiList/{unitId}",
            arguments = listOf(navArgument("unitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val unitId = backStackEntry.arguments?.getString("unitId") ?: ""
            KanjiListScreen(
                unitId = unitId,
                onBack = {
                    if (navController.previousBackStackEntry?.destination?.route == "kanjiInput/{unitId}") {
                        navController.popBackStack("unitList", inclusive = false)
                    } else {
                        navController.popBackStack()
                    }
                },
                onAddKanji = { unitId ->
                    navController.navigate("kanjiInput/$unitId")
                }
            )
        }
        composable(
            route = "kanjiInput/{unitId}",
            arguments = listOf(navArgument("unitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val unitId = backStackEntry.arguments?.getString("unitId") ?: ""
            KanjiInputScreen(
                unitId = unitId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}