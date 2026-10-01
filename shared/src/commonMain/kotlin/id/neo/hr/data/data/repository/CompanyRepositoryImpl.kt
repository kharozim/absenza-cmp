package id.neo.hr.data.data.repository

import id.neo.hr.data.data.remote.api.ApiCompany
import id.neo.hr.data.data.remote.request.BranchUpdateRequest
import id.neo.hr.data.data.remote.request.EmployeeEditRequest
import id.neo.hr.data.data.remote.request.EmployeeRequest
import id.neo.hr.data.data.remote.response.BranchResponse
import id.neo.hr.data.data.remote.response.CompanyFileResponse
import id.neo.hr.data.data.remote.response.EmployeeResponse
import id.neo.hr.data.data.remote.response.UserPassResponse
import id.neo.hr.data.data.util.NetworkUtil
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.data.domain.model.CompanyFileModel
import id.neo.hr.data.domain.model.EmployeeDetailModel
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.data.domain.model.UserPassModel
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.coroutines.flow.first

class CompanyRepositoryImpl(
  private val api: ApiCompany,
  private val session: SessionUtil,
) : CompanyRepository {

  override suspend fun getListEmployee(): StateDataUtil<List<EmployeeModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getListEmployee(header)
      },
      mapData = { response ->
        val data = response.data ?: emptyList()
        data.map { it.toDomain() }
      },
      defaultErrorMessage = "Failed to get employee list",
    )
  }

  override suspend fun getDetailEmployee(id: Int): StateDataUtil<EmployeeDetailModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getDetailEmployee(header, id)
      },
      mapData = { response ->
        val data = response.data ?: EmployeeResponse()
        data.toDetailDomain()
      },
      defaultErrorMessage = "Failed to get employee detail",
    )
  }

  override suspend fun addEmployee(payload: EmployeeRequest): StateDataUtil<UserPassModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.addEmployee(header = header, body = payload)
      },
      mapData = { response ->
        val data = response.data?.toDomain() ?: UserPassResponse().toDomain()
        return@safeApiCallBearer data
      },
      defaultErrorMessage = "Failed to add employee",
    )
  }

  override suspend fun deleteEmployee(employeeId: Int): StateDataUtil<String> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.deleteEmployee(header = header, id = employeeId)
      },
      mapData = { response ->
        val data = response.data?.employeeName ?: ""
        return@safeApiCallBearer data
      },
      defaultErrorMessage = "Failed to delete employee",
    )
  }

  override suspend fun editEmployee(payload: EmployeeEditRequest): StateDataUtil<String> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.editEmployee(header = header, body = payload)
      },
      mapData = { response ->
        val data = response.data?.employeeName ?: ""
        return@safeApiCallBearer data
      },
      defaultErrorMessage = "Failed to edit employee",
    )
  }

  override suspend fun getListBranch(): StateDataUtil<List<BranchModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getListBranch(header)
      },
      mapData = { response ->
        val data = response.data ?: emptyList()
        data.map { it.toDomain() }
      },
      defaultErrorMessage = "Failed to get branch list",
    )
  }

  /**
   * Mengambil detail branch memakai bearer token dan memetakannya ke model domain.
   *
   * Jika data response null, repository memakai BranchResponse kosong sebagai fallback aman.
   */
  override suspend fun getDetailBranch(branchCode: String): StateDataUtil<BranchModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getDetailBranch(header = header, id = branchCode)
      },
      mapData = { response ->
        response.data?.toDomain() ?: BranchResponse().toDomain()
      },
      defaultErrorMessage = "Failed to get branch detail",
    )
  }

  /**
   * Mengirim perubahan data branch memakai bearer token dan mengembalikan message backend.
   *
   * Data response endpoint ini null, sehingga repository hanya mengambil message dari BaseResponse.
   */
  override suspend fun updateBranch(payload: BranchUpdateRequest): StateDataUtil<String> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.updateBranch(header = header, body = payload)
      },
      mapData = { response ->
        response.message.orEmpty()
      },
      defaultErrorMessage = "Failed to update branch",
    )
  }

  override suspend fun getCompanyBanner(): StateDataUtil<List<CompanyFileModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getCompanyBanner(header = header)
      },
      mapData = { response ->
        response.data.orEmpty().map { it.toDomain() }
      },
      defaultErrorMessage = "Failed get company banner",
    )
  }

  override suspend fun getCompanyPoster(): StateDataUtil<CompanyFileModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getCompanyPoster(header = header)
      },
      mapData = { response ->
        response.data?.toDomain() ?: CompanyFileResponse().toDomain()
      },
      defaultErrorMessage = "Failed get company poster",
    )
  }
}
