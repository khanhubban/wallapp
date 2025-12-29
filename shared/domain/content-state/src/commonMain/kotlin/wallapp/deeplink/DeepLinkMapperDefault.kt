package wallapp.deeplink

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import wallapp.content.model.Id
import wallapp.data.content.ContentRepository
import wallapp.log.Log
import wallapp.screen.ScreenArgument
import wallapp.string.quote

class DeepLinkMapperDefault(
    private val contentRepository: ContentRepository,
    private val slugResolver: DeepLinkSlugResolver,
) : DeepLinkMapper {

    private val deepLinkMappings: Flow<List<DeepLinkMapping>>
        get() = contentRepository.deepLinkMappings

    override fun map(url: String): Flow<ScreenArgument?> = flow {
        Log.d("[deeplink] DeepLinkMapper.map($url)")

        val slug = slugResolver.resolve(url) ?: return@flow emit(null)
        if (slug.isEmpty()) return@flow emit(null)
        Log.d("[deeplink] DeepLinkMapper slug: ${slug.quote()}")

        deepLinkMappings
            .filter { it.isNotEmpty() }
            .collect { mappings ->
                val mapping = mappings.firstOrNull(slug)
                if (mapping != null) {
                    Log.d("[deeplink] DeepLinkMapper.mapping: $mapping")
                    mapping.toScreenArgument?.let { emit(it) }
                } else {
                    Log.v("[deeplink] No mapping found for ${slug.quote()}")
                }
            }
    }

    override fun map(id: Id) = id.toScreenArgument

}