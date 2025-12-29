package wallapp.deeplink

import wallapp.content.model.Id

data class DeepLinkMapping(
    val id: Id,
    val slugs: List<String>?,
)
