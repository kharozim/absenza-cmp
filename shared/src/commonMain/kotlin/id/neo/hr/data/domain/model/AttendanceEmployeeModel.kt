package id.neo.hr.data.domain.model

/**
 * Created by lucas
 * 02/07/2026 - katherin.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
data class AttendanceEmployeeModel(
  val id: String,
  val employeeCode: String,
  val name: String,
  val position: String,
  val photoUrl: String?,
  val shiftTime: String,
  val clockInTime: String,
  val statusNote: String
)