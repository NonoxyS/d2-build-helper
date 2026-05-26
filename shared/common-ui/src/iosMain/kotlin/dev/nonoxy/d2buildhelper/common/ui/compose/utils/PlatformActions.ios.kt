package dev.nonoxy.d2buildhelper.common.ui.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.Foundation.create
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.UIKit.UIApplication

private class IosPlatformActions : PlatformActions {

    private val app = UIApplication.sharedApplication

    override fun openUrl(url: String, onFail: () -> Unit) {
        openUrlSafely(urlString = url, onFail = onFail)
    }

    override fun openEmailClient(email: String, onFail: () -> Unit) {
        openUrlSafely(urlString = "mailto:$email", onFail = onFail)
    }

    override fun openMap(address: String, onFail: () -> Unit) {
        val encoded = address.encodeURLQuery()
        openUrlSafely(
            urlString = "http://maps.apple.com/?q=$encoded",
            onFail = onFail
        )
    }

    override fun openPhonebook(phone: String, onFail: () -> Unit) {
        openUrlSafely(urlString = "tel:$phone", onFail = onFail)
    }

    @OptIn(BetaInteropApi::class)
    private fun String.encodeURLQuery(): String {
        val nsString = NSString.create(string = this)
        return nsString.stringByAddingPercentEncodingWithAllowedCharacters(
            allowedCharacters = NSCharacterSet.URLQueryAllowedCharacterSet()
        ) ?: this
    }

    private fun openUrlSafely(urlString: String, onFail: () -> Unit) {
        val nsUrl = NSURL.URLWithString(urlString)
        if (nsUrl == null) {
            onFail()
            return
        }

        app.openURL(
            url = nsUrl,
            options = emptyMap<Any?, Any?>()
        ) { success -> if (!success) onFail() }
    }
}

@Composable
actual fun rememberPlatformActions(): PlatformActions = remember { IosPlatformActions() }
