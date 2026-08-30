package id.neo.hr

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Created by Kharozim
 * 28/08/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
@Composable
fun HomeScreen(onProfileClick: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = {
            Column(Modifier.padding(it)) {
                Button(onClick = onProfileClick) {
                    Text("Buka Profile")
                }
            }

        },
    )
}
