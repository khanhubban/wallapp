package wallapp.di.module

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import org.koin.dsl.module
import wallapp.account.data.AccountDataManagerDefault
import wallapp.account.google.GoogleSignInFactory
import wallapp.data.favorite.AccountDataRepositoryFirebaseMobile
import wallapp.di.NamedScope
import wallapp.firebase.FirebaseHolder
import wallapp.firebase.FirebaseHolderAndroid
import wallapp.messaging.CloudMessagingManagerAndroid
import wallapp.profileimage.ProfileImageManagerAndroid
import wallapp.userprofile.UserProfileRepositoryFirebase

@Suppress("RemoveExplicitTypeArguments")
val AccountPlatformModule = module {
    single<AccountDataManagerDefault> { AccountDataManagerDefault(get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<AccountDataRepositoryFirebaseMobile> { AccountDataRepositoryFirebaseMobile(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<CloudMessagingManagerAndroid> { CloudMessagingManagerAndroid(get()) }
    single<FirebaseHolder> { get<FirebaseHolderAndroid>() }
    single<FirebaseHolderAndroid> { FirebaseHolderAndroid(get()) }
    single<GoogleSignInClient> { GoogleSignInFactory.createGoogleSignInClient(get()) }
    single<ProfileImageManagerAndroid> { ProfileImageManagerAndroid(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<UserProfileRepositoryFirebase> { UserProfileRepositoryFirebase(get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
}