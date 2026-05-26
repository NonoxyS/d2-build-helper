package dev.nonoxy.d2buildhelper.common.ui.compose.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private class AndroidPlatformActions(
    private val context: Context
) : PlatformActions {

    override fun openUrl(url: String, onFail: () -> Unit) {
        SiteStarter(siteUrl = url).start(
            context = context,
            onFail = { onFail() }
        )
    }

    override fun openEmailClient(email: String, onFail: () -> Unit) {
        EmailStarter(email = email).start(
            context = context,
            onFail = { onFail() }
        )
    }

    override fun openMap(address: String, onFail: () -> Unit) {
        MapStarter(
            address = address,
            latitude = 0.0,
            longitude = 0.0
        ).start(
            context = context,
            onFail = { onFail() }
        )
    }

    override fun openPhonebook(phone: String, onFail: () -> Unit) {
        CallPhoneStarter(phone = phone).start(
            context = context,
            onFail = { onFail() }
        )
    }
}

@Composable
actual fun rememberPlatformActions(): PlatformActions {
    val context = LocalContext.current
    return remember(context) { AndroidPlatformActions(context) }
}
