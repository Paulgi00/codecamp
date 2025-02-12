package com.example.wildidle.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wildidle.R
import kotlinx.serialization.Serializable

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: Any,
    val topBarComposable: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainNavController: NavController) {
    val contentNavController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val items = listOf(
        BottomNavigationItem(
            title = stringResource(id = R.string.home),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            screen = GameScreen,
            topBarComposable = { GameTopBar() }
        ),
        BottomNavigationItem(
            title = stringResource(id = R.string.shop),
            selectedIcon = Icons.Filled.Storefront,
            unselectedIcon = Icons.Outlined.Storefront,
            screen = ShopScreen,
            topBarComposable = { ShopTopBar() }
        ),
        BottomNavigationItem(
            title = stringResource(R.string.leaderboard),
            selectedIcon = Icons.Filled.Leaderboard,
            unselectedIcon = Icons.Filled.Leaderboard,
            screen = LeaderboardScreen,
            topBarComposable = { LeaderBoardTopBar(scrollBehavior) }
        ),
        BottomNavigationItem(
            title = stringResource(R.string.settings),
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            screen = SettingsScreen,
            topBarComposable = { SettingsTopBar() }
        )
    )

    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            contentNavController.navigate(item.screen)
                        }, label = {
                            Text(text = item.title)
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        })
                }
            }
        },
        topBar = {
            items[selectedItemIndex].topBarComposable()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            NavHost(navController = contentNavController, startDestination = GameScreen) {
                composable<GameScreen> {
                    GameComposable()
                }
                composable<ShopScreen> {
                    ShopComposable()
                }
                composable<SettingsScreen> {
                    SettingsComposable(mainNavController)
                }
                composable<LeaderboardScreen> {
                    LeaderboardComposable()
                }
            }

        }
    }
}

@Serializable
object GameScreen

@Serializable
object ShopScreen

@Serializable
object SettingsScreen

@Serializable
object LeaderboardScreen
