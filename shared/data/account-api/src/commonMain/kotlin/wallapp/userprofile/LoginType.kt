package wallapp.userprofile

import wallapp.signin.SignInProvider

enum class LoginType(val label: String) {
    Apple("firebase/apple"),
    Google("firebase/google"),
    Anonymous("firebase/anonymous"),
    Email("firebase/email"), // only for testing
    None("none"), // for deleted users
}

fun String.toLoginType(): LoginType? =
    when (this) {
        LoginType.Apple.label -> LoginType.Apple
        LoginType.Google.label -> LoginType.Google
        LoginType.Anonymous.label -> LoginType.Anonymous
        LoginType.Email.label -> LoginType.Email
        LoginType.None.label -> LoginType.None
        else -> null
    }

fun LoginType.toSignInProvider(): SignInProvider? =
    when (this) {
        LoginType.Apple -> SignInProvider.Apple
        LoginType.Google -> SignInProvider.Google
        LoginType.Anonymous -> null
        LoginType.Email -> SignInProvider.Email
        LoginType.None -> null
    }