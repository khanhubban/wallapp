package wallapp.viewmodel

import wallapp.log.Log
import wallapp.util.WeakReference
import kotlin.reflect.KClass

data class ViewModelCacheKey(
    val modelClass: KClass<*>,
    val extra: Any?,
) {
    val key: String
        get() = "${modelClass.qualifiedName}::${extra?.toString()}"
}

@Suppress("UNCHECKED_CAST")
class ViewModelFactoryCached(
    private val viewModelFactory: ViewModelFactory,
) : ViewModelFactory {

    internal data class ViewModelCacheWrapper(
        val viewModel: WeakReference<ViewModel>,
        var count: Int = 0,
    )

    private val cache: MutableMap<ViewModelCacheKey, ViewModelCacheWrapper> = mutableMapOf()

    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extra: Any?,
        onClearedCallback: (() -> Unit)?
    ): T {
        val key = ViewModelCacheKey(modelClass, extra)
        val cached = cache[key]
        if (cached != null) {
            Log.d("[ViewModelFactoryCached] Using cached ViewModel: $key")
            cached.viewModel.get()?.let {
                cached.count++
                return it as T
            }
        }

        // Otherwise, create the ViewModel using the ViewModelFactory
        return viewModelFactory.create(modelClass, extra, onClearedCallback = {
            Log.i("[ViewModelFactoryCached] Clearing ViewModel: $key")
            cache[key]?.let {
                it.count--
                if (it.count <= 0) {
                    Log.i("[ViewModelFactoryCached] Removing ViewModel from cache: $key")
                    cache.remove(key)
                }
            }
            onClearedCallback?.invoke()
        }).also {
            Log.i("[ViewModelFactoryCached] Caching ViewModel: $key")
            cache[key] = ViewModelCacheWrapper(WeakReference(it), count = 1)
        }
    }
}