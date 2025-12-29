package wallapp.initializer.module

import wallapp.log.Log

data class ModuleInitializers(
    private val initializers: List<ModuleInitializer>,
) : ModuleInitializer {

    override fun initialize() {
        Log.d("ModuleInitializers initialize() start")
        initializers.forEach {
            it.initialize()
        }
        Log.d("ModuleInitializers initialize() end")
    }
}
