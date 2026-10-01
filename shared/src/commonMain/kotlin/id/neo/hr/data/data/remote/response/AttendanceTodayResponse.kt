package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Created by lucas
 * 02/07/2026 - katherin.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
@Serializable
data class AttendanceTodayResponse(
  @SerialName("id") val id: Int? = null,
  @SerialName("clock_in_time") val clockInTime: String? = null,
  @SerialName("clock_out_time") val clockOutTime: String? = null,
)
