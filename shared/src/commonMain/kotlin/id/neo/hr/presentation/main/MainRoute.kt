package id.neo.hr.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.neo.hr.presentation.home.HomeScreen
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.serialization.Serializable
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.activity
import neohr_mp.shared.generated.resources.home
import neohr_mp.shared.generated.resources.ic_bottom_nav_home
import neohr_mp.shared.generated.resources.ic_bottom_nav_home_selected
import neohr_mp.shared.generated.resources.ic_bottom_nav_setting
import neohr_mp.shared.generated.resources.ic_bottom_nav_setting_selected
import neohr_mp.shared.generated.resources.ic_bottom_nav_task
import neohr_mp.shared.generated.resources.ic_bottom_nav_task_selected
import neohr_mp.shared.generated.resources.profile
import neohr_mp.shared.generated.resources.setting
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
private data object MainHomeTab

@Serializable
private data object MainActivityTab

@Serializable
private data object MainSettingTab

private enum class MainTab {
  Home,
  Activity,
  Setting,
}

private data class BottomMenu(
  val tab: MainTab,
  val unselectedIcon: DrawableResource,
  val selectedIcon: DrawableResource,
  val label: StringResource,
)

private val bottomMenus = listOf(
  BottomMenu(
    tab = MainTab.Home,
    unselectedIcon = Res.drawable.ic_bottom_nav_home,
    selectedIcon = Res.drawable.ic_bottom_nav_home_selected,
    label = Res.string.home,
  ),
  BottomMenu(
    tab = MainTab.Activity,
    unselectedIcon = Res.drawable.ic_bottom_nav_task,
    selectedIcon = Res.drawable.ic_bottom_nav_task_selected,
    label = Res.string.activity,
  ),
  BottomMenu(
    tab = MainTab.Setting,
    unselectedIcon = Res.drawable.ic_bottom_nav_setting,
    selectedIcon = Res.drawable.ic_bottom_nav_setting_selected,
    label = Res.string.setting,
  ),
)

@Composable
fun MainRoute(
  navToProfile: () -> Unit,
  onTokenExpired: () -> Unit,
  sessionUtil: SessionUtil = koinInject(),
) {
  val isAdmin by sessionUtil.isRoleAdmin.collectAsStateWithLifecycle(initialValue = false)
  val tabController = rememberNavController()
  val backStackEntry by tabController.currentBackStackEntryAsState()
  val selectedTab = backStackEntry?.destination.toMainTab()

  MainScreen(
    selectedTab = selectedTab,
    onTabSelected = { tab ->
      if (tab == selectedTab) return@MainScreen
      when (tab) {
        MainTab.Home -> tabController.navigate(MainHomeTab) {
          popUpTo(MainHomeTab) { inclusive = false }
          launchSingleTop = true
          restoreState = true
        }
        MainTab.Activity -> tabController.navigate(MainActivityTab) {
          popUpTo(MainHomeTab) { saveState = true }
          launchSingleTop = true
          restoreState = true
        }
        MainTab.Setting -> tabController.navigate(MainSettingTab) {
          popUpTo(MainHomeTab) { saveState = true }
          launchSingleTop = true
          restoreState = true
        }
      }
    },
  ) { paddingValues ->
    NavHost(
      navController = tabController,
      startDestination = MainHomeTab,
      modifier = Modifier.fillMaxSize(),
    ) {
      composable<MainHomeTab> {
        HomeScreen(
          innerPadding = paddingValues,
          isAdmin = isAdmin,
          onProfileClick = navToProfile,
          onTokenExpired = onTokenExpired,
        )
      }
      composable<MainActivityTab> {
        MainPlaceholderScreen(
          title = stringResource(Res.string.activity),
          description = if (isAdmin) {
            "Aktivitas admin sedang disiapkan"
          } else {
            "Aktivitas employee sedang disiapkan"
          },
          paddingValues = paddingValues,
        )
      }
      composable<MainSettingTab> {
        MainPlaceholderScreen(
          title = stringResource(Res.string.setting),
          description = "Pengaturan akun sedang disiapkan",
          paddingValues = paddingValues,
          actionText = stringResource(Res.string.profile),
          onAction = navToProfile,
        )
      }
    }
  }
}

@Composable
private fun MainScreen(
  selectedTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
  content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = Colors.Gray50,
    bottomBar = {
      MainBottomNavigation(
        menus = bottomMenus,
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
      )
    },
    content = content,
  )
}

@Composable
private fun MainBottomNavigation(
  menus: List<BottomMenu>,
  selectedTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        Brush.verticalGradient(
          colors = listOf(Colors.DarkTransparent.copy(alpha = 0.02f), Color.White),
        ),
      )
      .padding(top = 3.dp),
  ) {
    NavigationBar(
      containerColor = Colors.White,
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .navigationBarsPadding(),
    ) {
      menus.forEach { menu ->
        val selected = selectedTab == menu.tab
        Column(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onTabSelected(menu.tab) }
            .padding(vertical = 8.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Image(
            painter = painterResource(
              if (selected) menu.selectedIcon else menu.unselectedIcon,
            ),
            contentDescription = stringResource(menu.label),
            modifier = Modifier.size(32.dp),
          )
          Text(
            text = stringResource(menu.label),
            style = if (selected) TextStyleCustom.ExtraBold else TextStyleCustom.SemiBold,
            color = if (selected) Colors.Purple800 else Colors.Gray800,
          )
        }
      }
    }
  }
}

private fun NavDestination?.toMainTab(): MainTab = when (this?.route) {
  MainActivityTab::class.qualifiedName -> MainTab.Activity
  MainSettingTab::class.qualifiedName -> MainTab.Setting
  else -> MainTab.Home
}

@Composable
private fun MainPlaceholderScreen(
  title: String,
  description: String,
  paddingValues: PaddingValues,
  actionText: String? = null,
  onAction: () -> Unit = {},
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Colors.Gray50)
      .padding(paddingValues)
      .padding(24.dp),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = title,
        style = TextStyleCustom.ExtraBold,
        color = Colors.Gray800,
      )
      Text(
        text = description,
        style = TextStyleCustom.Medium,
        color = Colors.Gray500,
        modifier = Modifier.padding(top = 8.dp),
      )
      if (actionText != null) {
        Text(
          text = actionText,
          style = TextStyleCustom.Bold,
          color = Colors.Purple800,
          modifier = Modifier
            .padding(top = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onAction)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        )
      }
    }
  }
}
