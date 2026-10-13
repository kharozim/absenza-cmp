package id.neo.hr.presentation.timeoff.adminView.detail

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.approve
import neohr_mp.shared.generated.resources.detail_time_off
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_check
import neohr_mp.shared.generated.resources.ic_cross
import neohr_mp.shared.generated.resources.reject
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by felic
 * 29/04/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@Composable
fun TimeOffDetailAdminScreen(
  id: Int,
  navBack: () -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: TimeOffDetailAdminViewModel = koinViewModel(),
) {
  val uiState = viewModel.uiState
  SetSystemBarAppearance(isLight = true)

  LaunchedEffect(id) {
    viewModel.getTimeOffDetail(id)
  }

  val isTokenExpired = uiState.getDataState is UiState.TokenExpired
  if (isTokenExpired) {
    LaunchedEffect(uiState.getDataState) {
      onTokenExpired()
    }
  }

  when (val apiState = uiState.getDataState) {
    is UiState.Loading -> {
      LoadingDialog(
        text = apiState.message,
        onDismissRequest = {},
      )
    }

    else -> Unit
  }

  when (val approveState = uiState.approveState) {
    is UiState.Loading -> {
      LoadingDialog(
        text = approveState.message,
        onDismissRequest = {},
      )
    }

    else -> Unit
  }

  when (val rejectState = uiState.rejectState) {
    is UiState.Loading -> {
      LoadingDialog(
        text = rejectState.message,
        onDismissRequest = {},
      )
    }

    else -> Unit
  }

  val data = uiState.data ?: return
  val isWaiting = data.status == 0
  var showConfirmReject by remember { mutableStateOf(false) }
  var showConfirmApprove by remember { mutableStateOf(false) }
  var showSuccessReject by remember { mutableStateOf(false) }
  var showSuccessApprove by remember { mutableStateOf(false) }

  LaunchedEffect(uiState.rejectState) {
    if (uiState.rejectState is UiState.Success) {
      showSuccessReject = true
    }
  }

  LaunchedEffect(uiState.approveState) {
    if (uiState.approveState is UiState.Success) {
      showSuccessApprove = true
    }
  }

  TimeOffDetailAdminContent(
    navBack = navBack,
    data = data,
    onRejectClick = { showConfirmReject = true },
    onApproveClick = { showConfirmApprove = true },
    showActions = isWaiting,
  )

  if (showConfirmReject) {
    DialogConfirmationRejectTimeOff(
      rejectionReason = uiState.rejectionReason,
      updateReason = { viewModel.updateReason(it) },
      employeeName = data.account.name,
      onCancel = { showConfirmReject = false },
      onProceed = {
        showConfirmReject = false
        viewModel.rejectTimeOff(data.id)
      }
    )
  }
  if (showSuccessReject) {
    DialogSuccessRejectTimeOff(
      employeeName = data.account.name,
      onClick = {
        showSuccessReject = false
        viewModel.resetRejectState()
        navBack()
      }
    )
  }

  if (showConfirmApprove) {
    DialogConfirmationApprovalTimeOff(
      employeeName = data.account.name,
      onCancel = { showConfirmApprove = false },
      onProceed = {
        showConfirmApprove = false
        viewModel.approveTimeOff(data.id)
      }
    )
  }
  if (showSuccessApprove) {
    DialogSuccessApprovalTimeOff(
      employeeName = data.account.name,
      onClick = {
        showSuccessApprove = false
        viewModel.resetApproveState()
        navBack()
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeOffDetailAdminContent(
  navBack: () -> Unit,
  data: TimeOffDetailModel,
  onRejectClick: () -> Unit,
  onApproveClick: () -> Unit,
  showActions: Boolean,
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
            style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp, color = Colors.Gray800)
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null
            )
          }
        },
        actions = {
          Spacer(modifier = Modifier.width(48.dp))
        }
      )
    },
    bottomBar = {
      if (showActions) {
        LayoutBottom(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
        ) {
          TextButtonCustom(
            text = stringResource(Res.string.reject),
            onClick = onRejectClick,
            trailingIcon = Res.drawable.ic_cross,
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(16.dp))
          ButtonCustom(
            text = stringResource(Res.string.approve),
            onClick = onApproveClick,
            trailingIcon = Res.drawable.ic_check,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  ) { padding ->
    TimeOffDetailWidget(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize(),
      data = data
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimeOffDetailAdmin() {
  val mockData = TimeOffDetailModel(
    id = 17,
    account = TimeOffAccountModel(
      id = 1,
      name = "Siti Rahmawati",
      email = "siti@neo.id",
    ),
    approvalAt = "",
    approvalBy = 0,
    createdAt = "2026-05-20T10:37:01+07:00",
    description = "Pengajuan izin cuti melahirkan selama 3 bulan.",
    docs = listOf(
      TimeOffDocModel(
        id = 1,
        timeOffId = 17,
        type = "pdf",
        url = "https://link.com/sample-file.pdf",
        fileSize = "1.2 MB"
      ),
    ),
    end = "2026-08-20",
    deletedAt = "",
    note = "",
    start = "2026-05-20",
    status = 0,
    statusLabel = "Waiting",
    totalDay = 90,
    type = "MATERNITY",
    typeLabel = "Cuti Melahirkan",
    updatedAt = "2026-05-20T10:37:01+07:00",
  )

  TimeOffDetailAdminContent(
    data = mockData,
    navBack = {},
    onApproveClick = {},
    onRejectClick = {},
    showActions = true,
  )
}
