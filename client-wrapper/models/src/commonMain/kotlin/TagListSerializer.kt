package qbittorrent.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object TagListSerializer : KSerializer<List<String>> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Tag", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<String> {
        return decoder.decodeString().split(",")
    }

    override fun serialize(encoder: Encoder, value: List<String>) {
        encoder.encodeString(value.joinToString(","))
    }
}
