package id.neo.hr.data.domain.model

/**
 * Created by lucas
 * Refactored by Katherin Monica
 * 30/06/2026 - katherin.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
data class NotificationModel(
  val id: Int,
  val accountId: Int,
  val title: String,
  val desc: String,
  val icon: String,
  val isRead: Boolean,
  val createdAt: String,
  val updatedAt: String,
  val updatedBy: Int? = null,
  val deletedAt: String? = null
)