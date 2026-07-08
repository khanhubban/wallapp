package wallapp.pipeline.publish

interface ObjectPutter { fun put(localPath: String, remoteKey: String) }
interface ObjectFetcher { fun fetch(url: String): ByteArray? }

/** PUTs one object to R2 via `rclone copyto <local> <remote>:<bucket>/<key>`. */
class RcloneClient(private val remote: String, private val bucket: String) : ObjectPutter {
    override fun put(localPath: String, remoteKey: String) {
        val p = ProcessBuilder("rclone", "copyto", localPath, "$remote:$bucket/$remoteKey")
            .redirectErrorStream(true).start()
        val out = p.inputStream.bufferedReader().readText()
        check(p.waitFor() == 0) { "rclone put failed for $remoteKey: $out" }
    }
}
