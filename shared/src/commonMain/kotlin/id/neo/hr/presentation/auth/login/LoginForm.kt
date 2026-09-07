package id.neo.hr.presentation.auth.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginForm(
  val username: String,
  val password: String,
)