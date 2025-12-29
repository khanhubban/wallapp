package wallapp.account.signin

enum class ChangeAccountState {
    Start,
    SyncedFromPreviousAccount,
    PreviousAccountDataDeleted,
    PreviousAccountDeleted,
}