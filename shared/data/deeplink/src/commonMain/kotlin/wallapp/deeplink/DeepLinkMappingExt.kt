package wallapp.deeplink


fun List<DeepLinkMapping>.firstOrNull(slug: String): DeepLinkMapping? {
    return find { mapping ->
        mapping.slugs?.contains(slug) ?: false
    }
}