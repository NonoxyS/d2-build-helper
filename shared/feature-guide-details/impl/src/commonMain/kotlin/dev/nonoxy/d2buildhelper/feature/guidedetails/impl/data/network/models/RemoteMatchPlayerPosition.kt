package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models

import dev.nonoxy.d2buildhelper.core.network.deserializer.fallbackEnumSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = RemoteMatchPlayerPositionSerializer::class)
internal enum class RemoteMatchPlayerPosition {
    POSITION_1,
    POSITION_2,
    POSITION_3,
    POSITION_4,
    POSITION_5,
    UNKNOWN,
}

@OptIn(ExperimentalSerializationApi::class)
internal object RemoteMatchPlayerPositionSerializer : KSerializer<RemoteMatchPlayerPosition> by fallbackEnumSerializer(
    generatedSerializer = RemoteMatchPlayerPosition.generatedSerializer(),
    fallback = RemoteMatchPlayerPosition.UNKNOWN,
)
