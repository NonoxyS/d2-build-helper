package dev.nonoxy.d2buildhelper.core.resources.data.local.constants

import dev.icerock.moko.resources.FileResource

internal actual class FileContentReader {
    actual fun read(resource: FileResource): String = resource.readText()
}
