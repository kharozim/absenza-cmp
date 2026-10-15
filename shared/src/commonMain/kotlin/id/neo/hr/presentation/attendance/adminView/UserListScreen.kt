package id.neo.hr.presentation.attendance.adminView

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.ToastType
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.MonthDropdown
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.attendance_list
import neohr_mp.shared.generated.resources.attendance_report_download_success
import neohr_mp.shared.generated.resources.attendance_report_preview_unavailable
import neohr_mp.shared.generated.resources.attendance_report_ready
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.download
import neohr_mp.shared.generated.resources.download_attendance_report_csv
import neohr_mp.shared.generated.resources.download_destination
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_arrow_right
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.ic_download
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.ic_search
import neohr_mp.shared.generated.resources.preview_file
import neohr_mp.shared.generated.resources.report_month
import neohr_mp.shared.generated.resources.search
import neohr_mp.shared.generated.resources.select_download_folder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by felic
 * 30/04/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@Composable
fun UserListScreen(
  navBack: () -> Unit,
  navToUserAttendanceList: (model: UserListModel) -> Unit,
  onTokenExpired: () -> Unit,
) {
  SetSystemBarAppearance(isLight = true)
  val messagePreviewReportFailed = stringResource(Res.string.attendance_report_preview_unavailable)
  val viewModel: UserListViewModel = koinViewModel()
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.getData()
  }

  when (state.getDataState) {
    is UiState.Loading -> {
      LoadingDialog(
        (state.getDataState as UiState.Loading).message,
        onDismissRequest = {}
      )
    }

    UiState.TokenExpired -> {
      LaunchedEffect(state.getDataState) {
        onTokenExpired()
      }
    }

    else -> Unit
  }

  if (state.downloadState is UiState.TokenExpired) {
    LaunchedEffect(state.downloadState) {
      onTokenExpired()
    }
  }

  if (state.isDownloadDialogVisible) {
    DownloadAttendanceReportDialog(
      monthYear = state.downloadMonthYear,
      folderPath = state.downloadFolderPath,
      downloadState = state.downloadState,
      onMonthSelected = viewModel::updateDownloadMonth,
      onSelectFolder = {
        viewModel.updateDownloadFolder(
          folderUri = "Downloads",
          folderPath = "Downloads",
        )
      },
      onDownload = viewModel::downloadAttendanceReport,
      onDismiss = viewModel::dismissDownloadDialog,
    )
  }

  if (state.isPreviewDialogVisible) {
    DownloadSuccessDialog(
      onPreview = {
        if (state.downloadedFileUri.isNotBlank()) {
          ToastManager.show("Saved to: ${state.downloadedFileUri}", type = ToastType.Success)
          viewModel.dismissPreviewDialog()
        } else {
          ToastManager.show(messagePreviewReportFailed, type = ToastType.Error)
        }
      },
      onDismiss = viewModel::dismissPreviewDialog,
    )
  }

  UserListContent(
    state = state,
    onRefresh = {
      viewModel.getData(isRefresh = true)
    },
    onSearchChange = {
      viewModel.updateSearch(it)
    },
    navBack = navBack,
    navToUserAttendanceList = navToUserAttendanceList,
    onDownloadClick = viewModel::showDownloadDialog,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserListContent(
  navBack: () -> Unit,
  navToUserAttendanceList: (model: UserListModel) -> Unit,
  state: UserListState,
  onRefresh: () -> Unit,
  onSearchChange: (String) -> Unit,
  onDownloadClick: () -> Unit,
) {
  val pullRefreshState = rememberPullToRefreshState()

  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.attendance_list),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp, color = Colors.Gray800)
          )
        },
        navigationIcon = {
          IconButton(onClick = { navBack() }) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.Gray800
            )
          }
        },
        actions = {
          IconButton(onClick = onDownloadClick) {
            Icon(
              painter = painterResource(Res.drawable.ic_download),
              contentDescription = stringResource(Res.string.download_attendance_report_csv),
              tint = Colors.Purple800,
            )
          }
        }
      )
    },
  ) { paddingValues ->
    PullToRefreshBox(
      modifier = Modifier.padding(paddingValues),
      isRefreshing = state.isRefreshing,
      state = pullRefreshState,
      onRefresh = onRefresh
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp)
      ) {
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar(
          value = state.searchQuery,
          onValueChange = onSearchChange
        )
        Spacer(modifier = Modifier.height(18.dp))
        LazyColumn(
          modifier = Modifier.fillMaxSize()
        ) {
          if (state.listEmployeeFiltered.isEmpty()) {
            item {
              Text(
                text = if (state.getDataState is UiState.Error) {
                  state.getDataState.message
                } else {
                  "Data not found"
                },
                style = TextStyleCustom.SemiBold.copy(
                  color = Colors.Gray400,
                  textAlign = TextAlign.Center
                ),
                modifier = Modifier
                  .fillMaxSize()
                  .padding(16.dp)
              )
            }
          } else {
            itemsIndexed(
              items = state.listEmployeeFiltered,
              key = { _, item -> item.id }
            ) { _, model ->
              UserListItem(
                profilePict = model.accountUrlPhoto,
                name = model.accountName,
                employeeNumber = model.employeeCode,
                navToUserAttendanceList = {
                  navToUserAttendanceList(
                    UserListModel(
                      accountId = model.id,
                      profilePict = model.accountUrlPhoto,
                      name = model.accountName,
                      employeeNumber = model.accountEmail
                    )
                  )
                },
                role = model.accountRole,
                position = model.accountPosition
              )
              Spacer(modifier = Modifier.height(12.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SearchBar(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(color = Colors.Gray10, shape = RoundedCornerShape(10.dp))
      .border(width = 1.dp, color = Colors.Gray50, shape = RoundedCornerShape(10.dp))
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      painter = painterResource(Res.drawable.ic_search),
      contentDescription = null,
      tint = Colors.Purple800
    )
    Spacer(modifier = Modifier.width(12.dp))
    BasicTextField(
      singleLine = true,
      value = value,
      onValueChange = onValueChange,
      textStyle = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray800),
      modifier = Modifier.fillMaxWidth(),
      decorationBox = { innerTextField ->
        if (value.isEmpty()) {
          Text(
            text = stringResource(Res.string.search),
            style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray400)
          )
        }
        innerTextField()
      }
    )
  }
}

@Composable
private fun UserListItem(
  navToUserAttendanceList: () -> Unit,
  profilePict: String,
  name: String,
  role: String,
  position: String,
  employeeNumber: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(shape = RoundedCornerShape(12.dp))
      .clickable(onClick = navToUserAttendanceList)
      .padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AsyncImage(
      model = profilePict,
      contentDescription = "Profile Picture",
      modifier = Modifier
        .size(36.dp)
        .clip(RoundedCornerShape(12.dp)),
      error = painterResource(Res.drawable.ic_person_placeholder),
      placeholder = painterResource(Res.drawable.ic_person_placeholder)
    )
    Spacer(modifier = Modifier.width(13.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = name,
        style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          "${role}-${position}",
          style = TextStyleCustom.Regular.copy(
            color = Colors.Gray400,
          ),
        )
        Text(
          employeeNumber,
          style = TextStyleCustom.SemiBold.copy(
            color = Colors.Gray500,
            fontSize = 10.sp
          ),
          modifier = Modifier
            .padding(start = 4.dp)
            .background(Colors.Purple50, CircleShape)
            .padding(vertical = 2.dp, horizontal = 8.dp)
        )
      }
    }
    Icon(
      painter = painterResource(Res.drawable.ic_arrow_right),
      contentDescription = null,
      tint = Colors.Purple800
    )
  }
  HorizontalDivider(
    modifier = Modifier.padding(vertical = 8.dp),
    thickness = 1.dp,
    color = Colors.Purple50
  )
}

