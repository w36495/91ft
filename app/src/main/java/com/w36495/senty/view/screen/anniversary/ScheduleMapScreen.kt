package com.w36495.senty.view.screen.anniversary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.overlay.Marker
import com.w36495.senty.view.entity.SearchAddress
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.viewModel.MapSearchViewModel

@Composable
fun ScheduleMapScreen(
    vm: MapSearchViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onSelectLocation: (SearchAddress) -> Unit,
) {
    val resultAddress by vm.search.collectAsState()

    ScheduleMapContents(
        searchAddress = resultAddress,
        onBackPressed = { onBackPressed() },
        onClickSearch = { vm.getSearchResult(it) },
        onSelectLocation = { onSelectLocation(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalNaverMapApi::class)
@Composable
private fun ScheduleMapContents(
    searchAddress: List<SearchAddress>,
    onBackPressed: () -> Unit,
    onClickSearch: (String) -> Unit,
    onSelectLocation: (SearchAddress) -> Unit,
) {
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(maxZoom = 10.0, minZoom = 5.0)
        )
    }
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                isLocationButtonEnabled = false,
                isIndoorLevelPickerEnabled = true
            )
        )
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(37.565563, 126.977728), 10.0)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "장소 선택",
                        style = SentyTheme.typography.headlineSmall,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            NaverMap(
                uiSettings = mapUiSettings,
                properties = mapProperties,
                cameraPositionState = cameraPositionState
            ) {
                searchAddress.forEach { address ->
                    AddressMarker(
                        searchAddress = address,
                        onClickMarker = { marker ->
                            onSelectLocation(address)
                            true
                        }
                    )
                }
            }

            SearchSection(
                modifier = Modifier.fillMaxWidth(),
                resultAddress = searchAddress,
                onClickSearch = { keyword ->
                    onClickSearch(keyword)
                },
                onClickAddress = { address ->
                    val latLng = LatLng(address.y.toDouble(), address.x.toDouble())

                    cameraPositionState.move(
                        CameraUpdate.scrollTo(latLng)
                    )
                },
            )
        }
    }
}

@Composable
private fun SearchSection(
    modifier: Modifier = Modifier,
    resultAddress: List<SearchAddress>,
    onClickSearch: (String) -> Unit,
    onClickAddress: (SearchAddress) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        ScheduleMapSearch(
            modifier = Modifier.fillMaxWidth(),
            onClickSearch = { keyword ->
                onClickSearch(keyword)
            }
        )

        if (resultAddress.isNotEmpty()) {
            resultAddress.forEachIndexed { index, address ->
                AddressList(
                    modifier = Modifier.fillMaxWidth(),
                    searchAddress = address,
                    onClickAddress = { onClickAddress(it) }
                )

                if (index != resultAddress.lastIndex) {
                    HorizontalDivider(
                        color = SentyGray20,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
//                else {
//                    Box(modifier = Modifier.fillMaxWidth()
//                        .background(Color.White),
//                        contentAlignment = Alignment.CenterEnd) {
//                        Text(text = "접기", style = MaterialTheme.typography.labelLarge,
//                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
//                                .clickable { onResetSearchAddress() }
//                        )
//                    }
//                }
            }
        }
    }
}

@Composable
private fun ScheduleMapSearch(
    modifier: Modifier = Modifier,
    onClickSearch: (String) -> Unit,
) {
    var search by remember { mutableStateOf("") }

    Box(modifier = modifier) {
        TextField(
            value = search,
            onValueChange = {
                search = it
            },
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SentyGreen60, RoundedCornerShape(4.dp)),
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color(0xFFFBFBFB),
                focusedContainerColor = Color(0xFFFBFBFB),
                unfocusedContainerColor = Color(0xFFFBFBFB),
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { onClickSearch(search) }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                }
            },
            placeholder = {
                Text(
                    text = "주소를 입력해주세요.",
                    style = SentyTheme.typography.bodyMedium,
                )
            }
        )
    }
}

@Composable
private fun AddressList(
    modifier: Modifier = Modifier,
    searchAddress: SearchAddress,
    onClickAddress: (SearchAddress) -> Unit,
) {
    Box(modifier = modifier
        .clickable { onClickAddress(searchAddress) }
        .background(Color(0xFFFBFBFB), RoundedCornerShape(4.dp))
    ) {
        Text(
            text = searchAddress.address,
            style = SentyTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
        )
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun AddressMarker(
    searchAddress: SearchAddress,
    onClickMarker: (Marker) -> Boolean,
) {
    Marker(
        state = MarkerState(
            position = LatLng(searchAddress.y.toDouble(), searchAddress.x.toDouble())
        ),
        onClick = { onClickMarker(it) }
    )
}

@Preview
@Composable
private fun AddressListPreview() {
    SentyTheme {
        SearchSection(
            resultAddress = listOf(
                SearchAddress(address = "123", x = "123", y = "123"),
                SearchAddress(address = "123", x = "123", y = "123"),
                SearchAddress(address = "123", x = "123", y = "123"),
                SearchAddress(address = "123", x = "123", y = "123"),
            ),
            onClickSearch = {},
            onClickAddress = {},
//            onResetSearchAddress = {}
        )
    }
}