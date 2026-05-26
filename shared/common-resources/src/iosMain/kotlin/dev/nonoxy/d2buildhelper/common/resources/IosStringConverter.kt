package dev.nonoxy.d2buildhelper.common.resources

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.desc

internal class IosStringConverter : StringConverter {
    override fun convert(stringResource: StringResource) =
        stringResource.desc().localized()

    override fun convert(resourceFormattedString: ResourceFormattedStringDesc) =
        resourceFormattedString.localized()
}