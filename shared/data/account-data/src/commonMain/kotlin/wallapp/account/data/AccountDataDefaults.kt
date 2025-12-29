package wallapp.account.data

interface AccountDataDefaults {

    val receiveNewsletter: Boolean
        get() = false

    val receiveNotifications: Boolean
        get() = false
}

object AccountDataDefaultsNoOp : AccountDataDefaults
