package wallapp.resource

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ImageVectorWrapperSerializer : KSerializer<ImageVectorWrapper> {

    @OptIn(ExperimentalSerializationApi::class)
    @InternalSerializationApi
    override val descriptor: SerialDescriptor = buildSerialDescriptor(
        "ImageVectorWrapper",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: ImageVectorWrapper) {
        // Implement your serialization logic here
        throw NotImplementedError("Serialization of ImageVectorWrapper is not supported")
    }

    override fun deserialize(decoder: Decoder): ImageVectorWrapper {
        // Implement your deserialization logic here
        throw NotImplementedError("Deserialization of ImageVectorWrapper is not supported")
    }
}