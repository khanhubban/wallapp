package wallapp.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import wallapp.activity.MainActivity
import wallapp.bitmap.BitmapMapper.toBitmap
import wallapp.data.DataRepository
import wallapp.di.NamedScope
import wallapp.download.DownloadManager
import wallapp.download.DownloadState
import wallapp.log.Logger
import wallapp.network.NetworkConnectionState
import wallapp.network.NetworkState
import wallapp.resources.string.StringRepository

class MainFirebaseMessagingService : FirebaseMessagingService() {

    val Log = Logger("[FCM] MainFirebaseMessagingService")

    private val strings: StringRepository by inject()
    private val cloudMessagingManager: CloudMessagingManager by inject()
    private val downloadManager: DownloadManager by inject()
    private val coroutineScopeIo: CoroutineScope by inject(NamedScope.CoroutineScopeIo)
    private val dataRepository: DataRepository by inject()
    private val networkState: NetworkState by inject()

    override fun onCreate() {
        super.onCreate()
        Log.d("Service is instantiated")
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Message data payload: ${remoteMessage.data}")

            // Check if data needs to be processed by long running job
            if (isLongRunningJob()) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        val dataMap: MutableMap<String, String> = remoteMessage.data

        // Check if message contains a notification payload.
        remoteMessage.notification?.also {
            val title = it.title
            val body = it.body
            val imageUrl = it.imageUrl
            Log.d("Message Notification title: $title, body: $body, imageUrl: $imageUrl")
            if (title != null || body != null) {
                sendNotification(title, body, imageUrl, dataMap)
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    private fun isLongRunningJob() = true

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d("Refreshed token: $token")

        cloudMessagingManager.onNewToken(token)
    }

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        Log.w("TODO: Schedule async work using WorkManager")
//        val work = OneTimeWorkRequest.Builder(MainFirebaseMessagingWorker::class.java).build()
//        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d("Short lived task is done.")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(
        title: String?,
        body: String?,
        imageUrl: Uri?,
        dataMap: Map<String, String>,
    ) {
        Log.d("sendNotification($title, $body, $imageUrl, $dataMap)")

        val requestCode = 0
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Add each entry from dataMap as an extra
            for ((key, value) in dataMap) {
                putExtra(key, value)
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = strings.notificationContentUpdatesChannelId
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(wallapp.resources.R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            strings.notificationContentUpdatesChannelName,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = strings.notificationContentUpdatesChannelDescription
        }
        notificationManager.createNotificationChannel(channel)

        val notificationId = ((title ?: "") + (body ?: "") + (imageUrl ?: "")).hashCode()
        notificationManager.notify(notificationId, notificationBuilder.build())

        if(networkState.networkConnectionState.value == NetworkConnectionState.Connected) {
            imageUrl?.let {
                coroutineScopeIo.launch {
                    downloadImageAndUpdateNotification(imageUrl, notificationBuilder, notificationId, notificationManager)
                }
            }
        }
    }

    private suspend fun downloadImageAndUpdateNotification(
        imageUrl: Uri,
        notificationBuilder: NotificationCompat.Builder,
        notificationId: Int,
        notificationManager: NotificationManager,
    ) {
        downloadManager.downloadUrl(imageUrl.toString()).collectLatest { downloadState ->
            if (downloadState is DownloadState.Success) {
                dataRepository.getDataBlob(downloadState.dataHandle)?.byteArray?.let { byteArray ->
                    val bitmap = byteArray.toBitmap()

                    // Update the notification with the downloaded image
                    notificationBuilder.setLargeIcon(bitmap)
                    val bigPictureStyle = NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(bitmap)
                    notificationBuilder.setStyle(bigPictureStyle)

                    // Notify with the updated notification
                    notificationManager.notify(notificationId, notificationBuilder.build())
                }
            }
        }
    }
}
