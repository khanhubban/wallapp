package wallapp.interop

//class InteropModulesAndroid(
//    koinApplication: KoinApplication,
//) : InteropModulesBase(koinApplication) {
//
//    override val appViewModel: AppViewModel
//        get() = TODO("Android impl handled differently on Android")
//
//    override val windowFrameManager: WindowFrameManager
//        get() = windowFrameManagerDefault
//    val windowFrameManagerDefault: WindowFrameManagerDefault
//        = koin.get<WindowFrameManager>() as WindowFrameManagerDefault
//}
//
//lateinit var InteropModules: InteropModulesAndroid
//
//actual fun InteropModules(koinApplication: KoinApplication): InteropModulesBase =
//    InteropModulesAndroid(koinApplication).also {
//        InteropModules = it
//    }