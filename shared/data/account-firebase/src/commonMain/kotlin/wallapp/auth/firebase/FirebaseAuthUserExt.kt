package wallapp.auth.firebase

import dev.gitlive.firebase.auth.FirebaseUser
import wallapp.string.takeIfNotEmpty

val FirebaseUser.firebaseAuthUser: FirebaseAuthUser
    get() = FirebaseAuthUser(
        userId = uid,
        email = email.takeIfNotEmpty(),
        displayName = displayName.takeIfNotEmpty(),
        photoUrl = photoURL.takeIfNotEmpty(),
        isAnonymous = isAnonymous,
        providerId = providerId,
    )
