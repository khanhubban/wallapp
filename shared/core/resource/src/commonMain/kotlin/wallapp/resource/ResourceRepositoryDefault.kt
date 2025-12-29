package wallapp.resource

import wallapp.shared.core.resource.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
class ResourceRepositoryDefault : ResourceRepository {

    override suspend fun readBytes(fileName: String): ByteArray {
        return Res.readBytes(fileName)
    }
}