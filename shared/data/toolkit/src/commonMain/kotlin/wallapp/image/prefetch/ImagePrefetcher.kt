package wallapp.image.prefetch

import wallapp.util.CancellableWork
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("ImagePrefetcherEx")
interface ImagePrefetcher {

    fun prefetch(imagePrefetchData: ImagePrefetchData)

    fun prefetch(
        imagePrefetchEntry: ImagePrefetchEntry,
        onCompletion: (() -> Unit)? = null,
    ): CancellableWork?
}

object ImagePrefetcherNoOp : ImagePrefetcher {

    override fun prefetch(imagePrefetchData: ImagePrefetchData) { }

    override fun prefetch(
        imagePrefetchEntry: ImagePrefetchEntry,
        onCompletion: (() -> Unit)?,
    ): CancellableWork? = null
}