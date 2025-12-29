package wallapp.wallpapers.preview

import org.json.JSONException
import org.json.JSONObject
import wallapp.content.model.Id.DesignId
import wallapp.content.model.Id.RemixId
import wallapp.json.getOpt
import wallapp.wallpaper.bucket.DataBucketLegacy


data class WallpaperPreviewConfig(
    val designId: DesignId?,
    val remixId: RemixId?,
    val deepLinkRemixId: RemixId? = null,
    val deepLinkDataBucketLegacy: DataBucketLegacy? = null,
) {

    fun asExportString(): String = JSONObject().apply {
        designId?.also { put("d", it.name) }
        remixId?.also { put("r", it.name) }
        deepLinkRemixId?.also { put("dr", it.name) }
        deepLinkDataBucketLegacy?.also { put("db", it.bucketId) }
    }.toString()

    companion object {

        fun fromExportString(exportString: String): WallpaperPreviewConfig? {
            return try {
                val jsonObject = JSONObject(exportString)
                val designId = jsonObject.getOpt<String>("d")?.let { DesignId(it) }
                val remixId = jsonObject.getOpt<String>("r")?.let { RemixId(it) }
                val deepLinkRemixId = jsonObject.getOpt<String>("dr")?.let { RemixId(it) }
                val deepLinkDataBucketLegacy = jsonObject.getOpt<String>("db")?.let { DataBucketLegacy(it) }
                if (designId != null || remixId != null || deepLinkRemixId != null || deepLinkDataBucketLegacy != null) {
                    WallpaperPreviewConfig(designId, remixId, deepLinkRemixId, deepLinkDataBucketLegacy)
                } else {
                    null
                }
            } catch (ex: JSONException) {
                null
            }
        }
    }

}