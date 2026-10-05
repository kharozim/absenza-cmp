package id.neo.hr.presentation.setting.legal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.widget.ButtonCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.invalid_document_url
import neohr_mp.shared.generated.resources.open_document
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentScreen(
  title: String,
  url: String,
  emptyMessage: String,
  navBack: () -> Unit,
) {
  val uriHandler = LocalUriHandler.current
  val invalidUrlMessage = stringResource(Res.string.invalid_document_url)
  val validUrl = url.takeIf { it.startsWith("https://") || it.startsWith("http://") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold,
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(painterResource(Res.drawable.ic_arrow_left), contentDescription = null)
          }
        },
        actions = { Spacer(Modifier.width(48.dp)) },
      )
    },
  ) { padding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      if (validUrl == null) {
        Text(emptyMessage, style = TextStyleCustom.Medium.copy(color = Colors.Gray800))
      } else {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Text(title, style = TextStyleCustom.Bold.copy(color = Colors.Gray800))
          ButtonCustom(
            text = stringResource(Res.string.open_document),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
              runCatching { uriHandler.openUri(validUrl) }
                .onFailure { ToastManager.error(invalidUrlMessage) }
            },
          )
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun LegalDocumentScreenPreview() {
  AppTheme {
    LegalDocumentScreen(
      title = "Terms & Conditions",
      url = "https://example.com/terms",
      emptyMessage = "Document is not available",
      navBack = {},
    )
  }
}
