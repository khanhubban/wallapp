package wallapp.pipeline

import wallapp.pipeline.build.*
import wallapp.pipeline.manifest.PipelineManifest
import wallapp.pipeline.publish.*
import wallapp.pipeline.validate.CatalogValidator

data class PipelineArgs(val manifestPath: String, val dryRun: Boolean)

private val KnownFlags = setOf("--dry-run")

fun parseArgs(args: Array<String>): PipelineArgs {
    args.filter { it.startsWith("--") }.forEach { flag ->
        if (flag !in KnownFlags) error("unknown flag: $flag (known: ${KnownFlags.sorted()})")
    }
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

/**
 * The bucket is derived from the manifest's baseUrl rather than passed separately: baseUrl already
 * declares the environment, and a second way to say it is a second way to say it wrong. A prod
 * manifest can only reach the prod bucket. An unrecognised host publishes nowhere.
 */
private val BucketForHost: Map<String, String> = mapOf(
    "media-staging.stillscenes.app" to "stillscenes-content-staging",
    "media.stillscenes.app" to "stillscenes-content-prod",
)

/** Remote Config key each environment's catalog version lives under. */
private val CatalogVersionKeyForHost: Map<String, String> = mapOf(
    "media-staging.stillscenes.app" to "catalog_version_staging",
    "media.stillscenes.app" to "catalog_version",
)

internal fun hostOf(baseUrl: String): String =
    baseUrl.substringAfter("://").substringBefore('/').substringBefore(':')

internal fun bucketFor(baseUrl: String): String {
    val host = hostOf(baseUrl)
    return BucketForHost[host]
        ?: error("manifest baseUrl host '$host' maps to no known R2 bucket; known: ${BucketForHost.keys.sorted()}")
}

internal fun catalogVersionKeyFor(baseUrl: String): String {
    val host = hostOf(baseUrl)
    return CatalogVersionKeyForHost[host] ?: error("no catalog_version key known for host '$host'")
}

fun main(args: Array<String>) {
    val parsed = parseArgs(args)
    val text = java.io.File(parsed.manifestPath).readText()

    if (parsed.dryRun) {
        // Build + validate only. Deliberately does not call runPipeline: that function's job is to
        // construct Publisher, and it is `main` — not runPipeline — that builds the R2/HTTP clients
        // and passes them in below. A dry run must never bring either the clients or Publisher into
        // being, so it stops here, before either is constructed.
        val (m, bundle) = buildAndValidate(text)
        println("DRY RUN — validated ${m.version} for bucket ${bucketFor(m.baseUrl)}, " +
                "${bundle.media.mediaMap.size} media entries, nothing uploaded")
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
    val bucket = bucketFor(m.baseUrl)
    runPipeline(text, WranglerClient(bucket = bucket), CdnReadBackVerifier(), renditions)
    println("Published version ${m.version} to $bucket. Ensure RC ${catalogVersionKeyFor(m.baseUrl)} is ${m.version} to go live (no flip needed if it already is).")
}
