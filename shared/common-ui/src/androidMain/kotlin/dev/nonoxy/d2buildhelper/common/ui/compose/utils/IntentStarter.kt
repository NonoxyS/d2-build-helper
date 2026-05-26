package dev.nonoxy.d2buildhelper.common.ui.compose.utils

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri

internal sealed interface IntentStarter {
    fun start(context: Context, onFail: (Throwable) -> Unit)
}

data class CallPhoneStarter(
    private val phone: String
) : IntentStarter {
    override fun start(context: Context, onFail: (Throwable) -> Unit) {
        Intent(Intent.ACTION_DIAL).startActivityWithCheckResolve(
            context = context,
            uri = "$PREFIX_TEL$phone",
            onFail = onFail,
        )
    }

    companion object {
        const val PREFIX_TEL = "tel:"
    }
}

data class ShareImageStarter(
    private val fileUri: Uri?,
    private val extraText: String? = null,
) : IntentStarter {
    override fun start(context: Context, onFail: (Throwable) -> Unit) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*|text/*"

            extraText?.let { putExtra(Intent.EXTRA_TEXT, it) }
            fileUri?.let {
                clipData = ClipData.newRawUri("", it)
                putExtra(Intent.EXTRA_STREAM, it)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        Intent.createChooser(shareIntent, null).startActivityWithCheckResolve(
            context = context,
            uri = null,
            onFail = onFail
        )
    }
}

data class EmailStarter(
    private val email: String,
    private val subject: String? = null,
    private val message: String? = null,
) : IntentStarter {
    override fun start(context: Context, onFail: (Throwable) -> Unit) {
        Intent(Intent.ACTION_SENDTO)
            .apply {
                if (subject != null) putExtra(Intent.EXTRA_SUBJECT, subject)
                if (message != null) putExtra(Intent.EXTRA_TEXT, message)
            }
            .startActivityWithCheckResolve(
                context = context,
                uri = "$PREFIX_EMAIL$email",
                onFail = onFail,
            )
    }

    companion object {
        const val PREFIX_EMAIL = "mailto:"
    }
}

data class SiteStarter(
    private val siteUrl: String,
) : IntentStarter {
    override fun start(context: Context, onFail: (Throwable) -> Unit) {
        val intent = openUrlIntent(siteUrl)
        intent.startActivityWithCheckResolve(
            context = context,
            uri = intent.data ?: Uri.EMPTY,
            onFail = onFail
        )
    }
}

data class MapStarter(
    private val address: String,
    private val latitude: Double,
    private val longitude: Double,
) : IntentStarter {
    override fun start(context: Context, onFail: (Throwable) -> Unit) {
        Intent(Intent.ACTION_VIEW).startActivityWithCheckResolve(
            context = context,
            uri = "geo:$latitude,$longitude?q=$address",
            onFail = onFail,
        )
    }
}

data class SettingsStarter(
    val applicationId: String,
    val action: String = Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
    val uriSchemePackage: String = URI_SCHEME_PACKAGE,
) : IntentStarter {
    override fun start(context: Context, onFail: (Throwable) -> Unit) {
        Intent(action).startActivityWithCheckResolve(
            context = context,
            uri = Uri.fromParts(uriSchemePackage, applicationId, null),
            onFail = onFail,
        )
    }

    companion object {
        private const val URI_SCHEME_PACKAGE = "package"
    }
}

fun Intent.startActivityWithCheckResolve(
    context: Context,
    uri: String,
    onFail: (Throwable) -> Unit,
) = startActivityWithCheckResolve(
    uri = uri.toUri(),
    context = context,
    onFail = onFail
)

fun Intent.startActivityWithCheckResolve(
    uri: Uri?,
    context: Context,
    onFail: (Throwable) -> Unit,
) {
    data = uri
    try {
        context.startActivity(this)
    } catch (e: ActivityNotFoundException) {
        onFail(e)
    }
}

private fun openUrlIntent(siteUrl: String): Intent {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = if (!siteUrl.startsWith("http://") && !siteUrl.startsWith("https://")) {
            "https://$siteUrl"
        } else {
            siteUrl
        }.toUri()
    }
    return intent
}
