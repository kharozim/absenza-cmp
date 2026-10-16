package id.neo.hr.presentation.timeoff.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.data.domain.model.TimeOffAccountModel
import id.neo.hr.data.domain.model.TimeOffDetailModel
import id.neo.hr.data.domain.model.TimeOffDocModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.timeoff.TimeOffDetailWidget
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.are_you_sure_want_to_delete_this_time_off
import neohr_mp.shared.generated.resources.back_to_apps
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.delete
import neohr_mp.shared.generated.resources.delete_time_off_request_confirmation
import neohr_mp.shared.generated.resources.detail_time_off
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_delete
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.success
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by felic
 * 20/04/2026 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@Composable
fun TimeOffDetailScreen(
  id: Int,
  navBack: () -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: TimeOffDetailViewModel = koinViewModel(),
) {
  val uiState = viewModel.uiState

  LaunchedEffect(id) {
    viewModel.getTimeOffDetail(id)
  }

  SetSystemBarAppearance(isLight = true)
  val isTokenExpired =
    uiState.getDataState is UiState.TokenExpired || uiState.deleteState is UiState.TokenExpired
  if (isTokenExpired) {
    LaunchedEffect(uiState.deleteState, uiState.getDataState) {
      onTokenExpired()
    }
  }

  when (val apiState = uiState.getDataState) {
    is UiState.Loading -> {
      LoadingDialog(
        text = apiState.message,
        onDismissRequest = { },
      )
    }

    else -> Unit
  }

  when (val apiState = uiState.deleteState) {
    is UiState.Loading -> {
      LoadingDialog(
        text = apiState.message,
        onDismissRequest = { },
      )
    }

    UiState.Success -> {
      DialogSuccessDelete(
        onDismiss = {
          viewModel.updateState(uiState.copy(getDataState = null))
          navBack()
        },
      )
    }

    else -> Unit
  }

  if (uiState.showDialogDelete) {
    DialogConfirmationDelete(
      onDismiss = {
        viewModel.updateState(uiState.copy(showDialogDelete = false))
      },
      onProcessClick = {
        viewModel.updateState(uiState.copy(showDialogDelete = false))
        viewModel.delete(uiState.data?.id ?: 0)
      }
    )
  }

  uiState.data?.let { data ->
    TimeOffDetailContent(
      navBack = navBack,
      data = data,
      onDeleteClick = {
        viewModel.updateState(uiState.copy(showDialogDelete = true))
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogConfirmationDelete(onDismiss: () -> Unit, onProcessClick: () -> Unit) {
  BottomSheetDialogCustom(
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.delete_time_off_request_confirmation),
    description = {
      Text(
        stringResource(Res.string.are_you_sure_want_to_delete_this_time_off),
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500
        )
      )
    },
    onDismissRequest = onDismiss,
    actions = {
      ButtonCustom(
        onClick = onDismiss,
        text = stringResource(Res.string.cancel),
        modifier = Modifier.weight(1f)
      )
      TextButtonCustom(
        onClick = { onProcessClick() },
        text = stringResource(Res.string.proceed),
        modifier = Modifier.weight(1f)
      )
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogSuccessDelete(onDismiss: () -> Unit) {
  BottomSheetDialogCustom(
    icon = Res.drawable.ic_dialog_success,
    title = stringResource(Res.string.success),
    description = {
      Text(
        "Pengajuan cuti berhasil dihapus",
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500
        ),
      )
    },
    onDismissRequest = onDismiss,
  ) {
    TextButtonCustom(
      onClick = onDismiss,
      text = stringResource(Res.string.back_to_apps),
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeOffDetailContent(
  navBack: () -> Unit = {},
  data: TimeOffDetailModel,
  onDeleteClick: () -> Unit,
) {
  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.detail_time_off),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp, color = Colors.Gray800),
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
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
    },
    bottomBar = {
      if (data.status == 0) {
        LayoutBottom(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        ) {
          IconAction(
            modifier = Modifier.weight(1f),
            text = {
              Text(
                stringResource(Res.string.delete),
                style = TextStyleCustom.Bold.copy(color = Colors.Red500),
              )
            },
            icon = {
              Icon(
                painterResource(Res.drawable.ic_delete),
                contentDescription = null,
                tint = Colors.Red500,
              )
            },
            onClick = onDeleteClick
          )
        }
      }
    },
  ) { padding ->
    TimeOffDetailWidget(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize(),
      data = data
    )
  }
}

@Composable
private fun IconAction(
  modifier: Modifier = Modifier,
  icon: @Composable (() -> Unit)? = null,
  text: @Composable (() -> Unit)? = null,
  onClick: () -> Unit = {},
) {
  Column(
    modifier = modifier
      .clip(shape = RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
  ) {
    icon?.invoke()
    text?.invoke()
  }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimeOffDetail() {
  val mockData = TimeOffDetailModel(
    id = 17,
    account = TimeOffAccountModel(
      id = 1,
      name = "Ahmad Pratama",
      email = "ahmad@example.com",
    ),
    approvalAt = "",
    approvalBy = 0,
    createdAt = "2026-05-20T10:37:01+07:00",
    description = "Pengajuan cuti tahunan selama 3 hari kerja.",
    docs = listOf(
      TimeOffDocModel(
        id = 1,
        timeOffId = 17,
        type = "pdf",
        url = "https://link.com/sample-file.pdf",
        fileSize = "1.3 KB"
      ),
    ),
    end = "2026-05-22",
    deletedAt = "",
    note = "sudah disetujui",
    start = "2026-05-20",
    status = 0,
    statusLabel = "Waiting",
    totalDay = 3,
    type = "ANNUAL",
    typeLabel = "Cuti Tahunan",
    updatedAt = "2026-05-20T10:37:01+07:00",
  )

  TimeOffDetailContent(
    data = mockData,
    onDeleteClick = {}
  )
}
