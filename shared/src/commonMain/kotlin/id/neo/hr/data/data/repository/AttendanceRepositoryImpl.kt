package id.neo.hr.data.data.repository

import id.neo.hr.data.data.remote.api.ApiAttendance
import id.neo.hr.data.data.remote.request.CLockInRequest
import id.neo.hr.data.data.remote.request.ClockOutRequest
import id.neo.hr.data.data.remote.request.PublishRosterRequest
import id.neo.hr.data.data.remote.request.UpdateRosterRequest
import id.neo.hr.data.data.remote.response.AttendanceResponse
import id.neo.hr.data.data.remote.response.AttendanceRosterResponse
import id.neo.hr.data.data.remote.response.PublishRosterResponse
import id.neo.hr.data.data.remote.response.mapToDomain
import id.neo.hr.data.data.util.ErrorModel
import id.neo.hr.data.data.util.NetworkUtil
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.AttendanceEmployeeModel
import id.neo.hr.data.domain.model.AttendanceModel
import id.neo.hr.data.domain.model.AttendanceRosterModel
import id.neo.hr.data.domain.model.PublishRosterModel
import id.neo.hr.data.domain.model.RosterModel
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

class AttendanceRepositoryImpl(
  private val api: ApiAttendance,
  private val session: SessionUtil,
) : AttendanceRepository {

  private companion object {
    val CONTENT_DISPOSITION_FILENAME_REGEX = Regex(
      pattern = """filename\s*=\s*(?:"([^"]+)"|([^;]+))""",
      option = RegexOption.IGNORE_CASE,
    )
  }

  override suspend fun getEmployeeAttendanceStatus(
    uiTab: String,
  ): StateDataUtil<List<AttendanceEmployeeModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())

        val apiStatusParam = when (uiTab) {
          "Tepat Waktu" -> "on_time"
          "Terlambat" -> "late"
          "Belum Absen" -> "no_attendance_yet"
          "Cuti" -> "leave"
          else -> "on_time"
        }

        api.getEmployeeAttendanceStatus(header = header, status = apiStatusParam)
      },
      mapData = { response ->
        response.data
          ?.map { it.mapToDomain(uiTab) }
          .orEmpty()
      },
      defaultErrorMessage = "Failed to get employee attendance status",
    )
  }

  override suspend fun clockIn(
    request: CLockInRequest,
  ): StateDataUtil<AttendanceModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.clockIn(header = header, body = request)
      },
      mapData = { response ->
        val data = response?.data ?: AttendanceResponse()
        return@safeApiCallBearer data.toDomain()
      },
      defaultErrorMessage = "Failed to submit clock in",
    )
  }

  override suspend fun clockOut(
    request: ClockOutRequest,
  ): StateDataUtil<AttendanceModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.clockOut(
          header = header,
          body = request
        )
      },
      mapData = { response ->
        val data = response?.data ?: AttendanceResponse()
        return@safeApiCallBearer data.toDomain()
      },
      defaultErrorMessage = "Failed to submit clock out",
    )
  }

  override suspend fun getAttendanceToday(): StateDataUtil<AttendanceRosterModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val todayDate = FormatterUtil.dateToString(Clock.System.now(), "yyyy-MM-dd")
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getAttendanceList(header, startDate = todayDate, endDate = todayDate)
      },
      mapData = { response ->
        val data = response.data?.firstOrNull() ?: AttendanceRosterResponse()
        return@safeApiCallBearer data.toDomain()
      },
      defaultErrorMessage = "Failed to get attendance today",
    )
  }

  override suspend fun getAttendanceList(
    startDate: String,
    endDate: String,
  ): StateDataUtil<List<AttendanceRosterModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getAttendanceList(
          header = header,
          startDate = startDate,
          endDate = endDate,
        )
      },
      mapData = { response ->
        return@safeApiCallBearer response?.data
          ?.asSequence()
          ?.map { it.toDomain() }
          ?.toList()
          .orEmpty()
      },
      defaultErrorMessage = "Failed to get list attendance",
    )
  }

  override suspend fun getAttendanceDetail(
    id: Int,
  ): StateDataUtil<AttendanceModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getAttendanceDetail(
          header = header,
          id = id,
        )
      },
      mapData = { response ->
        val data = response?.data ?: AttendanceResponse()
        return@safeApiCallBearer data.toDomain()
      },
      defaultErrorMessage = "Failed to get attendance detail",
    )
  }

  override suspend fun getAttendanceListAdmin(
    accountId: Int,
    startDate: String,
    endDate: String,
  ): StateDataUtil<List<AttendanceRosterModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getAttendanceListAdmin(
          header = header,
          accountId = accountId,
          startDate = startDate,
          endDate = endDate,
        )
      },
      mapData = { response ->
        return@safeApiCallBearer response?.data
          ?.asSequence()
          ?.map { it.toDomain() }
          ?.toList()
          .orEmpty()
      },
      defaultErrorMessage = "Failed to get list attendance",
    )
  }

  override suspend fun downloadAttendanceReport(
    startDate: String,
    endDate: String,
    destinationFolderUri: String,
  ): StateDataUtil<String> {
    // TODO: handle nanti jangan sekarang
    return StateDataUtil.Error(ErrorModel(message = "TODO"))
//    val parsedDestination = runCatching { destinationFolderUri.toUri() }.getOrNull()
//      ?: return StateDataUtil.Error(ErrorModel(message = "Invalid report destination"))
//    if (
//      destinationFolderUri.isBlank() ||
//      parsedDestination.scheme != ContentResolver.SCHEME_CONTENT
//    ) {
//      return StateDataUtil.Error(ErrorModel(message = "Invalid report destination"))
//    }
//
//    val isValidDateRange = runCatching {
//      val parsedStartDate = LocalDate.parse(startDate)
//      val parsedEndDate = LocalDate.parse(endDate)
//      !parsedEndDate.isBefore(parsedStartDate)
//    }.getOrDefault(false)
//    if (!isValidDateRange) {
//      return StateDataUtil.Error(ErrorModel(message = "End date cannot be earlier than start date"))
//    }
//
//    return NetworkUtil.safeFileApiCallBearer(
//
//      call = {
//        val header = NetworkUtil.generateHeader(
//          context,
//          NetworkUtil.HEADER_TOKEN_BEARER,
//        ).toMutableMap().apply {
//          this["Accept"] = "text/csv"
//        }
//        api.downloadAttendanceReport(
//          header = header,
//          startDate = startDate,
//          endDate = endDate,
//        )
//      },
//      consumeBody = { responseBody, responseHeaders ->
//        val responseContentType = responseHeaders["Content-Type"]
//          ?: responseBody.contentType()?.toString().orEmpty()
//        if (responseContentType.contains("json", ignoreCase = true)) {
//          throw IOException("Unexpected JSON response for attendance report")
//        }
//
//        val fileName = extractDownloadFileName(
//          contentDisposition = responseHeaders["Content-Disposition"],
//          fallback = "attendance-report_${startDate}_${endDate}.csv",
//        )
//        val destinationFolder = DocumentsContract.buildDocumentUriUsingTree(
//          parsedDestination,
//          DocumentsContract.getTreeDocumentId(parsedDestination),
//        )
//        val destinationFile = DocumentsContract.createDocument(
//          context.contentResolver,
//          destinationFolder,
//          "text/csv",
//          fileName,
//        ) ?: throw IOException("Unable to create report file")
//
//        try {
//          val outputStream = context.contentResolver.openOutputStream(destinationFile, "w")
//            ?: throw IOException("Unable to open report destination")
//          val copiedBytes = responseBody.byteStream().use { input ->
//            outputStream.use { output -> input.copyTo(output) }
//          }
//          if (copiedBytes <= 0L) {
//            throw IOException("Downloaded report is empty")
//          }
//          destinationFile.toString()
//        } catch (exception: Exception) {
//          runCatching {
//            DocumentsContract.deleteDocument(context.contentResolver, destinationFile)
//          }
//          throw exception
//        }
//      },
//      defaultErrorMessage = "Failed to download attendance report",
//    )
  }

  private fun extractDownloadFileName(
    contentDisposition: String?,
    fallback: String,
  ): String {
    val headerFileName = contentDisposition
      ?.let { CONTENT_DISPOSITION_FILENAME_REGEX.find(it) }
      ?.groupValues
      ?.drop(1)
      ?.firstOrNull { it.isNotBlank() }
      ?.trim()
      ?.substringAfterLast('/')
      ?.substringAfterLast('\\')
      ?.takeIf { it.isNotBlank() && it != "." && it != ".." }

    return headerFileName ?: fallback
  }

  override suspend fun getAdminRosterList(
    employeeId: Int,
    startDate: String,
    endDate: String,
  ): StateDataUtil<List<RosterModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getAdminRosterList(
          header = header,
          employeeId = employeeId,
          startDate = startDate,
          endDate = endDate
        )
      },
      mapData = { response ->
        response?.data?.map { it.toDomain() }.orEmpty()
      },
      defaultErrorMessage = "Failed to get roster list"
    )
  }

  override suspend fun publishRoster(
    employeeId: Int,
    startDate: String,
    endDate: String,
  ): StateDataUtil<PublishRosterModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        val body = PublishRosterRequest(
          employeeId = employeeId,
          startDate = startDate,
          endDate = endDate
        )
        api.publishRoster(header = header, body = body)
      },
      mapData = { response ->
        (response?.data ?: PublishRosterResponse()).toDomain()
      },
      defaultErrorMessage = "Failed to publish roster"
    )
  }

  override suspend fun updateRoster(
    request: UpdateRosterRequest,
  ): StateDataUtil<String> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.updateRoster(header = header, body = request)
      },
      mapData = {
        "Successfully updated roster"
      },
      defaultErrorMessage = "Failed to update roster"
    )
  }
}
