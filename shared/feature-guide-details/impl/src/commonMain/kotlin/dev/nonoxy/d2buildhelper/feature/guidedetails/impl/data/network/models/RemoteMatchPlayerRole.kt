package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models

import dev.nonoxy.d2buildhelper.core.network.deserializer.fallbackEnumSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = RemoteMatchPlayerRoleSerializer::class)
internal enum class RemoteMatchPlayerRole {
    CORE,
    LIGHT_SUPPORT,
    HARD_SUPPORT,
    UNKNOWN,
}

@OptIn(ExperimentalSerializationApi::class)
internal object RemoteMatchPlayerRoleSerializer : KSerializer<RemoteMatchPlayerRole> by fallbackEnumSerializer(
    generatedSerializer = RemoteMatchPlayerRole.generatedSerializer(),
    fallback = RemoteMatchPlayerRole.UNKNOWN,
)
