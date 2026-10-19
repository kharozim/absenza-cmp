package id.neo.hr.presentation.schedule_management.schedule_assignment

import id.neo.hr.data.domain.model.EmployeeModel

/**
 * Created by Kharozim
 * 12/08/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
data class SelectedEmployeeModel(
  val employee: EmployeeModel,
  val checked: Boolean,
)
