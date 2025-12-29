package wallapp.crashtracking

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CrashTrackingUserIdNoOp : CrashTrackingUserId {

    override val crashTrackingUserId: StateFlow<String?> = MutableStateFlow(null)
}