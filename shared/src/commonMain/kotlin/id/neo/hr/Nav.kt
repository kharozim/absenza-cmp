package id.neo.hr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import id.neo.hr.data.data.remote.request.RegisterRequest
import id.neo.hr.data.domain.model.CoordinateModel
import id.neo.hr.presentation.activity.DetailTaskAdminScreen
import id.neo.hr.presentation.activity.TaskAdminViewModel
import id.neo.hr.presentation.activity.TaskViewModel
import id.neo.hr.presentation.activity.add.CreateTaskScreen
import id.neo.hr.presentation.attendance.`in`.ClockInScreen
import id.neo.hr.presentation.auth.login.LoginScreen
import id.neo.hr.presentation.auth.register.RegisterScreen
import id.neo.hr.presentation.auth.registerotp.RegisterOtpScreen
import id.neo.hr.presentation.location.SearchMapScreen
import id.neo.hr.presentation.main.MainRoute
import id.neo.hr.presentation.setting.SettingViewModel
import id.neo.hr.presentation.setting.branch.BranchDetailScreen
import id.neo.hr.presentation.setting.branch.EditBranchScreen
import id.neo.hr.presentation.setting.changepassword.ChangePasswordScreen
import id.neo.hr.presentation.setting.legal.LegalDocumentScreen
import id.neo.hr.presentation.setting.profile.ProfileScreen as AccountProfileScreen
import id.neo.hr.presentation.splash.FirstScreen
import kotlinx.serialization.Serializable
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.privacy_policy_title
import neohr_mp.shared.generated.resources.privacy_policy_url_empty
import neohr_mp.shared.generated.resources.terms_and_conditions_title
import neohr_mp.shared.generated.resources.terms_and_conditions_url_empty
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by Kharozim
 * 28/08/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@Serializable
object Home

@Serializable
object Profile

@Serializable
object ChangePassword

@Serializable
object TermsAndConditions

@Serializable
object PrivacyPolicy

@Serializable
data class BranchDetail(val branchCode: String)

@Serializable
data class EditBranch(val branchCode: String)

@Serializable
object BranchLocationPicker

@Serializable
object Splash

@Serializable
object Login

@Serializable
object Register

@Serializable
object RegisterOtp

@Serializable
object LocationPicker

@Serializable
data class DetailTaskAdmin(
  val accountId: Int,
  val fullName: String,
  val photo: String,
  val username: String,
)

@Serializable
object CreateTask

@Serializable
object ClockIn

