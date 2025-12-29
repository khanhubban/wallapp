package wallapp.crashtracking

import kotlinx.coroutines.flow.Flow
import wallapp.account.AccountManager
import wallapp.account.data.AccountDataRepository
import wallapp.util.combine

class CrashTrackingUserIdDefault(
    accountManager: AccountManager,
    accountDataRepository: AccountDataRepository,
) : CrashTrackingUserId {

    override val crashTrackingUserId: Flow<String?> = combine(
        accountDataRepository.reportUsageStats,
        accountManager.signedInOrAnonymousUserId,
        ) { reportUsageStats, userId,  ->
            if (reportUsageStats) userId else null
        }
}