package wallapp.firebase.firestore

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import wallapp.service.Constant.AppFirebasePath
import wallapp.service.EnvironmentVariables
import wallapp.string.quote
import java.io.FileInputStream
import java.io.FileNotFoundException

private var firebaseApp: FirebaseApp? = null
fun initializeFirebase(databaseUrl: String = AppFirebasePath): FirebaseApp {
    if (firebaseApp != null) {
        return firebaseApp!!
    }

    val filePath = System.getenv(EnvironmentVariables.FirebaseAdminSdkServiceJsonFilePath)
        ?: throw RuntimeException("Error: Firebase Admin SDK JSON file path not set - please set the environment variable: ${EnvironmentVariables.FirebaseAdminSdkServiceJsonFilePath.quote()}")

    println("Using path: ${filePath.quote()}")
    val refreshToken = try {
         FileInputStream(filePath)
    } catch (ex: FileNotFoundException) {
        throw RuntimeException("Error: Firebase Admin SDK JSON file not found at path: ${filePath.quote()}")
    }

    val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(refreshToken))
        .setDatabaseUrl(databaseUrl)
        .build()

    return FirebaseApp.initializeApp(options)!!.also {
        firebaseApp = it
    }
}
