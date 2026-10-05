package id.neo.hr.presentation.widget

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.clock_in
import neohr_mp.shared.generated.resources.ic_arrow_left
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/*
 * Created by Kharozim
 * 25/08/25 - kharozim.wrk@gmail.com
 * Copyright (c) 2025. NeoHR
 * All Rights Reserved
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarCustom(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit = {},
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
  expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
  windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
  colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = Color.Transparent
  ),
  scrollBehavior: TopAppBarScrollBehavior? = null,
) {

  TopAppBar(
    title = title,
    modifier = modifier,
    colors = colors,
    navigationIcon = navigationIcon,
    actions = actions,
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    scrollBehavior = scrollBehavior
  )

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TopAppBarCustomPreview() {
  AppTheme {
    TopAppBarCustom(
      title = {
        Text(
          stringResource(Res.string.clock_in),
          style = TextStyleCustom.ExtraBold.copy(
            fontSize = 20.sp,
            color = Colors.Gray800,
            textAlign = TextAlign.Center
          ),
          modifier = Modifier.fillMaxWidth()
        )
      },

      navigationIcon = {
        IconButton(onClick = {}) {
          Icon(
            painter = painterResource(Res.drawable.ic_arrow_left),
            contentDescription = null,
            tint = Colors.Gray800
          )
        }
      },
      actions = {
        Spacer(modifier = Modifier.width(48.dp))
      },
    )
  }

}