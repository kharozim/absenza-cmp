package id.neo.hr.presentation.location

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.data.domain.model.CoordinateModel
import id.neo.hr.data.domain.model.OsmLocationDomain
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.ButtonCustom
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_btn_location
import neohr_mp.shared.generated.resources.ic_cross
import neohr_mp.shared.generated.resources.ic_search
import neohr_mp.shared.generated.resources.map_pin
import neohr_mp.shared.generated.resources.search_location
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

private const val DEFAULT_LATITUDE = -6.200603468816713
private const val DEFAULT_LONGITUDE = 106.79873778292526
private const val OPEN_FREE_MAP_STYLE = "https://tiles.openfreemap.org/styles/liberty"

@Composable
fun SearchMapScreen(
  initialCoordinate: CoordinateModel? = null,
  onBack: () -> Unit,
  onLocationSelected: (CoordinateModel) -> Unit,
  onCurrentLocationClick: () -> Unit = {},
  viewModel: SearchMapViewModel = koinViewModel(),
) {
  val state = viewModel.state
  Content(
    initialCoordinate = initialCoordinate,
    onBack = onBack,
    onLocationSelected = onLocationSelected,
    onCurrentLocationClick = onCurrentLocationClick,
    search = state.search,
    results = state.searchResults,
    selectedAddress = state.locationString,
    isLoading = state.searchLoading || state.reverseLoading,
    onSearchChange = viewModel::updateSearch,
    onSearch = viewModel::searchAddress,
    onClearSearch = { viewModel.updateSearch("") },
    onAddressSelected = viewModel::selectAddress,
    onReverseGeocode = viewModel::reverseGeocode,
    isDebug = false,
  )
}

@OptIn(FlowPreview::class)
@Composable
private fun Content(
  initialCoordinate: CoordinateModel? = null,
  onBack: () -> Unit,
  onLocationSelected: (CoordinateModel) -> Unit,
  onCurrentLocationClick: () -> Unit = {},
  search: String,
  results: List<OsmLocationDomain>,
  selectedAddress: String?,
  isLoading: Boolean,
  onSearchChange: (String) -> Unit,
  onSearch: () -> Unit,
  onClearSearch: () -> Unit,
  onAddressSelected: (OsmLocationDomain) -> Unit,
  onReverseGeocode: (Double, Double) -> Unit,
  isDebug: Boolean = false,
) {
  val focusManager = LocalFocusManager.current
  val initial = initialCoordinate ?: CoordinateModel(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
  var selected by remember { mutableStateOf(initial) }
  val scope = rememberCoroutineScope()
  val mapState = rememberMapState(
    baseStyle = BaseStyle.Uri(OPEN_FREE_MAP_STYLE),
    initialCameraPosition = CameraPosition(
      target = Position(initial.lat, initial.lon),
      zoom = 18.0
    ),
  )
  LaunchedEffect(search) {
    if (search.isNotBlank()) {
      delay(700)
      onSearch()
    }
  }
  LaunchedEffect(mapState) {
    snapshotFlow { mapState.cameraPosition.target }
      .map { CoordinateModel(it.latitude, it.longitude) }
      .distinctUntilChanged()
      .debounce(1_000)
      .collect { coordinate ->
        selected = coordinate
        onReverseGeocode(coordinate.lat, coordinate.lon)
      }
  }
  Scaffold(
    topBar = {
      Column(
        modifier = Modifier
//          .align(Alignment.TopCenter)
//          .padding(paddingValues)
          .statusBarsPadding()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(color = Colors.Gray10, shape = RoundedCornerShape(10.dp))
            .border(width = 1.dp, color = Colors.Gray50, shape = RoundedCornerShape(10.dp))
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = null,
            tint = Colors.Purple800,
            modifier = Modifier.size(16.dp)
          )

          Spacer(modifier = Modifier.width(12.dp))

          BasicTextField(
            singleLine = true,
            value = search,
            onValueChange = onSearchChange,
            textStyle = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray800),
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
              focusManager.clearFocus()
              onSearch()
            }),
            decorationBox = { innerTextField ->
              if (search.isEmpty()) {
                Text(
                  stringResource(Res.string.search_location),
                  style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray400)
                )
              }
              innerTextField()
            }
          )

          if (search.isNotEmpty()) {
            Icon(
              painter = painterResource(Res.drawable.ic_cross),
              contentDescription = null,
              tint = Colors.Purple300,
              modifier = Modifier
                .clickable {
                  onClearSearch()
                }
                .padding(start = 12.dp)
                .size(16.dp)
            )
          }
        }
        if (isLoading) {
          LinearProgressIndicator(
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .fillMaxWidth()
              .height(2.dp)
              .offset(y = (-3).dp)
          )
        }
        if (results.isNotEmpty()) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
          ) {
            LazyColumn(
              modifier = Modifier.heightIn(max = 250.dp)
            ) {
              items(results) { address ->
                val displayName =
                  address.displayName.ifEmpty { "Unknown Location" }

                Text(
                  text = displayName,
                  style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray800),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      focusManager.clearFocus()
                      onAddressSelected(address)
                    }
                    .padding(16.dp)
                )
                HorizontalDivider(color = Colors.Gray50)
              }
            }
          }
        }
      }

    },
    bottomBar = {
      Column(
        modifier = Modifier.fillMaxWidth()
          .navigationBarsPadding()
          .background(Color.White)
          .padding(16.dp)
      ) {
        Text(
          text = selectedAddress ?: "Alamat belum dipilih",
          color = Colors.Gray800,
          modifier = Modifier.padding(bottom = 12.dp),
        )
        ButtonCustom(
          text = "Simpan Cabang",
          modifier = Modifier.fillMaxWidth(),
          onClick = { onLocationSelected(selected) },
        )
      }
    },
    floatingActionButton = {
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .background(Color.White)
          .clickable {
            onCurrentLocationClick()
          }
          .padding(8.dp)
      ) {
        Icon(painterResource(Res.drawable.ic_btn_location), null)
      }
    }
  ) { paddingValues ->
    Box(Modifier.fillMaxSize().padding(paddingValues)) {
      if (isDebug) {
        Box(
          modifier = Modifier.fillMaxSize().background(Colors.Gray100),
          contentAlignment = Alignment.Center,
        ) {
          Text("Map preview", color = Colors.Gray500)
        }
      } else {
        MaplibreMap(
          state = mapState,
          interactions = MapInteractions {
            callbacks {
              click {
                onEvent { event ->
                  event.position?.let {
                    selected = CoordinateModel(it.latitude, it.longitude)
                    onReverseGeocode(it.latitude, it.longitude)
                  }
                  ClickResult.Consume
                }
              }
            }
          },
        )
      }

      Image(
        painter = painterResource(Res.drawable.map_pin),
        contentDescription = "Lokasi terpilih",
        modifier = Modifier
          .align(Alignment.Center)
          .size(42.dp),
      )
    }
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Prev() {
  AppTheme {
    Content(
      initialCoordinate = CoordinateModel(DEFAULT_LATITUDE, DEFAULT_LONGITUDE),
      onBack = {},
      onLocationSelected = {},
      onCurrentLocationClick = {},
      search = "Jl. Pahlawan, Jakarta",
      results = emptyList(),
      selectedAddress = "Jl. Pahlawan No. 11, Jakarta",
      isLoading = false,
      onSearchChange = {},
      onSearch = {},
      onClearSearch = {},
      onAddressSelected = {},
      onReverseGeocode = { _, _ -> },
      isDebug = true,
    )
  }
}
