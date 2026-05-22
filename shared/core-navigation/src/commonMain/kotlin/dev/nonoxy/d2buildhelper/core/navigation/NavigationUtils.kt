package dev.nonoxy.d2buildhelper.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import androidx.savedstate.read

const val DEFAULT_RESULT_KEY = "RESULT"

val NavController.currentRoute: String?
    get() = currentDestination?.route

fun NavController.popBackStackOnResumed() {
    runOnResumed(::popBackStack)
}

fun NavController.runOnResumed(block: () -> Unit) {
    if (currentBackStackEntry?.lifecycle?.currentState != Lifecycle.State.RESUMED) return

    block()
}

fun <T> NavController.popBackStackOnResumed(
    key: String = DEFAULT_RESULT_KEY,
    value: T? = null
) {
    runOnResumed {
        popBackStack(
            key = key,
            value = value,
        )
    }
}

fun <T> NavController.popBackStack(
    key: String = DEFAULT_RESULT_KEY,
    value: T? = null
) {
    if (value != null) {
        putNavigationResultToPreviousBackStackEntry(key, value)
    }

    popBackStack()
}

fun <T> NavController.putNavigationResultToPreviousBackStackEntry(
    key: String,
    value: T,
) {
    previousBackStackEntry
        ?.savedStateHandle
        ?.set(key, value)
}

fun <T> NavController.putNavigationResultToCurrentBackStackEntry(
    key: String,
    value: T,
) {
    currentBackStackEntry
        ?.savedStateHandle
        ?.set(key, value)
}

@Composable
fun <T> NavBackStackEntry.CheckNavigationResult(
    key: String,
    initialValue: T? = null,
    onResult: (T) -> Unit = {},
) {
    savedStateHandle
        .getStateFlow(key, initialValue)
        .collectAsStateWithLifecycle()
        .value
        ?.also(onResult)
        ?.let {
            savedStateHandle[key] = null
            savedStateHandle.remove<T>(key)
        }
}

inline fun <reified T : Any> NavController.navigateOnResumed(
    noinline builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigateOnResumed(route = T::class, builder = builder)
}

fun <T : Any> NavController.navigateOnResumed(
    route: T,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    runOnResumed {
        navigate(route, navOptions(builder))
    }
}

@Composable
fun <T> NavController.LaunchedParamsEffect(
    key: String,
    clear: Boolean = true,
    onResult: (T) -> Unit = {}
) {
    val params = get<T>(key)
    val currentOnResult by rememberUpdatedState(onResult)

    LaunchedEffect(key1 = params) {
        if (params != null) {
            if (clear) clear<T>(key)

            currentOnResult(params)
        }
    }
}

fun <T> NavController.get(key: String): T? = currentBackStackEntry?.savedStateHandle?.get(key)

fun <T> NavController.set(key: String, value: T) {
    currentBackStackEntry?.savedStateHandle?.set(key, value)
}

fun <T> NavController.clear(key: String): T? {
    currentBackStackEntry?.savedStateHandle?.set(key, null)
    return currentBackStackEntry?.savedStateHandle?.remove<T>(key)
}

fun NavBackStackEntry.getStringArg(key: String): String? = arguments?.read { getStringOrNull(key) }

fun NavBackStackEntry.getIntArg(key: String): Int? = arguments?.read { getIntOrNull(key) }

fun NavBackStackEntry.getLongArg(key: String): Long? = arguments?.read { getLongOrNull(key) }

fun NavBackStackEntry.getBooleanArg(key: String) = arguments?.read { getBooleanOrNull(key) }
