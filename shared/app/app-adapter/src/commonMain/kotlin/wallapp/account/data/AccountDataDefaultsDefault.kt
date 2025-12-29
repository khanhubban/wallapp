package wallapp.account.data

import wallapp.prefs.PreferenceDefaults

class AccountDataDefaultsDefault(
    private val preferenceDefaults: PreferenceDefaults,
) : AccountDataDefaults {

    override val receiveNewsletter: Boolean
        get() = preferenceDefaults.joinNewsletter.default()

    override val receiveNotifications: Boolean
        get() = preferenceDefaults.receiveNotifications.default()
}