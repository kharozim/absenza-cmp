package id.neo.hr.presentation.util

import androidx.compose.runtime.Composable

/**
 * Created by Kharozim
 * 31/08/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
@Composable
expect fun FullScreenEffect(
  enabled: Boolean,
  hideNavigationBar: Boolean = true,
)