@Composable
fun Nav() {
  val controller = rememberNavController()
  var registerOtpPayload by remember { mutableStateOf<RegisterRequest?>(null) }
  var selectedRegisterCoordinate by remember { mutableStateOf<CoordinateModel?>(null) }
  var selectedBranchCoordinate by remember { mutableStateOf<CoordinateModel?>(null) }
  var branchRefreshVersion by remember { mutableStateOf(0) }

  NavHost(navController = controller, startDestination = Splash) {
    composable<Home> {
      MainRoute(
        navToProfile = { controller.navigate(Profile) },
        navToTermAndConditions = { controller.navigate(TermsAndConditions) },
        navToPrivacyPolicy = { controller.navigate(PrivacyPolicy) },
        navToChangePassword = { controller.navigate(ChangePassword) },
        navToBusinessLocation = { branchCode ->
          if (branchCode.isNotBlank()) controller.navigate(BranchDetail(branchCode))
        },
        onTokenExpired = {
          controller.navigate(Login) {
            popUpTo(Home) { inclusive = true }
          }
        },
        navToDetailTaskAdmin = { employee ->
          controller.navigate(
            DetailTaskAdmin(
              accountId = employee.id,
              fullName = employee.accountName,
              photo = employee.accountUrlPhoto,
              username = employee.accountUid,
            )
          )
        },
        navToCreate = {
          controller.navigate(CreateTask)
        },
        navToClockIn = {
          controller.navigate(ClockIn)
        },
      )
    }
    composable<Splash> {
      FirstScreen(
        navToMain = {
          controller.navigate(Home) {
            popUpTo(Splash) { inclusive = true }
          }
        },
        navToLogin = {
          controller.navigate(Login) {
            popUpTo(Splash) { inclusive = true }
          }
        },
      )
    }

    composable<Login> {
      LoginScreen(
        navToMain = {
          controller.navigate(Home) {
            popUpTo(Login) { inclusive = true }
          }
        },
        navToRegister = { controller.navigate(Register) },
      )
    }

    composable<Register> {
      RegisterScreen(
        navBack = { controller.popBackStack() },
        onRegisterSuccess = { registerRequest ->
          registerOtpPayload = registerRequest
          controller.navigate(RegisterOtp)
        },
        onLocationPicker = { controller.navigate(LocationPicker) },
        selectedCoordinate = selectedRegisterCoordinate,
      )
    }

    composable<LocationPicker> {
      SearchMapScreen(
        initialCoordinate = selectedRegisterCoordinate,
        onBack = { controller.popBackStack() },
        onSave = { coordinate ->
          selectedRegisterCoordinate = coordinate
          controller.popBackStack()
        },
      )
    }

    composable<RegisterOtp> {
      val payload = registerOtpPayload
      if (payload == null) {
        controller.popBackStack()
      } else {
        RegisterOtpScreen(
          registerRequest = payload,
          navBack = { controller.popBackStack() },
          navToHome = {
            registerOtpPayload = null
            controller.navigate(Home) {
              popUpTo(Login) { inclusive = true }
            }
          },
        )
      }
    }

    composable<Profile> {
      AccountProfileScreen(
        navBack = { controller.popBackStack() },
        onTokenExpired = {
          controller.navigate(Login) { popUpTo(Home) { inclusive = true } }
        },
      )
    }

    composable<ChangePassword> {
      ChangePasswordScreen(
        navBack = { controller.popBackStack() },
        onTokenExpired = {
          controller.navigate(Login) { popUpTo(Home) { inclusive = true } }
        },
      )
    }

    composable<TermsAndConditions> {
      val viewModel: SettingViewModel = koinViewModel()
      val state by viewModel.state.collectAsStateWithLifecycle()
      LegalDocumentScreen(
        title = stringResource(Res.string.terms_and_conditions_title),
        url = state.termAndConditionUrl,
        emptyMessage = stringResource(Res.string.terms_and_conditions_url_empty),
        navBack = { controller.popBackStack() },
      )
    }

    composable<PrivacyPolicy> {
      val viewModel: SettingViewModel = koinViewModel()
      val state by viewModel.state.collectAsStateWithLifecycle()
      LegalDocumentScreen(
        title = stringResource(Res.string.privacy_policy_title),
        url = state.privacyPolicyUrl,
        emptyMessage = stringResource(Res.string.privacy_policy_url_empty),
        navBack = { controller.popBackStack() },
      )
    }

    composable<BranchDetail> { entry ->
      val route = entry.toRoute<BranchDetail>()
      BranchDetailScreen(
        branchCode = route.branchCode,
        refreshVersion = branchRefreshVersion,
        navBack = { controller.popBackStack() },
        navToEdit = { branchCode ->
          selectedBranchCoordinate = null
          controller.navigate(EditBranch(branchCode))
        },
        onTokenExpired = {
          controller.navigate(Login) { popUpTo(Home) { inclusive = true } }
        },
      )
    }

    composable<EditBranch> { entry ->
      val route = entry.toRoute<EditBranch>()
      EditBranchScreen(
        branchCode = route.branchCode,
        selectedCoordinate = selectedBranchCoordinate,
        navBack = {
          selectedBranchCoordinate = null
          controller.popBackStack()
        },
        onSaved = {
          selectedBranchCoordinate = null
          branchRefreshVersion++
          controller.popBackStack()
        },
        navToMapPicker = { coordinate ->
          selectedBranchCoordinate = coordinate
          controller.navigate(BranchLocationPicker)
        },
        onTokenExpired = {
          controller.navigate(Login) { popUpTo(Home) { inclusive = true } }
        },
      )
    }

    composable<BranchLocationPicker> {
      SearchMapScreen(
        initialCoordinate = selectedBranchCoordinate,
        onBack = { controller.popBackStack() },
        onSave = { coordinate ->
          selectedBranchCoordinate = coordinate
          controller.popBackStack()
        },
      )
    }
    composable<DetailTaskAdmin> { entry ->
      val viewModel: TaskAdminViewModel = koinViewModel()
      val route = entry.toRoute<DetailTaskAdmin>()
      DetailTaskAdminScreen(
        accountId = route.accountId,
        fullName = route.fullName,
        photo = route.photo,
        username = route.username,
        viewModel = viewModel,
        onBackClick = { controller.navigateUp() }
      )
    }

    composable<CreateTask> { entry ->
      val viewModel: TaskViewModel = koinViewModel()
      CreateTaskScreen(
        viewModel = viewModel,
        onBackClick = { controller.navigateUp() }
      )
    }

    composable<ClockIn> {
      ClockInScreen(
        navBack = { controller.popBackStack() },
        onTokenExpired = {
          controller.navigate(Login) { popUpTo(Home) { inclusive = true } }
        },
        navToHome = {
          controller.navigate(Home) {
            popUpTo(ClockIn) { inclusive = true }
          }
        },
      )
    }
  }
}
