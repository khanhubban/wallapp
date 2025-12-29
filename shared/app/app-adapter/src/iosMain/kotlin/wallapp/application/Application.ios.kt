package wallapp.application

import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import wallapp.account.data.AccountDataRepository
import wallapp.data.content.ContentRepository
import wallapp.data.favorite.FavoriteItemsRepository
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.di.NamedScope
import wallapp.initializer.module.ModuleInitializers
import wallapp.preferences.UserPreferences
import wallapp.time.TimeRepository
import wallapp.viewmodel.ViewModelFactory
import wallapp.viewmodel.ViewModelProviderFactory


@Suppress("unused")
class ApplicationIos : Application, KoinComponent {

    private val moduleInitializers: ModuleInitializers by inject()
    private val coroutineScopeIo: CoroutineScope by inject(NamedScope.CoroutineScopeIo)
    private val coroutineScopeMain: CoroutineScope by inject(NamedScope.CoroutineScopeMain)
    private val accountDataRepository: AccountDataRepository by inject()
    private val contentRepository: ContentRepository by inject()
    private val favoriteItemsRepository: FavoriteItemsRepository by inject()
    private val timeRepository: TimeRepository by inject()
    private val userPreferences: UserPreferences by inject()
    private val viewModelFactory: ViewModelFactory by inject()
    private val viewModelProviderFactory: ViewModelProviderFactory by inject()
    private val wallpaperRepository: WallpaperRepository by inject()

    init {
//        FIRApp.configure()
        moduleInitializers.initialize()

//        runBlocking {
//            println("Running test testEncryption() start")
//            val encryptionManager = resolveDependency<EncryptionManager>()
//            encryptionManager.testDecryptionMultiplatform()
//        }
    }
}
