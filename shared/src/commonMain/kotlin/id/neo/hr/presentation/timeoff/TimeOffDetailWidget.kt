package id.neo.hr.presentation.timeoff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.TimeOffAccountModel
import id.neo.hr.data.domain.model.TimeOffDetailModel
import id.neo.hr.data.domain.model.TimeOffDocModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.util.dashIfEmpty
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.days
import neohr_mp.shared.generated.resources.description
import neohr_mp.shared.generated.resources.employee_name
import neohr_mp.shared.generated.resources.empty
import neohr_mp.shared.generated.resources.end_date
import neohr_mp.shared.generated.resources.ic_download
import neohr_mp.shared.generated.resources.ic_pdf
import neohr_mp.shared.generated.resources.notes
import neohr_mp.shared.generated.resources.start_date
import neohr_mp.shared.generated.resources.status
import neohr_mp.shared.generated.resources.supporting_document
import neohr_mp.shared.generated.resources.time_off_type
import neohr_mp.shared.generated.resources.total_leaves
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TimeOffDetailWidget(
  modifier: Modifier = Modifier,
  data: TimeOffDetailModel,
) {
  val uriHandler = LocalUriHandler.current

  LazyColumn(
    modifier = modifier
      .padding(horizontal = 16.dp, vertical = 6.dp),
  ) {
    item {
      ItemProfile(title = stringResource(Res.string.employee_name), value = data.account.name)
      ItemProfile(title = stringResource(Res.string.time_off_type), value = data.typeLabel)
      ItemProfile(
        title = stringResource(Res.string.start_date),
        value = FormatterUtil.stringDateToNewFormat(data.start).orEmpty()
      )
      ItemProfile(
        title = stringResource(Res.string.end_date),
        value = FormatterUtil.stringDateToNewFormat(data.end).orEmpty()
      )
      ItemProfile(
        title = stringResource(Res.string.total_leaves),
        value = stringResource(Res.string.days, data.totalDay),
      )
      ItemProfile(title = stringResource(Res.string.description), value = data.description)
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        stringResource(Res.string.supporting_document),
        style = TextStyleCustom.SemiBold.copy(color = Colors.Gray400, fontSize = 12.sp),
      )

      if (data.docs.isEmpty()) {
        Text(
          stringResource(Res.string.empty),
          modifier = Modifier.padding(vertical = 8.dp),
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp),
        )
      }
    }

    if (data.docs.isNotEmpty()) {
      items(items = data.docs) { doc ->
        FileDownloaderItem(
          fileUrl = doc.url,
          fileName = doc.url.substringAfterLast('/').ifBlank { "Document" },
          fileSize = doc.fileSize,
          fileType = doc.type,
          onDownloadClick = {
            if (doc.url.isNotBlank()) {
              uriHandler.openUri(doc.url)
            }
          },
        )
      }
    }

    item {
      ItemStatus(status = data.status, statusLabel = data.statusLabel)
    }

    if(data.note.isNotEmpty()) {
      item {
        ItemProfile(title = stringResource(Res.string.notes), value = data.note)
      }
    }
  }
}

@Composable
private fun ItemProfile(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
) {
  Column(modifier = modifier) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = title,
      style = TextStyleCustom.SemiBold.copy(color = Colors.Gray400, fontSize = 12.sp),
    )
    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = value.dashIfEmpty(),
      modifier = Modifier.padding(vertical = 4.dp),
      style = TextStyleCustom.Bold.copy(
        fontSize = 14.sp,
        color = Colors.Gray800,
      ),
    )
  }
}

@Composable
private fun ItemStatus(
  status: Int,
  statusLabel: String,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = stringResource(Res.string.status),
      style = TextStyleCustom.SemiBold.copy(color = Colors.Gray400, fontSize = 12.sp),
    )
    Spacer(modifier = Modifier.height(4.dp))
    val statusColor =
      when (status) {
        1 -> Color(0xFF12B76A)
        -1 -> Color(0xFFF04438)
        0 -> Color(0xFFF79009)
        else -> Colors.Gray400
      }

    Text(
      text = statusLabel,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(statusColor.copy(alpha = 0.1f))
        .border(1.dp, statusColor, RoundedCornerShape(12.dp))
        .padding(horizontal = 10.dp, vertical = 4.dp),
      style = TextStyleCustom.Bold.copy(
        fontSize = 14.sp,
        color = statusColor,
      ),
    )
  }
}

@Composable
private fun FileDownloaderItem(
  fileUrl: String,
  fileName: String,
  fileSize: String,
  fileType: String,
  onDownloadClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.Start,
  ) {
    Row(
      modifier = modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (fileType.lowercase() == "pdf") {
        Icon(
          painter = painterResource(Res.drawable.ic_pdf),
          contentDescription = null,
          tint = Color.Unspecified,
          modifier = Modifier.height(34.dp),
        )
      } else {
        AsyncImage(
          fileUrl,
          contentDescription = null,
          modifier = Modifier.height(34.dp),
        )
      }

      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = fileName,
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800),
        )
        Text(
          text = fileSize,
          style = TextStyleCustom.Medium.copy(color = Colors.Gray400, fontSize = 12.sp),
        )
      }
      IconButton(onClick = onDownloadClick) {
        Icon(
          painter = painterResource(Res.drawable.ic_download),
          contentDescription = null,
          tint = Colors.Gray700,
          modifier = Modifier.height(24.dp),
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimeOffDetail() {
  val mockData = TimeOffDetailModel(
    id = 17,
    account = TimeOffAccountModel(
      id = 1,
      name = "John Doe",
      email = "john@example.com",
    ),
    approvalAt = "",
    approvalBy = 0,
    createdAt = "2026-05-20T10:37:01+07:00",
    description = "Mengambil cuti tahunan selama 3 hari untuk keperluan keluarga.",
    docs = listOf(
      TimeOffDocModel(
        id = 1,
        timeOffId = 17,
        type = "pdf",
        url = "https://link.com/sample-file.pdf",
        fileSize = "500 KB"
      ),
    ),
    end = "2026-07-24T00:00:00+07:00",
    deletedAt = "",
    note = "terpaksa saya tolak",
    start = "2026-07-22T00:00:00+07:00",
    status = 0,
    statusLabel = "Waiting",
    totalDay = 3,
    type = "ANNUAL",
    typeLabel = "Annual Leave",
    updatedAt = "2026-05-20T10:37:01+07:00",
  )

  Column(modifier = Modifier.background(Color.White)) {
    TimeOffDetailWidget(
      modifier = Modifier,
      data = mockData,
    )
  }
}