@Composable
private fun DownloadAttendanceReportDialog(
  monthYear: String,
  folderPath: String,
  downloadState: UiState?,
  onMonthSelected: (String, Int) -> Unit,
  onSelectFolder: () -> Unit,
  onDownload: () -> Unit,
  onDismiss: () -> Unit,
) {
  val isDownloading = downloadState is UiState.Loading

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Colors.White,
    title = {
      Text(
        text = stringResource(Res.string.download_attendance_report_csv),
        style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp),
      )
    },
    text = {
      Column {
        Text(
          text = stringResource(Res.string.report_month),
          style = TextStyleCustom.SemiBold.copy(color = Colors.Gray700),
        )
        Spacer(modifier = Modifier.height(8.dp))
        MonthDropdown(
          currentMonthYear = monthYear,
          onMonthYearSelected = onMonthSelected,
          modifier = Modifier.clip(RoundedCornerShape(10.dp)),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = stringResource(Res.string.download_destination),
          style = TextStyleCustom.SemiBold.copy(color = Colors.Gray700),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Colors.Purple100, RoundedCornerShape(10.dp))
            .clickable(enabled = !isDownloading, onClick = onSelectFolder)
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            painter = painterResource(Res.drawable.ic_download),
            contentDescription = null,
            tint = Colors.Purple800,
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = folderPath.ifBlank {
              stringResource(Res.string.select_download_folder)
            },
            modifier = Modifier.weight(1f),
            style = TextStyleCustom.Medium.copy(
              color = if (folderPath.isBlank()) Colors.Gray400 else Colors.Gray800,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
          )
        }

        if (downloadState is UiState.Error) {
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = downloadState.message,
            style = TextStyleCustom.Medium.copy(color = Colors.Red500),
          )
        }
      }
    },
    confirmButton = {
      if (isDownloading) {
        CircularProgressIndicator(
          modifier = Modifier.padding(14.dp).size(24.dp),
          color = Colors.Purple500,
          strokeWidth = 3.dp,
        )
      } else {
        ButtonCustom(
          onClick = onDownload,
          enabled = folderPath.isNotBlank(),
          text = stringResource(Res.string.download)
        )
      }
    },
    dismissButton = {
      TextButtonCustom(
        text = stringResource(Res.string.cancel),
        onClick = onDismiss,
        enabled = !isDownloading,
      )
    },
  )
}

@Composable
private fun DownloadSuccessDialog(
  onPreview: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(
        painter = painterResource(Res.drawable.ic_dialog_success),
        contentDescription = null,
        tint = Colors.Green500,
      )
    },
    title = {
      Text(
        text = stringResource(Res.string.attendance_report_ready),
        style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp),
      )
    },
    text = {
      Text(
        text = stringResource(Res.string.attendance_report_download_success),
        style = TextStyleCustom.Medium.copy(color = Colors.Gray600),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
      )
    },
    confirmButton = {
      ButtonCustom(onClick = onPreview, text = stringResource(Res.string.preview_file))
    },
    dismissButton = {
      TextButtonCustom(onClick = onDismiss, text = stringResource(Res.string.close))
    },
  )
}
