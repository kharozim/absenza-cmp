package id.neo.hr.presentation.location

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import id.neo.hr.data.data.remote.response.OsmLocationResponse
import id.neo.hr.data.data.remote.response.toDomain
import id.neo.hr.data.domain.model.CoordinateModel
import id.neo.hr.data.domain.model.OsmLocationDomain
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.ButtonCustom
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_btn_location
import neohr_mp.shared.generated.resources.ic_cross
import neohr_mp.shared.generated.resources.ic_search
import neohr_mp.shared.generated.resources.map_pin
import neohr_mp.shared.generated.resources.search_location
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraAnimation
import org.maplibre.compose.camera.CameraMoveReason
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.map.MapState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

private const val DEFAULT_LATITUDE = -6.200603468816713
private const val DEFAULT_LONGITUDE = 106.79873778292526
private const val OPEN_FREE_MAP_STYLE = "https://tiles.openfreemap.org/styles/bright"

@OptIn(FlowPreview::class)
@Composable
fun SearchMapScreen(
  initialCoordinate: CoordinateModel? = null,
  onBack: () -> Unit,
  onSave: (CoordinateModel) -> Unit,
  onCurrentLocationClick: () -> Unit = {},
  viewModel: SearchMapViewModel = koinViewModel(),
) {
  val state = viewModel.state
  val initialCoordinate = initialCoordinate ?: CoordinateModel(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
  val mapState = rememberMapState(
    baseStyle = BaseStyle.Uri(OPEN_FREE_MAP_STYLE),
    initialCameraPosition = CameraPosition(
      target = Position(latitude = initialCoordinate.lat, longitude = initialCoordinate.lon),
      zoom = 18.0
    ),
  )

  LaunchedEffect(mapState) {
    snapshotFlow { mapState.cameraPosition.target }
      .drop(1)
      .map { CoordinateModel(it.latitude, it.longitude) }
      .distinctUntilChanged()
      .debounce(1_000)
      .collect { coordinate ->
        if (mapState.cameraMoveReason == CameraMoveReason.GESTURE) {
          viewModel.reverseGeocode(latitude = coordinate.lat, longitude = coordinate.lon)
        }
      }
  }

  LaunchedEffect(state.pendingCameraMove) {
    state.pendingCameraMove?.let { coordinate ->
      mapState.animateCameraPosition(
        position = mapState.cameraPosition.copy(
          target = Position(latitude = coordinate.lat, longitude = coordinate.lon)
        ),
        animation = CameraAnimation.Fly(duration = 500.milliseconds)
      )
      viewModel.consumeCameraMove()
    }
  }

  Content(
    onBack = onBack,
    onSaveClick = {
      state.selectedMap?.let { item ->
        onSave(
          CoordinateModel(
            item.lat,
            item.lon
          )
        )
      }
    },
    onCurrentLocationClick = onCurrentLocationClick,
    search = state.search,
    results = state.searchResults,
    selectedMap = state.selectedMap,
    searchLoading = state.searchLoading,
    onSearchChange = viewModel::updateSearch,
    onSearch = viewModel::searchAddress,
    onClearSearch = { viewModel.updateSearch("") },
    onAddressSelected = viewModel::selectAddress,
    onReverseGeocode = viewModel::reverseGeocode,
    isDebug = false,
    mapState = mapState,
    reverseLoading = state.reverseLoading
  )
}

@Composable
private fun Content(
  onBack: () -> Unit,
  onSaveClick: () -> Unit,
  onCurrentLocationClick: () -> Unit = {},
  search: String,
  results: List<OsmLocationDomain>,
  selectedMap: OsmLocationDomain?,
  searchLoading: Boolean,
  reverseLoading: Boolean,
  onSearchChange: (String) -> Unit,
  onSearch: () -> Unit,
  onClearSearch: () -> Unit,
  onAddressSelected: (OsmLocationDomain) -> Unit,
  onReverseGeocode: (Double, Double) -> Unit,
  isDebug: Boolean = false,
  mapState: MapState?,
) {
  val focusManager = LocalFocusManager.current

  Scaffold(
    content = { paddingValues ->
      Box(Modifier.fillMaxSize()) {
        if (isDebug) {
          Box(
            modifier = Modifier.fillMaxSize().background(Colors.Gray100),
            contentAlignment = Alignment.Center,
          ) {
            Text("Map preview", color = Colors.Gray500)
          }
        } else {
          if (mapState != null) {
            MaplibreMap(
              state = mapState,
              interactions = MapInteractions {
                callbacks {
//                  click {
//                    onEvent { event ->
//                      event.position?.let {
//                        onReverseGeocode(it.latitude, it.longitude)
//                      }
//                      ClickResult.Consume
//                    }
//                  }
                }
              },
            )
          }
        }

        Column(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(paddingValues)
//            .statusBarsPadding()
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
          if (searchLoading) {
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

        Box(
          modifier = Modifier
            .align(Alignment.Center)
            .offset(y = (-21).dp)
        ) {
          Icon(
            painter = painterResource(Res.drawable.map_pin),
            contentDescription = "Map Center Pin",
            tint = Color.Unspecified
          )
          if (reverseLoading)
            CircularProgressIndicator(
              modifier = Modifier
                .size(18.dp)
                .align(Alignment.TopCenter)
                .padding(top = 1.dp),
              strokeWidth = 3.dp,
              color = Colors.Gray50
            )
        }
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
          .background(Color.White)
          .navigationBarsPadding()
          .padding(16.dp)
      ) {
        Text(
          text = selectedMap?.displayName ?: "Alamat belum dipilih",
          color = Colors.Gray800,
          style = TextStyleCustom.Medium
        )
        Text(
          text = "${selectedMap?.lat.toString()},${selectedMap?.lon}",
          color = Colors.Gray300,
          style = TextStyleCustom.Medium,
          fontSize = 11.sp,
          modifier = Modifier.padding(bottom = 12.dp),
        )
        ButtonCustom(
          text = "Simpan Cabang",
          modifier = Modifier.fillMaxWidth(),
          onClick = { onSaveClick() },
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
    },
  )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Prev() {
  val state = SearchMapState(
    search = "PT kumon",
    locationString = "Jl. Pahlawan no. 11",
    searchResults = List(5) {
      OsmLocationResponse(displayName = "Jl. Kebangsaan No.$it").toDomain()
    },
  )

  AppTheme {
    Content(
      onBack = {},
      onSaveClick = {},
      onCurrentLocationClick = {},
      search = "Jl. Pahlawan, Jakarta",
      results = state.searchResults,
      selectedMap = OsmLocationDomain(displayName = "Jl. Pahlawan No. 11, Jakarta", 0.0, 0.0),
      searchLoading = false,
      onSearchChange = {},
      onSearch = {},
      onClearSearch = {},
      onAddressSelected = {},
      onReverseGeocode = { _, _ -> },
      isDebug = true,
      mapState = null,
      reverseLoading = false
    )
  }
}
