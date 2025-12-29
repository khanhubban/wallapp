package wallapp.userprofile

/**
 * Example Json to copy to the Firebase Console in the "flags" field:
 * """{"key":"specialCaseIsDeveloper", "value": "false"}"""
 * https://console.firebase.google.com/project/obra-app/firestore/databases/-default-/data/~2Fusers~2F...
 */
object UserProfileFlags {

    object Flag {
        const val SpecialCasePlusEntitlement = "specialCasePlusEntitlement"
        const val SpecialCaseIsDeveloper = "specialCaseIsDeveloper"
    }

    fun hasSpecialCasePlusEntitlement(flags: List<UserProfileFlag>): Boolean {
        return flags.any { hasSpecialCasePlusEntitlement(it) }
    }

    fun hasSpecialCasePlusEntitlement(userProfileFlag: UserProfileFlag): Boolean {
        return userProfileFlag.key == Flag.SpecialCasePlusEntitlement
                && (userProfileFlag.value == "true" || userProfileFlag.value == "1")
    }

    fun hasSpecialCaseIsDeveloper(flags: List<UserProfileFlag>): Boolean {
        return flags.any { hasSpecialCaseIsDeveloper(it) }
    }

    fun hasSpecialCaseIsDeveloper(userProfileFlag: UserProfileFlag): Boolean {
        return userProfileFlag.key == Flag.SpecialCaseIsDeveloper
                && (userProfileFlag.value == "true" || userProfileFlag.value == "1")
    }

}