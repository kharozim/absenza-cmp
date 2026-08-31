package id.neo.hr.data.data.local.model

import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.data.domain.model.CompanyModel
import id.neo.hr.data.domain.model.DeviceModel
import id.neo.hr.data.domain.model.LoginModel
import id.neo.hr.data.domain.model.PackageModel
import kotlinx.serialization.Serializable

/** Persisted representation kept separate from the domain model's serialization concerns. */
@Serializable
internal data class StoredLoginModel(
    val id: Int,
    val accountUid: String,
    val accountName: String,
    val accountEmail: String,
    val accountPhoneNumber: String,
    val accountPosition: String,
    val accountRole: String,
    val accountUrlPhoto: String,
    val emailVerification: Boolean,
    val phoneNumberVerification: Boolean,
    val leaveQuota: Int,
    val tokenAccess: String,
    val tokenRefresh: String,
    val company: StoredCompanyModel,
    val branch: StoredBranchModel,
    val device: StoredDeviceModel,
)

@Serializable
internal data class StoredCompanyModel(
    val id: String,
    val companyName: String,
    val companyAddress: String,
    val isActive: Boolean,
    val companyUrlPhoto: String,
    val packageModel: StoredPackageModel,
)

@Serializable
internal data class StoredPackageModel(
    val packageName: String,
    val packageMaxUser: Int,
    val packageMaxBranch: Int,
)

@Serializable
internal data class StoredBranchModel(
    val branchCode: String,
    val branchName: String,
    val branchAddress: String,
    val branchCoordinate: String,
    val openHour: String,
    val closeHour: String,
    val isActive: Boolean,
    val isFreeBranch: Boolean,
)

@Serializable
internal data class StoredDeviceModel(
    val appVersion: String,
    val deviceName: String,
    val deviceSn: String,
    val deviceModel: String,
    val deviceOs: String,
    val tokenFcm: String,
)

internal fun LoginModel.toStored(): StoredLoginModel = StoredLoginModel(
    id = id,
    accountUid = accountUid,
    accountName = accountName,
    accountEmail = accountEmail,
    accountPhoneNumber = accountPhoneNumber,
    accountPosition = accountPosition,
    accountRole = accountRole,
    accountUrlPhoto = accountUrlPhoto,
    emailVerification = emailVerification,
    phoneNumberVerification = phoneNumberVerification,
    leaveQuota = leaveQuota,
    tokenAccess = tokenAccess,
    tokenRefresh = tokenRefresh,
    company = StoredCompanyModel(
        id = company.id,
        companyName = company.companyName,
        companyAddress = company.companyAddress,
        isActive = company.isActive,
        companyUrlPhoto = company.companyUrlPhoto,
        packageModel = StoredPackageModel(
            packageName = company.`package`.packageName,
            packageMaxUser = company.`package`.packageMaxUser,
            packageMaxBranch = company.`package`.packageMaxBranch,
        ),
    ),
    branch = StoredBranchModel(
        branchCode = branch.branchCode,
        branchName = branch.branchName,
        branchAddress = branch.branchAddress,
        branchCoordinate = branch.branchCoordinate,
        openHour = branch.openHour,
        closeHour = branch.closeHour,
        isActive = branch.isActive,
        isFreeBranch = branch.isFreeBranch,
    ),
    device = StoredDeviceModel(
        appVersion = device.appVersion,
        deviceName = device.deviceName,
        deviceSn = device.deviceSn,
        deviceModel = device.deviceModel,
        deviceOs = device.deviceOs,
        tokenFcm = device.tokenFcm,
    ),
)

internal fun StoredLoginModel.toDomain(): LoginModel = LoginModel(
    id = id,
    accountUid = accountUid,
    accountName = accountName,
    accountEmail = accountEmail,
    accountPhoneNumber = accountPhoneNumber,
    accountPosition = accountPosition,
    accountRole = accountRole,
    accountUrlPhoto = accountUrlPhoto,
    emailVerification = emailVerification,
    phoneNumberVerification = phoneNumberVerification,
    leaveQuota = leaveQuota,
    tokenAccess = tokenAccess,
    tokenRefresh = tokenRefresh,
    company = CompanyModel(
        id = company.id,
        companyName = company.companyName,
        companyAddress = company.companyAddress,
        isActive = company.isActive,
        companyUrlPhoto = company.companyUrlPhoto,
        `package` = PackageModel(
            packageName = company.packageModel.packageName,
            packageMaxUser = company.packageModel.packageMaxUser,
            packageMaxBranch = company.packageModel.packageMaxBranch,
        ),
    ),
    branch = BranchModel(
        branchCode = branch.branchCode,
        branchName = branch.branchName,
        branchAddress = branch.branchAddress,
        branchCoordinate = branch.branchCoordinate,
        openHour = branch.openHour,
        closeHour = branch.closeHour,
        isActive = branch.isActive,
        isFreeBranch = branch.isFreeBranch,
    ),
    device = DeviceModel(
        appVersion = device.appVersion,
        deviceName = device.deviceName,
        deviceSn = device.deviceSn,
        deviceModel = device.deviceModel,
        deviceOs = device.deviceOs,
        tokenFcm = device.tokenFcm,
    ),
)
