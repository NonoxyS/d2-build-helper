package dev.nonoxy.d2buildhelper.core.resources.data.local.constants

import android.content.Context
import dev.icerock.moko.resources.FileResource

internal actual class FileContentReader(private val context: Context) {
    actual fun read(resource: FileResource): String = resource.readText(context)
}
