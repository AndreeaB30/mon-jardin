package ro.gradinamea.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ro.gradinamea.ui.detail.PlantDetailScreen
import ro.gradinamea.ui.edit.PlantEditScreen
import ro.gradinamea.ui.list.PlantListScreen

object Routes {
    const val ARG_PLANT_ID = "plantId"
    const val NO_PLANT = -1L

    const val LIST = "list"
    const val DETAIL = "detail/{$ARG_PLANT_ID}"
    const val EDIT = "edit?$ARG_PLANT_ID={$ARG_PLANT_ID}"

    fun detail(plantId: Long) = "detail/$plantId"

    /** Fara id = planta noua; cu id = editare. */
    fun edit(plantId: Long? = null) = if (plantId == null) "edit" else "edit?$ARG_PLANT_ID=$plantId"
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.LIST) {

        composable(Routes.LIST) {
            PlantListScreen(
                onPlantClick = { id -> navController.navigate(Routes.detail(id)) },
                onAddClick = { navController.navigate(Routes.edit()) },
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument(Routes.ARG_PLANT_ID) { type = NavType.LongType }),
        ) {
            PlantDetailScreen(
                onBack = { navController.navigateUp() },
                onEdit = { id -> navController.navigate(Routes.edit(id)) },
                onDeleted = { navController.popBackStack(Routes.LIST, inclusive = false) },
            )
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument(Routes.ARG_PLANT_ID) {
                type = NavType.LongType
                defaultValue = Routes.NO_PLANT
            }),
        ) {
            PlantEditScreen(
                onBack = { navController.navigateUp() },
                onSaved = { navController.navigateUp() },
            )
        }
    }
}
