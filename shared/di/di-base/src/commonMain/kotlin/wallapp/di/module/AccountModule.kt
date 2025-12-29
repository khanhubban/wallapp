package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.account.AccountManager
import wallapp.account.AccountManagerDefault
import wallapp.account.data.AccountDataManager
import wallapp.account.state.UserProfileErrorHandler
import wallapp.auth.firebase.FirebaseAuthManager
import wallapp.auth.google.GoogleAuthManager
import wallapp.auth.google.GoogleAuthManagerNoOp
import wallapp.di.Factory
import wallapp.di.NamedScope
import wallapp.messaging.CloudMessagingManager
import wallapp.profileimage.ProfileImageManager
import wallapp.profileimage.ProfileImageManagerDefault
import wallapp.signin.SignInProviderController
import wallapp.signin.SignInProviderControllerDefault
import wallapp.userprofile.UserProfileRepository

@Suppress("RemoveExplicitTypeArguments")
val AccountModule: Module = module {
    single<AccountDataManager> { Factory.accountDataManager(this) }
    single<AccountManager> { get<AccountManagerDefault>() }
    single<AccountManagerDefault> { AccountManagerDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<CloudMessagingManager> { Factory.cloudMessagingManager(this) }
    single<FirebaseAuthManager> { Factory.firebaseAuthManager(this) }
    single<GoogleAuthManager> { Factory.googleAuthManager(this) }
    single<GoogleAuthManagerNoOp> { GoogleAuthManagerNoOp }
    single<ProfileImageManager> { Factory.profileImageManager(this) }
    single<ProfileImageManagerDefault> { ProfileImageManagerDefault(get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<SignInProviderController> { SignInProviderControllerDefault(get(), get(NamedScope.CoroutineScopeIo)) }
    single<UserProfileErrorHandler> { UserProfileErrorHandler(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<UserProfileRepository> { Factory.userProfileRepository(this) }
}
