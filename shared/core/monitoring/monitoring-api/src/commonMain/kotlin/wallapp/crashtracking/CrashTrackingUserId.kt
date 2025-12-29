package wallapp.crashtracking

import kotlinx.coroutines.flow.Flow

interface CrashTrackingUserId {

    val crashTrackingUserId: Flow<String?>
}