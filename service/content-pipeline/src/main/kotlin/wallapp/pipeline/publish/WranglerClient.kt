package wallapp.pipeline.publish

/** Uploads one object to R2 via `wrangler r2 object put`. Uses the operator's existing wrangler OAuth (no rclone token). */
class WranglerClient(
    private val bucket: String,
    private val exec: (List<String>) -> ExecResult = WranglerClient::realExec,
) : ObjectPutter {
    data class ExecResult(val code: Int, val output: String)

    override fun put(localPath: String, remoteKey: String) {
        val contentType = if (remoteKey.endsWith(".webp")) "image/webp" else "application/json"
        val cmd = listOf(
            "wrangler", "r2", "object", "put", "$bucket/$remoteKey",
            "--file=$localPath", "--remote",
            "--content-type=$contentType",
            "--cache-control=public, max-age=31536000, immutable",
        )
        val r = exec(cmd)
        check(r.code == 0) { "wrangler put failed for $remoteKey (exit ${r.code}): ${r.output}" }
    }

    companion object {
        private fun realExec(cmd: List<String>): ExecResult {
            val p = ProcessBuilder(cmd).redirectErrorStream(true).start()
            val out = p.inputStream.bufferedReader().readText()
            return ExecResult(p.waitFor(), out)
        }
    }
}
