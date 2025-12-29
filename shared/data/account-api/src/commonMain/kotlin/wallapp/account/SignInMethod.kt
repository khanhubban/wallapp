package wallapp.account

import co.touchlab.skie.configuration.annotations.EnumInterop
import wallapp.userprofile.LoginType

@EnumInterop.Enabled
enum class SignInMethod {
    Apple,
    Google,
    Anonymous,
    Email, // only for testing
}

fun SignInMethod.toLoginType(): LoginType {
    return when (this) {
        SignInMethod.Apple -> LoginType.Apple
        SignInMethod.Google -> LoginType.Google
        SignInMethod.Anonymous -> LoginType.Anonymous
        SignInMethod.Email -> LoginType.Email
    }
}