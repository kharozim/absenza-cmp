package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.AttendanceRosterModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Kharozim
 * 05/08/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
@Serializable
data class AttendanceRosterResponse(
  @SerialName("work_date")
  val workDate: String? = null,
  @SerialName("roster")
  val roster: RosterResponse? = null,
  @SerialName("attendance")
  val attendance: AttendanceResponse? = null,
  @SerialName("time_offs")
  val timeOffs: List<TimeOffResponse>? = null,
) {
  fun toDomain() = AttendanceRosterModel(
    workDate = workDate.orEmpty(),
    roster = roster?.toDomain(),
    attendance = attendance?.toDomain(),
    timeOffs = timeOffs?.map { it.toListDomain() }.orEmpty()
  )
}
