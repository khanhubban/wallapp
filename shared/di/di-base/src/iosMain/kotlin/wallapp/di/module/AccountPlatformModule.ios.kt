package wallapp.di.module

import org.koin.dsl.module
import wallapp.account.data.AccountDataManagerDefault
import wallapp.auth.google.GoogleAuthCoordinatorForIos
import wallapp.auth.google.GoogleAuthManagerIos
import wallapp.data.favorite.AccountDataRepositoryFirebaseMobile
import wallapp.di.FactoryIos
import wallapp.di.NamedScope
import wallapp.messaging.CloudMessagingManagerIos
import wallapp.profileimage.ProfileImageManagerIos
import wallapp.userprofile.UserProfileRepositoryFirebase

@Suppress("RemoveExplicitTypeArguments")
val AccountPlatformModule = module {
    single<AccountDataManagerDefault> { AccountDataManagerDefault(get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<AccountDataRepositoryFirebaseMobile> { AccountDataRepositoryFirebaseMobile(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<CloudMessagingManagerIos> { CloudMessagingManagerIos(get()) }
    single<GoogleAuthCoordinatorForIos> { FactoryIos.googleAuthCoordinatorForIos() }
    single<GoogleAuthManagerIos> { GoogleAuthManagerIos(get()) }
    single<ProfileImageManagerIos> { ProfileImageManagerIos(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<UserProfileRepositoryFirebase> { UserProfileRepositoryFirebase(get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
}