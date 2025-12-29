package wallapp.coroutine

import kotlinx.coroutines.CoroutineScope

data class CoroutineScopes(
    val main: CoroutineScope,
    val mainImmediate: CoroutineScope,
    val io: CoroutineScope,
    val prefetch: CoroutineScope,
) {
    constructor(
        coroutineContexts: CoroutineContexts,
    ) : this(
        main = CoroutineScope(coroutineContexts.main),
        mainImmediate = CoroutineScope(coroutineContexts.mainImmediate),
        io = CoroutineScope(coroutineContexts.io),
        prefetch = CoroutineScope(coroutineContexts.prefetch),
    )
}

fun CoroutineScopesPreset(coroutineScope: CoroutineScope): CoroutineScopes {
    return CoroutineScopes(
        main = coroutineScope,
        mainImmediate = coroutineScope,
        io = coroutineScope,
        prefetch = coroutineScope,
    )
}