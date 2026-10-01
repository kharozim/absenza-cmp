package id.neo.hr.data.repository

import id.neo.hr.data.data.remote.request.BranchUpdateRequest
import id.neo.hr.data.data.remote.request.EmployeeEditRequest
import id.neo.hr.data.data.remote.request.EmployeeRequest
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.data.domain.model.CompanyFileModel
import id.neo.hr.data.domain.model.EmployeeDetailModel
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.data.domain.model.UserPassModel

interface CompanyRepository {
  suspend fun getListEmployee(): StateDataUtil<List<EmployeeModel>>
  suspend fun getDetailEmployee(id: Int): StateDataUtil<EmployeeDetailModel>
  suspend fun addEmployee(payload: EmployeeRequest): StateDataUtil<UserPassModel>
  suspend fun deleteEmployee(employeeId: Int): StateDataUtil<String>
  suspend fun editEmployee(payload: EmployeeEditRequest): StateDataUtil<String>
  suspend fun getListBranch(): StateDataUtil<List<BranchModel>>
  suspend fun getDetailBranch(branchCode: String): StateDataUtil<BranchModel>
  suspend fun updateBranch(payload: BranchUpdateRequest): StateDataUtil<String>

  suspend fun getCompanyBanner(): StateDataUtil<List<CompanyFileModel>>
  suspend fun getCompanyPoster(): StateDataUtil<CompanyFileModel>
}
