package id.neo.hr.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import id.neo.hr.presentation.theme.Colors

/**
 * Created by Kharozim
 * 31/08/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
fun String?.filterMessageError(): String? {
    return when {
        (this ?: "").contains("failed to connect", true) -> "Failed connect to server"
        else -> this
    }
}
fun String.mandatory(): AnnotatedString {
    return buildAnnotatedString {
        append(this@mandatory)
        withStyle(
            style = SpanStyle(
                color = Colors.Red600
            )
        ) {
            append("*")
        }
    }
}