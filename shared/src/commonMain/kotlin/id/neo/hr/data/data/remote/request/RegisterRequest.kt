package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
  @SerialName("username")
  val username: String,
  @SerialName("name")
  val name: String,
  @SerialName("email")
  val email: String,
  @SerialName("phone_number")
  val phoneNumber: String,
  @SerialName("company_name")
  val companyName: String,
  @SerialName("company_address")
  val companyAddress: String,
  @SerialName("password")
  val password: String,
  @SerialName("confirm_password")
  val confirmPassword: String,
  @SerialName("branch_coordinate")
  val branchCoordinate: String?,
)
