package id.neo.hr.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.LoadingDialog
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.activity
import neohr_mp.shared.generated.resources.employee_is_not_found
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by Kharozim
 * 26/05/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@Composable
fun TaskAdminScreen(
  innerPadding: PaddingValues = PaddingValues(0.dp),
  viewModel: TaskAdminViewModel = koinViewModel(),
  navToDetail: (employee: EmployeeModel) -> Unit,
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.getData()
  }

  TaskAdminContent(
    innerPadding = innerPadding,
    searchQuery = state.searchQuery,
    employees = state.listEmployeeFiltered,
    employeeListState = state.getDataState,
    onSearchChange = viewModel::updateSearch,
    onItemClick = navToDetail,
  )

  if (state.getDataState is UiState.Loading) {
    LoadingDialog(
      (state.getDataState as UiState.Loading).message,
      onDismissRequest = {},
    )
  }
}

@Composable
private fun TaskAdminContent(
  innerPadding: PaddingValues = PaddingValues(0.dp),
  searchQuery: String,
  employees: List<EmployeeModel>,
  employeeListState: UiState?,
  onSearchChange: (String) -> Unit,
  onItemClick: (EmployeeModel) -> Unit,
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF4F6FA))
      .padding(innerPadding),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)
  ) {
    item {
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = stringResource(Res.string.activity),
          style = TextStyleCustom.Bold.copy(fontSize = 20.sp, color = Colors.Gray800)
        )
      }
    }

    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = {
          Text(
            text = stringResource(Res.string.search),
            style = TextStyleCustom.Medium.copy(color = Colors.Gray400, fontSize = 14.sp)
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(Res.string.search),
            tint = Colors.Gray400,
            modifier = Modifier.size(20.dp)
          )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Colors.White,
          unfocusedContainerColor = Colors.White,
          disabledContainerColor = Colors.White,
          focusedBorderColor = Color.Transparent,
          unfocusedBorderColor = Color.Transparent
        )
      )
    }

    when (employeeListState) {
      null,
      is UiState.Loading,
        -> Unit

      is UiState.Error -> {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 48.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = employeeListState.message,
              style = TextStyleCustom.SemiBold.copy(
                color = Colors.Gray400,
              )
            )
          }
        }
      }

      else -> {
        if (employees.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = stringResource(Res.string.employee_is_not_found),
                style = TextStyleCustom.SemiBold.copy(
                  color = Colors.Gray400,
                )
              )
            }
          }
        } else {
          items(items = employees, key = { it.id }) { employee ->
            EmployeeTaskItem(
              employee = employee,
              onItemClick = { onItemClick(employee) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun EmployeeTaskItem(
  employee: EmployeeModel,
  onItemClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(Colors.White)
      .clickable { onItemClick() }
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AsyncImage(
      model = employee.accountUrlPhoto,
      contentDescription = "Avatar ${employee.accountName}",
      contentScale = ContentScale.Crop,
      placeholder = painterResource(Res.drawable.ic_person_placeholder),
      modifier = Modifier
        .size(36.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Colors.Gray50),
      error = painterResource(Res.drawable.ic_person_placeholder)
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(
      modifier = Modifier.weight(1f)
    ) {
      Text(
        text = employee.accountName,
        style = TextStyleCustom.Bold.copy(fontSize = 15.sp, color = Colors.Gray800),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          "${employee.accountRole}-${employee.accountPosition}",
          style = TextStyleCustom.Regular.copy(
            color = Colors.Gray400,
          ),
        )
        Text(
          employee.employeeCode,
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
      imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
      contentDescription = "Go to detail",
      tint = Colors.Purple800,
      modifier = Modifier.size(24.dp)
    )
  }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun TaskAdminScreenPreview() {
  TaskAdminContent(
    searchQuery = "",
    employees = List(2) {
      EmployeeModel(
        id = it,
        accountUid = "uid-1",
        accountName = "Julian Robert Auliman Yesaya",
        accountEmail = "julian-$it@example.com",
        accountPhoneNumber = "081234567890",
        accountUrlPhoto = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
        accountRole = "Staff",
        accountPosition = "IT-$it",
        isActive = true,
        isFreeAccount = false,
        phoneNumberVerification = true,
        leaveQuota = 12,
        emailVerification = true,
        isStaff = false,
        employeeCode = "EMP-123123"
      )
    },
    employeeListState = UiState.Success,
    onSearchChange = {},
    onItemClick = {},
  )
}
