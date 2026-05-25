package ru.mirea.toir.core.network.deserializer

import io.github.aakira.napier.Napier
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A generic serializer for enum classes using Kotlin Serialization.
 *
 * This serializer handles the serialization and deserialization of enum values as strings,
 * using either the `serialName` (if available) or the regular `name` of the enum.
 *
 * @param T The enum type to serialize.
 * @param fallbackEnum The fallback enum, if deserialization fails
 */

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Enum<T>> enumSerializerWithFallback(
    fallbackEnum: T,
    serialNameMapping: Map<T, String>
): KSerializer<T> = object : KSerializer<T> {

    private val enumValues = enumValues<T>()

    private val nameToEnum: Map<String, T> by lazy {
        buildMap {
            enumValues.forEach { enumValue ->
                val serialName = serialNameMapping[enumValue]

                if (serialName == null) {
                    put(enumValue.name.lowercase(), enumValue)
                } else {
                    put(serialName, enumValue)
                }
            }
        }
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "EnumSerializer<${T::class.simpleName}>",
        kind = PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: T) {
        val serialName = serialNameMapping[value] ?: value.name
        encoder.encodeString(serialName)
    }

    override fun deserialize(decoder: Decoder): T {
        val decodeString = decoder.decodeString()
        return nameToEnum[decodeString]
            ?: nameToEnum[decodeString.lowercase()]
            ?: fallbackEnum.also {
                val exception = IllegalArgumentException("Unknown enum value found: $decodeString")
                Napier.e(message = "Error during deserializing enum", throwable = exception)
            }
    }
}
