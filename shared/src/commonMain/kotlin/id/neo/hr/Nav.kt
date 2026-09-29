package id.neo.hr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.neo.hr.data.data.remote.request.RegisterRequest
import id.neo.hr.data.domain.model.CoordinateModel
import id.neo.hr.presentation.auth.login.LoginScreen
import id.neo.hr.presentation.auth.register.RegisterScreen
import id.neo.hr.presentation.auth.registerotp.RegisterOtpScreen
import id.neo.hr.presentation.location.SearchMapScreen
import id.neo.hr.presentation.main.MainRoute
import id.neo.hr.presentation.splash.FirstScreen
import kotlinx.serialization.Serializable

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
object Splash

@Serializable
object Login

@Serializable
object Register

@Serializable
object RegisterOtp

@Serializable
object LocationPicker

@Composable
fun Nav() {
  val controller = rememberNavController()
  var registerOtpPayload by remember { mutableStateOf<RegisterRequest?>(null) }
  var selectedRegisterCoordinate by remember { mutableStateOf<CoordinateModel?>(null) }

  NavHost(navController = controller, startDestination = Splash) {
    composable<Home> {
      MainRoute(
        navToProfile = { controller.navigate(Profile) },
        onTokenExpired = {
          controller.navigate(Login) {
            popUpTo(Home) { inclusive = true }
          }
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

    composable<Profile> { ProfileScreen() }
  }
}
