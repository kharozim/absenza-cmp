package id.neo.hr.presentation.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.ic_error
import neohr_mp.shared.generated.resources.img_warning
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/*
 * Created by Kharozim
 * 03/09/25 - kharozim.wrk@gmail.com
 * Copyright (c) 2025. NeoHR
 * All Rights Reserved
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialogCustom(
  modifier: Modifier = Modifier,
  icon: DrawableResource = Res.drawable.ic_dialog_success,
  title: String? = null,
  description: @Composable (() -> Unit)? = null,
  additionalInput: @Composable (() -> Unit)? = null,
  warning: String? = null,
  onDismissRequest: () -> Unit = {},
  cancelable: Boolean = true,
  actions: @Composable (RowScope.(SheetState) -> Unit)? = null,
) {

  val sheetState = rememberModalBottomSheetState(
    skipPartiallyExpanded = true,
    confirmValueChange = {
      if (!cancelable) {
        return@rememberModalBottomSheetState it != SheetValue.Hidden
      }
      return@rememberModalBottomSheetState true
    }
  )

  ModalBottomSheet(
    containerColor = Colors.White,
    modifier = modifier,
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    dragHandle =
      if (cancelable) {
        { BottomSheetDefaults.DragHandle() }
      } else null,
    properties = ModalBottomSheetProperties(
      shouldDismissOnBackPress = cancelable
    )
  ) {
    BottomSheetContent(
      icon = icon,
      title = title,
      description = description,
      additionalInput = additionalInput,
      warning = warning,
      actions = {
        actions?.invoke(this, sheetState)
      },
    )
  }
}

@Composable
private fun BottomSheetContent(
  icon: DrawableResource = Res.drawable.ic_dialog_success,
  title: String? = null,
  description: @Composable (() -> Unit)? = null,
  additionalInput: @Composable (() -> Unit)?,
  warning: String? = null,
  actions: @Composable (RowScope.() -> Unit)? = null,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 16.dp, top = 32.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(painterResource(icon), null)
    title?.let {
      Text(
        it, style = TextStyleCustom.ExtraBold.copy(
          fontSize = 20.sp,
          color = Colors.Gray800
        ),
        modifier = Modifier.padding(top = 24.dp)
      )
    }
    description?.let {
      Spacer(Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .heightIn(max = 250.dp)
          .verticalScroll(rememberScrollState())
      ) {
        it.invoke()
      }
    }
    additionalInput?.let {
      Spacer(Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        it.invoke()
      }
    }
    warning?.let {
      WarningLabel(text = it, modifier = Modifier.padding(top = 24.dp))
    }

    actions?.let { action ->
      Row(
        modifier = Modifier
          .padding(top = 24.dp)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
          space = 16.dp,
          alignment = Alignment.CenterHorizontally
        )
      ) {
        action()
      }
    }

  }
}

@Composable
private fun WarningLabel(text: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .border(
        border = BorderStroke(1.dp, Colors.Warning100),
        shape = RoundedCornerShape(10.dp)
      )
      .background(
        color = Colors.Warning50,
        shape = RoundedCornerShape(10.dp)
      )
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically

  ) {
    Icon(
      painter = painterResource(Res.drawable.ic_error),
      contentDescription = "icon warning",
      tint = Colors.OrangePrimary
    )

    Text(
      text = text,
      style = TextStyleCustom.Medium.copy(
        color = Colors.OrangePrimary,
      ),
      modifier = Modifier
        .padding(start = 8.dp)
        .fillMaxWidth()
        .heightIn(max = 100.dp)
        .verticalScroll(rememberScrollState()),
    )
  }
}

@Preview
@Composable
private fun BottomSheetPreview() {
  AppTheme {
    BottomSheetContent(
      icon = Res.drawable.img_warning,
      title = "Title",
      description = {
        Text(
          buildAnnotatedString {
            append("Are you sure want to delete an employee with the name ")
            withStyle(
              style = SpanStyleCustom.ExtraBold.copy(
                fontSize = 14.sp,
                color = Colors.OrangePrimary
              )
            ) {
              append("\"Imron Nanda Marpaung\"")
            }
          },
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 14.sp,
            color = Colors.Gray500
          ),
          textAlign = TextAlign.Center,
        )
      },
      additionalInput = {
        InputField(
        value = "ini adalah alamat yang sangat",
        label = {
          Text("alamat Panjang")
        },
        placeholder = "PlaceHolder",
        onValueChange = {},
        modifier = Modifier
          .height(120.dp)
          .fillMaxWidth(),
        errorText = "",
        singleLine = false,
        prefix = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon"
          )
        },
      )},
      warning = "Warning is label long here",
      actions = {
        TextButtonCustom(
          onClick = {

          },
          text = "Button 1",
          modifier = Modifier.weight(1f)
        )
        ButtonCustom(
          onClick = {
          },
          text = "Button 2"
        )
      },
    )
  }
}
