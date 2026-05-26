package dev.nonoxy.d2buildhelper.common.resources

import android.content.Context
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc

internal class AndroidStringConverter(
    private val context: Context
) : StringConverter {

    override fun convert(stringResource: StringResource) =
        context.getString(stringResource.resourceId)

    override fun convert(resourceFormattedString: ResourceFormattedStringDesc): String =
        resourceFormattedString.toString(context)
}