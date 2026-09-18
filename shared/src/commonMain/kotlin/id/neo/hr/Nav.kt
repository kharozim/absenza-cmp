package id.neo.hr

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.neo.hr.presentation.auth.login.LoginScreen
import id.neo.hr.presentation.auth.register.RegisterScreen
import id.neo.hr.presentation.splash.FirstScreen
import id.neo.hr.presentation.util.ToastManager
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

@Composable
fun Nav() {
  val controller = rememberNavController()

  NavHost(navController = controller, startDestination = Splash) {
    composable<Home> {
      HomeScreen(onProfileClick = { controller.navigate(Profile) })
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
        onRegisterSuccess = {
          ToastManager.success("Registrasi berhasil")
          controller.popBackStack()
        },
      )
    }

    composable<Profile> { ProfileScreen() }
  }
}
