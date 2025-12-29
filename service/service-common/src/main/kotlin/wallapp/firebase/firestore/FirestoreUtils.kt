package wallapp.firebase.firestore

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.cloud.firestore.QuerySnapshot
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import wallapp.string.quote
import java.io.File
import java.io.PrintWriter
import java.util.concurrent.ExecutionException

fun convertEpochToDateTime(epochMillis: Long, timeZone: String = "UTC"): String {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val zone = TimeZone.of(timeZone)
    val localDateTime = instant.toLocalDateTime(zone)
    return "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')} " +
            "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}:${localDateTime.second.toString().padStart(2, '0')}"
}

fun Firestore.iterateUsers(
    maxBatchesToExport: Int? = null,
    batchSize: Int = 1000,
    handleDocument: (FirestoreUserSnapshot) -> Unit,
) {
    val usersCollection = collection("users")
    var lastDocument: QueryDocumentSnapshot? = null
    var batchesProcessed = 0
    var userCount = 0

    try {
        do {
            // Fetch a batch of documents, using pagination (startAfter last document)
            val query = usersCollection
                .orderBy("__name__")
                .limit(batchSize)
            val querySnapshot: QuerySnapshot = if (lastDocument != null) {
                query.startAfter(lastDocument).get().get()  // Continue after last document
            } else {
                query.get().get()
            }

            if (querySnapshot.isEmpty) {
                println("No more documents to process")
                break
            }

            for (document: QueryDocumentSnapshot in querySnapshot) {
                val userSnapshot = FirestoreUserSnapshot(document)
                handleDocument(userSnapshot)
                userCount++
            }

            lastDocument = querySnapshot.documents.last()  // Get the last document of the batch
            batchesProcessed++
            println("Processed $userCount users so far (batch $batchesProcessed)...")

            // Stop if the max batch limit is reached
            if (maxBatchesToExport != null && batchesProcessed >= maxBatchesToExport) {
                println("Reached the maximum number of batches: $maxBatchesToExport")
                break
            }
        } while (querySnapshot.size() == batchSize)
            println("Processed $userCount users")
    } catch (e: InterruptedException) {
        e.printStackTrace()
    } catch (e: ExecutionException) {
        e.printStackTrace()
    }
}


fun Firestore.iterateUsersCollectionToCsv(
    outputFilename: String,
    maxBatchesToExport: Int? = null,
) {
    println("Exporting users in batches to $outputFilename...")

    val file = File(outputFilename)
    var userCount = 0
    var newsletterUserCount = 0
    var userEmailCount = 0

    try {
        PrintWriter(file).use { writer ->
            // Write CSV header
            writer.println(
                "userId,email,loginType,isAnonymous,accountDeleted,newsletter,receiveNotifications," +
                        "languages,flags,epochCreated,epochLastSeen,epochLastUpdated," +
                        "deviceInfo,timezones,currency,locations,wallpaperDownloadEvents,favoriteIds," +
                        "purchaseRecords,followingIds"
            )

            // Call iterateUsers() to handle document processing
            iterateUsers(maxBatchesToExport) { userSnapshot ->
                val email = userSnapshot.email
                if (email != null) {
                    userEmailCount++
                    if (userSnapshot.newsletter == true) {
                        newsletterUserCount++
                    }
                }

                // Write the data to the CSV in the correct order
                writer.println(
                    listOf(
                        userSnapshot.userId.quote(),
                        userSnapshot.email.quote(),
                        userSnapshot.loginType.quote(),
                        userSnapshot.isAnonymous,
                        userSnapshot.accountDeleted,
                        userSnapshot.newsletter,
                        userSnapshot.receiveNotifications,
                        userSnapshot.languages?.joinToString(separator = "|").quote(),
                        userSnapshot.flags,
                        userSnapshot.epochCreated,
                        userSnapshot.epochLastSeen,
                        userSnapshot.epochLastUpdated,
                        userSnapshot.deviceInfo.quote(),
                        userSnapshot.timezones?.joinToString(separator = "|").quote(),
                        userSnapshot.currency.quote(),
                        userSnapshot.locations?.joinToString(separator = "|").quote(),
                        userSnapshot.wallpaperDownloadEvents?.joinToString(separator = "|").quote(),
                        userSnapshot.favoriteIds?.joinToString(separator = "|").quote(),
                        userSnapshot.purchaseRecords?.joinToString(separator = "|").quote(),
                        userSnapshot.followingIds?.joinToString(separator = "|").quote(),
                    ).joinToString(separator = ",")
                )

                userCount++
            }

            println("Export completed with $userCount users to $outputFilename, $newsletterUserCount users to receive with newsletter, $userEmailCount user email addresses")
        }
    } catch (e: InterruptedException) {
        e.printStackTrace()
    } catch (e: ExecutionException) {
        e.printStackTrace()
    }
}

