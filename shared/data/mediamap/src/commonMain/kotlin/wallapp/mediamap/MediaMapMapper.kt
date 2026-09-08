package wallapp.mediamap

import wallapp.image.ImageModel
import wallapp.image.sized.SizedImage
import wallapp.media.model.MediaId
import wallapp.media.network.model.NetworkMediaMap

object MediaMapMapper {

    private val Log = MediaMapLogger

    fun mapToMediaMap(networkMediaMaps: Map<Long, NetworkMediaMap>): Map<MediaId, MediaMap> {
        return mutableMapOf<MediaId, MutableMap<SizedImage, ImageModel>>().apply {
            networkMediaMaps.forEach { (mediaId, networkMediaMap) ->
                val mediaMap = mutableMapOf<SizedImage, ImageModel>()
                networkMediaMap.forEach { (sizedImage, url) ->
                    val known = SizedImage.fromOrNull(sizedImage)
                    if (known == null) {
                        Log.w("Unknown SizedImage key $sizedImage on mediaId $mediaId; entry ignored")
                    } else {
                        mediaMap[known] = ImageModel.from(url)
                    }
                }

                put(MediaId(mediaId), mediaMap)
            }
        }
    }
}
