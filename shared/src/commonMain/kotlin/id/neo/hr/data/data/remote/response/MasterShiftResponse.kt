package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.MasterShiftModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by lucas
 * 22/07/2026 - katherinmonica@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
@Serializable
data class MasterShiftResponse(
  @SerialName("id") val id: Int? = null,
  @SerialName("name") val name: String? = null,
  @SerialName("code") val code: String? = null,
  @SerialName("color_hex") val colorHex: String? = null,
  @SerialName("start_time") val startTime: String? = null,
  @SerialName("end_time") val endTime: String? = null,
  @SerialName("end_day_offset") val endDayOffset: Int? = null,
  @SerialName("break_minutes") val breakMinutes: Int? = null,
  @SerialName("total_work_minutes") val totalWorkMinutes: Int? = null,
  @SerialName("is_active") val isActive: Boolean? = null,
  @SerialName("created_at") val createdAt: String? = null,
  @SerialName("updated_at") val updatedAt: String? = null,
)

fun MasterShiftResponse.toDomain(): MasterShiftModel {
  return MasterShiftModel(
    id = id ?: 0,
    name = name.orEmpty(),
    code = code.orEmpty(),
    colorHex = colorHex ?: "#000000",
    startTime = startTime.orEmpty(),
    endTime = endTime.orEmpty(),
    endDayOffset = endDayOffset ?: 0,
    breakMinutes = breakMinutes ?: 0,
    totalWorkMinutes = totalWorkMinutes ?: 0,
    isActive = isActive ?: false,
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
  )
}
