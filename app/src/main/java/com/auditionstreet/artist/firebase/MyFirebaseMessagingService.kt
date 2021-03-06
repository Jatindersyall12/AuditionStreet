package com.auditionstreet.artist.firebase

import android.util.Log
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.utils.AppConstants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var preferences: Preferences

    override fun onNewToken(token: String) {
        Log.e("tokenn", "tokenn" + token)
        preferences.setString(AppConstants.FIREBASE_ID, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("message received", "message received")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Df", "Message data payload: ${remoteMessage.data}")
        }
        remoteMessage.notification?.let {
            Log.d("SDs", "Message Notification Body: ${it.body}")
        }
    }

}