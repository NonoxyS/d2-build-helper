package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models

import dev.nonoxy.d2buildhelper.core.network.deserializer.fallbackEnumSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = RemoteMatchLaneSerializer::class)
internal enum class RemoteMatchLane {
    ROAMING,
    SAFE_LANE,
    MID_LANE,
    OFF_LANE,
    JUNGLE,
    UNKNOWN,
}

@OptIn(ExperimentalSerializationApi::class)
internal object RemoteMatchLaneSerializer : KSerializer<RemoteMatchLane> by fallbackEnumSerializer(
    generatedSerializer = RemoteMatchLane.generatedSerializer(),
    fallback = RemoteMatchLane.UNKNOWN,
)
