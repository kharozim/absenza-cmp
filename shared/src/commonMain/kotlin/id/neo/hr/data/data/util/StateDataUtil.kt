package id.neo.hr.data.data.util

sealed class StateDataUtil<out T : Any> {
    data class Success<out T : Any>(val data: T?) : StateDataUtil<T>()
    data class Error(
        val errorModel: ErrorModel?,
    ) : StateDataUtil<Nothing>()
}