package id.neo.hr.util

import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import id.neo.hr.presentation.theme.Colors
import kotlin.time.Clock

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

fun String?.dashIfEmpty(): String = if (this.isNullOrEmpty()) "-" else this

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

fun Modifier.debounceClickable(
    debounceInterval: Long = 500L,
    onClick: () -> Unit,
): Modifier = composed {
    var lastClickTime by remember { mutableStateOf(0L) }
    this.clickable {
        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastClickTime >= debounceInterval) {
            lastClickTime = now
            onClick()
        }
    }
}

fun String.formatPhoneNumberWa(): String {
    val cleanedNumber = this.replace(Regex("\\D"), "")
    if (cleanedNumber.startsWith("62")) {
        return "+$cleanedNumber"
    }
    if (cleanedNumber.startsWith("08")) {
        return "+62${cleanedNumber.substring(1)}"
    }
    return cleanedNumber
}

fun String.formatPhoneNumber(): String {
    val cleanedNumber = this.replace(Regex("\\D"), "")
    if (cleanedNumber.startsWith("+62")) {
        return "0${cleanedNumber.substring(3)}"
    }
    if (cleanedNumber.startsWith("62")) {
        return "0${cleanedNumber.substring(2)}"
    }
    return cleanedNumber
}

fun String.toTitleCase(): String {
    if (this.isEmpty()) {
        return this
    }
    return this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}