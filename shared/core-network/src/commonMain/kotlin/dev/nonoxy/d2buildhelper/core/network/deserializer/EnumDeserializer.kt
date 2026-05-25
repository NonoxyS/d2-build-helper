package dev.nonoxy.d2buildhelper.core.network.deserializer

import io.github.aakira.napier.Napier
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Wraps an existing enum serializer for [T] and substitutes [fallback] whenever the wire value
 * does not match any declared enum entry. Element names (including `@SerialName` overrides)
 * are taken from [generated]'s descriptor — no manual mapping required.
 *
 * Use for forward-compatible DTO enums where the backend may introduce new values.
 *
 * Recommended setup — `@KeepGeneratedSerializer` keeps the plugin-generated serializer
 * accessible as `T.generatedSerializer()` even when `@Serializable(with = …)` is present.
 * That lets you point every use-site at the enum without sprinkling `@Serializable(with = …)`
 * on every field:
 *
 * ```
 * @OptIn(ExperimentalSerializationApi::class)
 * @KeepGeneratedSerializer
 * @Serializable(with = RemoteFooSerializer::class)
 * enum class RemoteFoo {
 *     @SerialName("known_value") KNOWN,
 *     UNKNOWN,
 * }
 *
 * @OptIn(ExperimentalSerializationApi::class)
 * object RemoteFooSerializer : KSerializer<RemoteFoo> by fallbackEnumSerializer(
 *     generatedSerializer = RemoteFoo.generatedSerializer(),
 *     fallback = RemoteFoo.UNKNOWN
 * )
 *
 * @Serializable
 * data class FooHolder(val foo: RemoteFoo)   // no per-field @Serializable(with = …)
 * ```
 *
 * Do NOT pass `serializer<T>()` here when `@Serializable(with = X)` points back to the same
 * object — that produces a self-referential delegate and a StackOverflowError on first
 * encode/decode. Always thread the generated serializer through explicitly.
 */
inline fun <reified T : Enum<T>> fallbackEnumSerializer(
    generatedSerializer: KSerializer<T>,
    fallback: T,
): KSerializer<T> = object : KSerializer<T> {

    override val descriptor = generatedSerializer.descriptor

    override fun deserialize(decoder: Decoder): T {
        val name = decoder.decodeString()
        return nameToValue[name] ?: fallback.also {
            val exception = IllegalArgumentException("Unknown enum value found: $name")
            Napier.e(message = "Error during deserializing enum", throwable = exception)
        }
    }

    override fun serialize(encoder: Encoder, value: T) {
        generatedSerializer.serialize(encoder, value)
    }

    private val enumValues = enumValues<T>()
    private val nameToValue: Map<String, T> = buildMap(enumValues.size) {
        for (index in 0 until descriptor.elementsCount) {
            put(descriptor.getElementName(index), enumValues[index])
        }
    }
}
