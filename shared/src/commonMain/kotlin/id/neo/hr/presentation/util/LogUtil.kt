package id.neo.hr.presentation.util

import io.github.aakira.napier.Napier

/** Level log yang tersedia pada seluruh target Kotlin Multiplatform. */
enum class LogLevel(val priority: Int) {
    Debug(0),
    Info(1),
    Warning(2),
    Error(3),
}

/**
 * Logger ringan untuk commonMain.
 *
 * Logger nonaktif secara default agar production tidak mencetak log. Aktifkan dari entry point
 * platform dengan `LogUtil.configure(enabled = isDebugBuild)`.
 */
object LogUtil {
    private var enabled = false
    private var minimumLevel = LogLevel.Debug

    fun configure(
        enabled: Boolean,
        minimumLevel: LogLevel = LogLevel.Debug,
    ) {
        this.enabled = enabled
        this.minimumLevel = minimumLevel
    }

    fun d(message: String, tag: String = DEFAULT_TAG) =
        log(LogLevel.Debug, tag, message)

    fun i(message: String, tag: String = DEFAULT_TAG) =
        log(LogLevel.Info, tag, message)

    fun w(message: String, tag: String = DEFAULT_TAG) =
        log(LogLevel.Warning, tag, message)

    fun e(
        message: String,
        throwable: Throwable? = null,
        tag: String = DEFAULT_TAG,
    ) = log(LogLevel.Error, tag, message, throwable)

    fun d(tag: String = DEFAULT_TAG, message: () -> String) =
        log(LogLevel.Debug, tag, message)

    fun i(tag: String = DEFAULT_TAG, message: () -> String) =
        log(LogLevel.Info, tag, message)

    fun w(tag: String = DEFAULT_TAG, message: () -> String) =
        log(LogLevel.Warning, tag, message)

    fun e(
        tag: String = DEFAULT_TAG,
        throwable: Throwable? = null,
        message: () -> String,
    ) = log(LogLevel.Error, tag, message, throwable)

    private fun log(
        level: LogLevel,
        tag: String,
        message: () -> String,
        throwable: Throwable? = null,
    ) {
        if (!shouldLog(level)) return
        log(level, tag, message(), throwable)
    }

    private fun log(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
        if (!shouldLog(level)) return
        when (level) {
            LogLevel.Debug -> Napier.d(message, tag = tag)
            LogLevel.Info -> Napier.i(message, tag = tag)
            LogLevel.Warning -> Napier.w(message, tag = tag)
            LogLevel.Error -> Napier.e(message, throwable = throwable, tag = tag)
        }
    }

    private fun shouldLog(level: LogLevel): Boolean =
        enabled && level.priority >= minimumLevel.priority

    private const val DEFAULT_TAG = "NeoHR"
}
