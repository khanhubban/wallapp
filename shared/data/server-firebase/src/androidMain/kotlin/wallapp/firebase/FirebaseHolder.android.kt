package wallapp.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import wallapp.process.Process
import wallapp.process.requireIsDefault

class FirebaseHolderAndroid(
    val process: Process,
) : FirebaseHolder {
    override val firebaseAuth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (ex: Exception) {
            null
        }
    }

    override val firebaseFirestore: FirebaseFirestore? by lazy {
        process.requireIsDefault("FirebaseFirestore")
        try {
            FirebaseFirestore.getInstance()
        } catch (ex: Exception) {
            null
        }
    }
}