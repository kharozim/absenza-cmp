package id.neo.hr.data.domain.model

data class RegisterModel(
    val accountEmail: String,
    val accountName: String,
    val accountPhoneNumber: String,
    val accountCompany: String
)