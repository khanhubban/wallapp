package wallapp.resources

import wallapp.resource.LocalFileAsset
import wallapp.resource.Resource
import kotlinx.serialization.json.JsonNull.content

/**
 * Represents a local file asset that is bundled with the application.
 *
 * This has been added as part of the open source release to avoid the need for Firebase.
 */
sealed class LocalFileAssetBundled(val fileName: String): LocalFileAsset {
    override fun toResource(): Resource {
        return this.toResourceNative()
    }

    data object ApiContentData : LocalFileAssetBundled("api/v0/content-1a.data")
    data object ApiContentMetadata : LocalFileAssetBundled("api/v0/content-metadata-1a.data")
    data object ApiEncryptionKey : LocalFileAssetBundled("api/v0/key1")
    data object ApiMedia_C_F_Fo : LocalFileAssetBundled("api/v0/media-1a-c-f~fo.data")
    data object ApiMedia_C_P_A_N : LocalFileAssetBundled("api/v0/media-1a-c-p~a~n.data")
    data object ApiMedia_C_P_A_Xl : LocalFileAssetBundled("api/v0/media-1a-c-p~a~xl.data")
    data object ApiMedia_C_P_Five0 : LocalFileAssetBundled("api/v0/media-1a-c-p~five0.data")
    data object ApiMedia_C_P_S : LocalFileAssetBundled("api/v0/media-1a-c-p~s.data")
    data object ApiMedia_C_P_Uhd : LocalFileAssetBundled("api/v0/media-1a-c-p~uhd.data")
    data object ApiMedia_C_T_L : LocalFileAssetBundled("api/v0/media-1a-c-t~l.data")
    data object ApiMedia_C_T_M : LocalFileAssetBundled("api/v0/media-1a-c-t~m.data")
    data object ApiMedia_C_T_S : LocalFileAssetBundled("api/v0/media-1a-c-t~s.data")
    data object ApiMedia_I_F_Fo : LocalFileAssetBundled("api/v0/media-1a-i-f~fo.data")
    data object ApiMedia_I_P_A_N : LocalFileAssetBundled("api/v0/media-1a-i-p~a~n.data")
    data object ApiMedia_I_P_A_Xl : LocalFileAssetBundled("api/v0/media-1a-i-p~a~xl.data")
    data object ApiMedia_I_P_Five0 : LocalFileAssetBundled("api/v0/media-1a-i-p~five0.data")
    data object ApiMedia_I_P_S : LocalFileAssetBundled("api/v0/media-1a-i-p~s.data")
    data object ApiMedia_I_P_Uhd : LocalFileAssetBundled("api/v0/media-1a-i-p~uhd.data")
    data object ApiMedia_I_T_L : LocalFileAssetBundled("api/v0/media-1a-i-t~l.data")
    data object ApiMedia_I_T_M : LocalFileAssetBundled("api/v0/media-1a-i-t~m.data")
    data object ApiMedia_I_T_S : LocalFileAssetBundled("api/v0/media-1a-i-t~s.data")
    data object ApiSpecJsonData : LocalFileAssetBundled("api/v0/spec.json.data")

    companion object {
        val All = listOf(
            ApiContentData,
            ApiContentMetadata,
            ApiEncryptionKey,
            ApiMedia_C_F_Fo,
            ApiMedia_C_P_A_N,
            ApiMedia_C_P_A_Xl,
            ApiMedia_C_P_Five0,
            ApiMedia_C_P_S,
            ApiMedia_C_P_Uhd,
            ApiMedia_C_T_L,
            ApiMedia_C_T_M,
            ApiMedia_C_T_S,
            ApiMedia_I_F_Fo,
            ApiMedia_I_P_A_N,
            ApiMedia_I_P_A_Xl,
            ApiMedia_I_P_Five0,
            ApiMedia_I_P_S,
            ApiMedia_I_P_Uhd,
            ApiMedia_I_T_L,
            ApiMedia_I_T_M,
            ApiMedia_I_T_S,
            ApiSpecJsonData,
        )
    }
}

expect fun LocalFileAssetBundled.toResourceNative(): Resource
