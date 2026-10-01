package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.RegisterModel

@Serializable
data class RegisterResponse(
    @SerialName("account_email")
    val accountEmail: String? = null,
    @SerialName("account_name")
    val accountName: String? = null,
    @SerialName("account_phone_number")
    val accountPhoneNumber: String? = null,
    @SerialName("account_company")
    val accountCompany: String? = null,
) {
  fun toDomain(): RegisterModel {
    return RegisterModel(
      accountEmail = accountEmail ?: "",
      accountName = accountName ?: "",
      accountPhoneNumber = accountPhoneNumber ?: "",
      accountCompany = accountCompany ?: ""
    )
  }

}
