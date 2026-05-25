package dev.nonoxy.d2buildhelper.core.network.deserializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = TestEnumSerializer::class)
internal enum class TestEnum {
    KNOWN_A,

    @SerialName("renamed-b")
    KNOWN_B,
    UNKNOWN,
}

@OptIn(ExperimentalSerializationApi::class)
internal object TestEnumSerializer : KSerializer<TestEnum> by fallbackEnumSerializer(
    generatedSerializer = TestEnum.generatedSerializer(),
    fallback = TestEnum.UNKNOWN,
)

@Serializable
internal data class Holder(val value: TestEnum)

class FallbackEnumSerializerTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decode known enum entry by name`() {
        val decoded = json.decodeFromString<Holder>("""{"value":"KNOWN_A"}""")
        assertEquals(TestEnum.KNOWN_A, decoded.value)
    }

    @Test
    fun `decode known enum entry by SerialName override`() {
        val decoded = json.decodeFromString<Holder>("""{"value":"renamed-b"}""")
        assertEquals(TestEnum.KNOWN_B, decoded.value)
    }

    @Test
    fun `decode unknown wire value falls back to UNKNOWN sentinel`() {
        val decoded = json.decodeFromString<Holder>("""{"value":"POSITION_42"}""")
        assertEquals(TestEnum.UNKNOWN, decoded.value)
    }

    @Test
    fun `encode round-trip uses generated names`() {
        val knownA = json.encodeToString(Holder.serializer(), Holder(TestEnum.KNOWN_A))
        val knownB = json.encodeToString(Holder.serializer(), Holder(TestEnum.KNOWN_B))
        assertEquals("""{"value":"KNOWN_A"}""", knownA)
        assertEquals("""{"value":"renamed-b"}""", knownB)
    }
}
