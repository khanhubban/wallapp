package wallapp.pipeline

import wallapp.pipeline.build.*
import wallapp.pipeline.manifest.PipelineManifest
import wallapp.pipeline.publish.*
import wallapp.pipeline.validate.CatalogValidator

private const val IMGIX_PREFIX = "https://stillscenes.imgix.net"

fun runPipeline(manifestText: String, putter: ObjectPutter, fetcher: ObjectFetcher, renditionFiles: Map<String, String>) {
    val m = PipelineManifest.parse(manifestText)
    val bundle = WireBundle(
        content = CatalogBuilder.build(m),
        search = SearchBuilder.build(m),
        media = MediaMapBuilder.build(m),
        baseUrl = m.baseUrl,
        imgixHostPrefix = IMGIX_PREFIX,
    )
    CatalogValidator.validate(bundle)                       // fail-fast BEFORE any upload
    Publisher(putter, fetcher, m.baseUrl).publish(bundle, renditionFiles, m.version)
}

fun main(args: Array<String>) {
    val manifestPath = args.firstOrNull() ?: error("usage: publish <manifest.json>")
    val text = java.io.File(manifestPath).readText()
    val m = PipelineManifest.parse(text)
    val dir = java.io.File(manifestPath).parentFile
    // remote key == manifest relative path; local file lives under the manifest dir
    val renditions = buildMap {
        m.wallpapers.forEach { put(it.downloadRenditionPath, java.io.File(dir, it.downloadRenditionPath).absolutePath)
                               put(it.previewRenditionPath, java.io.File(dir, it.previewRenditionPath).absolutePath) }
        put(m.artist.profileImagePath, java.io.File(dir, m.artist.profileImagePath).absolutePath)
        put(m.folder.profileImagePath, java.io.File(dir, m.folder.profileImagePath).absolutePath)
        put(m.folder.featureBannerImagePath, java.io.File(dir, m.folder.featureBannerImagePath).absolutePath)
    }
    runPipeline(text, RcloneClient(remote = "r2staging", bucket = "stillscenes-content-staging"),
                CdnReadBackVerifier(), renditions)
    println("Published version ${m.version}. Flip RC catalog_version to ${m.version} to go live.")
}
