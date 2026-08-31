package id.neo.hr.util

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