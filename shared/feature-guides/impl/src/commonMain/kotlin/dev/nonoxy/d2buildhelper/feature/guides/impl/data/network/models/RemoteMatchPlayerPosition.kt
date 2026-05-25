package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models

import dev.nonoxy.d2buildhelper.core.network.deserializer.fallbackEnumSerializer
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
internal enum class RemoteMatchPlayerPosition {
    POSITION_1,
    POSITION_2,
    POSITION_3,
    POSITION_4,
    POSITION_5,
    UNKNOWN,
}

internal object RemoteMatchPlayerPositionSerializer :
    KSerializer<RemoteMatchPlayerPosition> by fallbackEnumSerializer(RemoteMatchPlayerPosition.UNKNOWN)

internal fun RemoteMatchPlayerPosition.toDomainOrNull(): MatchPlayerPosition? = when (this) {
    RemoteMatchPlayerPosition.POSITION_1 -> MatchPlayerPosition.POSITION_1
    RemoteMatchPlayerPosition.POSITION_2 -> MatchPlayerPosition.POSITION_2
    RemoteMatchPlayerPosition.POSITION_3 -> MatchPlayerPosition.POSITION_3
    RemoteMatchPlayerPosition.POSITION_4 -> MatchPlayerPosition.POSITION_4
    RemoteMatchPlayerPosition.POSITION_5 -> MatchPlayerPosition.POSITION_5
    RemoteMatchPlayerPosition.UNKNOWN -> null
}
