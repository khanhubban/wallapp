package wallapp.pipeline

import wallapp.pipeline.build.*
import wallapp.pipeline.manifest.PipelineManifest
import wallapp.pipeline.publish.*
import wallapp.pipeline.validate.CatalogValidator

data class PipelineArgs(val manifestPath: String, val dryRun: Boolean)

fun parseArgs(args: Array<String>): PipelineArgs {
    val dryRun = args.any { it == "--dry-run" }
    val manifestPath = args.firstOrNull { !it.startsWith("--") }
        ?: error("usage: publish <manifest.json> [--dry-run]")
    return PipelineArgs(manifestPath, dryRun)
}

/** Parses, builds and validates the wire bundle. Pure computation — never touches the network. */
private fun buildAndValidate(manifestText: String): Pair<PipelineManifest, WireBundle> {
    val m = PipelineManifest.parse(manifestText)
    val bundle = WireBundle(
        content = CatalogBuilder.build(m),
        search = SearchBuilder.build(m),
        media = MediaMapBuilder.build(m),
        baseUrl = m.baseUrl,
        imgixHostPrefix = IMGIX_HOST_PREFIX,
    )
    CatalogValidator.validate(bundle)                       // fail-fast BEFORE any upload
    return m to bundle
}

fun runPipeline(manifestText: String, putter: ObjectPutter, fetcher: ObjectFetcher, renditionFiles: Map<String, String>) {
    val (m, bundle) = buildAndValidate(manifestText)
    Publisher(putter, fetcher, m.baseUrl).publish(bundle, renditionFiles, m.version)
}

fun main(args: Array<String>) {
    val parsed = parseArgs(args)
    val text = java.io.File(parsed.manifestPath).readText()

    if (parsed.dryRun) {
        // Build + validate only. Deliberately does not call runPipeline: that function's job is to
        // construct Publisher and its R2/HTTP clients, which a dry run must never bring into being.
        val (m, bundle) = buildAndValidate(text)
        println("DRY RUN — validated ${m.version}, ${bundle.media.mediaMap.size} media entries, nothing uploaded")
        println(bundle.media.exportString)
        return
    }

    val m = PipelineManifest.parse(text)
    val dir = java.io.File(parsed.manifestPath).parentFile
    // remote key == manifest relative path; local file lives under the manifest dir
    val renditions = buildMap {
        m.wallpapers.forEach { put(it.downloadRenditionPath, java.io.File(dir, it.downloadRenditionPath).absolutePath)
                               put(it.previewRenditionPath, java.io.File(dir, it.previewRenditionPath).absolutePath) }
        put(m.artist.profileImagePath, java.io.File(dir, m.artist.profileImagePath).absolutePath)
        put(m.folder.profileImagePath, java.io.File(dir, m.folder.profileImagePath).absolutePath)
        put(m.folder.featureBannerImagePath, java.io.File(dir, m.folder.featureBannerImagePath).absolutePath)
    }
    runPipeline(text, WranglerClient(bucket = "stillscenes-content-staging"),
                CdnReadBackVerifier(), renditions)
    println("Published version ${m.version}. Flip RC catalog_version to ${m.version} to go live.")
}
