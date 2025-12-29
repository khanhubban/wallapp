package wallapp.firebase.firestore

import com.google.firebase.FirebaseApp
import com.google.firebase.cloud.FirestoreClient

class FirestoreManager(
    private val firebaseApp: FirebaseApp = initializeFirebase()
) {
    val firestore by lazy {
        FirestoreClient.getFirestore(firebaseApp)
    }
}