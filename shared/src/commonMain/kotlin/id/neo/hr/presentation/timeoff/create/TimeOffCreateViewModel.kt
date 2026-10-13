package id.neo.hr.presentation.timeoff.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.TimeOffRequest
import id.neo.hr.data.data.remote.request.TimeOffRequestDoc
import id.neo.hr.data.data.util.BuildConfig
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.TimeOffTypeModel
import id.neo.hr.data.repository.TimeOffRepository
import id.neo.hr.data.service.ImageUploadService
import id.neo.hr.presentation.timeoff.TimeOffDocModel
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.dashIfEmpty
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Created by felic
 * 21/04/2026 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
class TimeOffCreateViewModel(
  private val repo: TimeOffRepository,
  private val session: SessionUtil,
  private val imageUploadService: ImageUploadService,
) : ViewModel() {

  var uiState by mutableStateOf(TimeOffUiState())
    private set

  /**
   * Mengunggah dokumen pendukung time off (gambar atau PDF).
   * URL hasil upload disimpan ke daftar dokumen untuk payload submit.
   */
  fun uploadDocument(fileName: String, fileBytes: ByteArray) {
    viewModelScope.launch {
      try {
        uiState = uiState.copy(uploadDocumentState = UiState.Loading("Prepare document.."))

        val extension = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
        if (!isAllowedDocument(extension)) {
          uiState = uiState.copy(
            uploadDocumentState = UiState.Error("Dokumen harus berupa gambar atau PDF"),
          )
          return@launch
        }

        if (fileBytes.size > MAX_DOCUMENT_SIZE_BYTES) {
          uiState = uiState.copy(
            uploadDocumentState = UiState.Error("Ukuran file maksimal 5MB"),
          )
          return@launch
        }

        val login = session.loginModel.firstOrNull()
        val companyId = login?.company?.id.orEmpty()
        val accountId = login?.id ?: 0
        val safeFileName = "${accountId}_${kotlin.time.Clock.System.now().toEpochMilliseconds()}_$fileName"

        val uploadResult = imageUploadService.uploadImage(
          imageBytes = fileBytes,
          fileName = safeFileName,
          directory = "time-off",
          companyId = companyId,
          serverImageBaseUrl = session.settingModel.first()?.baseUrlImg.orEmpty(),
          onProgress = { current, total ->
            uiState = uiState.copy(
              uploadDocumentState = UiState.Loading("Upload.. $current/$total")
            )
          }
        )

        if (uploadResult.isFailure) {
          uiState = uiState.copy(
            uploadDocumentState = UiState.Error("Gagal mengunggah dokumen pendukung"),
          )
          return@launch
        }

        val uploadedUrl = uploadResult.getOrNull()?.url.orEmpty()
        val fileSize = formatFileSize(fileBytes.size.toLong())
        val document = TimeOffDocModel(
          name = fileName,
          link = uploadedUrl,
          type = if (extension == "pdf") "pdf" else extension,
          fileSize = fileSize
        )

        uiState = uiState.copy(
          listDocument = uiState.listDocument + document,
          uploadDocumentState = UiState.Success,
          errorForm = null,
        )
      } catch (e: Exception) {
        if (e is CancellationException) throw e
        uiState = uiState.copy(
          uploadDocumentState = UiState.Error(
            e.message?.filterMessageError() ?: "Gagal mengunggah dokumen pendukung"
          ),
        )
      }
    }
  }

  fun updateStartDate(timestamp: Long) {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val formatted = FormatterUtil.dateToString(instant, "dd/MM/yyyy")
    val total = calculateDays(timestamp, uiState.endDate)

    uiState = uiState.copy(
      startDate = timestamp,
      startDateDisplay = formatted,
      totalLeaves = total,
      errorForm = null
    )
  }

  fun updateEndDate(timestamp: Long) {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val formatted = FormatterUtil.dateToString(instant, "dd/MM/yyyy")
    val startDate = uiState.startDate
    if (startDate != null && timestamp < startDate) {
      uiState = uiState.copy(
        endDate = timestamp,
        endDateDisplay = formatted,
        totalLeaves = 0,
        errorForm = "End date must be greater than or equal to start date",
      )
      return
    }

    val total = calculateDays(uiState.startDate, timestamp)
    uiState = uiState.copy(
      endDate = timestamp,
      endDateDisplay = formatted,
      totalLeaves = total,
      errorForm = null
    )
  }

  fun submitCreateTimeOff() {
    viewModelScope.launch {
      try {
        val startDate = uiState.startDate ?: 0L
        val endDate = uiState.endDate ?: 0L
        val description = uiState.description.trim()

        uiState = uiState.copy(submitState = UiState.Loading("Submit time off.."))

        val start = formatDatePickerStartOfDay(startDate)
        val end = formatDatePickerEndOfDay(endDate)
        val request = TimeOffRequest(
          type = uiState.type?.type.orEmpty(),
          start = start,
          end = end,
          description = description,
          totalDay = uiState.totalLeaves,
          docs = uiState.listDocument.map { document ->
            TimeOffRequestDoc(
              type = document.type,
              url = document.link,
            )
          },
        )

        when (val submitResult = repo.createTimeOff(request)) {
          is StateDataUtil.Error -> {
            uiState = uiState.copy(
              submitState = submitResult.toUiStateError("Failed create time off"),
            )
          }

          is StateDataUtil.Success -> {
            uiState = uiState.copy(submitState = UiState.Success)
          }
        }
      } catch (e: Exception) {
        if (e is CancellationException) throw e
        uiState = uiState.copy(
          submitState = UiState.Error(e.message?.filterMessageError().orEmpty()),
        )
      }
    }
  }

  fun removeDoc(document: TimeOffDocModel) {
    val updatedList = uiState.listDocument.filter { it.id != document.id }
    uiState = uiState.copy(listDocument = updatedList)
  }

  fun setInitialData() {
    viewModelScope.launch {
      try {
        val login = session.loginModel.firstOrNull()
        uiState = uiState.copy(
          name = login?.accountName.dashIfEmpty(),
          typeState = UiState.Loading("Get data..")
        )
        when (val typeTimeOff = repo.getTimeOffTypeList()) {
          is StateDataUtil.Error -> {
            uiState = uiState.copy(
              typeState = typeTimeOff.toUiStateError("Failed get type time off")
            )
          }

          is StateDataUtil.Success -> {
            uiState = uiState.copy(
              listTimeOffType = typeTimeOff.data.orEmpty(),
              typeState = UiState.Success,
            )
          }
        }
      } catch (e: Exception) {
        if (e is CancellationException) throw e
        uiState = uiState.copy(
          typeState = UiState.Error(e.message?.filterMessageError().orEmpty()),
        )
      }
    }
  }

  private fun calculateDays(start: Long?, end: Long?): Int {
    if (start == null || end == null) return 0
    if (end < start) return 0
    val diffDays = (end - start) / (24 * 60 * 60 * 1000)
    return (diffDays.toInt() + 1)
  }

  private fun formatDatePickerStartOfDay(timestamp: Long): String {
    val date = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${date}T00:00:00+07:00"
  }

  private fun formatDatePickerEndOfDay(timestamp: Long): String {
    val date = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${date}T23:59:59+07:00"
  }

  fun updateState(newState: TimeOffUiState) {
    uiState = newState
  }

  private fun isAllowedDocument(extension: String): Boolean {
    return extension in setOf("jpg", "jpeg", "png", "webp", "heic", "pdf")
  }

  private fun formatFileSize(bytes: Long): String {
    return when {
      bytes >= 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
      bytes >= 1024 -> "${bytes / 1024} KB"
      else -> "$bytes B"
    }
  }

  private companion object {
    const val MAX_DOCUMENT_SIZE_BYTES = 5L * 1024L * 1024L
  }
}

data class TimeOffUiState(
  val name: String = "",
  val type: TimeOffTypeModel? = null,
  val startDate: Long? = null,
  val endDate: Long? = null,
  val startDateDisplay: String = "",
  val endDateDisplay: String = "",
  val totalLeaves: Int = 0,
  val description: String = "",
  val supportingDoc: String = "",
  val listDocument: List<TimeOffDocModel> = emptyList(),
  val listTimeOffType: List<TimeOffTypeModel> = emptyList(),
  val errorForm: String? = null,
  val typeState: UiState? = null,
  val submitState: UiState? = null,
  val uploadDocumentState: UiState? = null,
)
