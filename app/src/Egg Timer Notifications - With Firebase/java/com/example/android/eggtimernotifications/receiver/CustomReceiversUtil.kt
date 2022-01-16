package com.example.android.eggtimernotifications.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.android.eggtimernotifications.BuildConfig
import com.example.android.eggtimernotifications.MainActivity
import com.example.android.eggtimernotifications.R

//Note this class is not part of the android tutorial
class CustomReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val intentAction = intent.action
        var toastMessage = "Unknown intent action"
        when (intentAction) {
            Intent.ACTION_BATTERY_CHANGED -> toastMessage = "New Battery percentage"
            Intent.ACTION_POWER_DISCONNECTED -> toastMessage = "Power Disconnected"
            Intent.ACTION_POWER_CONNECTED -> toastMessage = "Power Connected"
            ACTION_CUSTOM_EGG_BROADCAST -> toastMessage =
                "An Egg Custom Receiver::: $ACTION_CUSTOM_EGG_BROADCAST"
        }

        //Display a Toast
        Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val ACTION_CUSTOM_EGG_BROADCAST =
            BuildConfig.APPLICATION_ID + ".ACTION_EGG_CUSTOM_BROADCAST"
    }
}

class AlarmingReceiver : BroadcastReceiver() {
    val ACTION_UPDATE_NOTIFICATION =
        "${BuildConfig.APPLICATION_ID}.ACTION_UPDATE_NOTIFICATION"

    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private val NOTIFICATION_ID = 12
    private var notificationManager: NotificationManager? = null
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var context: Context? = null


    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context
        deliverNotification()

    }

    /**
     *  method that posts the reminder to stand up and walk.
     */
    private fun deliverNotification() {
        notificationManager = getNotificationManager()
        createNotificationChannel()
        notificationBuilder =
            getNotificationBuilder()
    }

    private fun getNotificationManager(): NotificationManager? {
        if (notificationManager == null) {
            return context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Alarm Notification", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "Notifies every 15 minutes to stand up and walk"
            }

            notificationManager?.createNotificationChannel(notificationChannel);
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder? {
        //intent is used to add responsiveness to the notifications when tapped
        val notificationIntent = Intent(context, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        //intent to add a Button to the notification. This intent contains the BroadcastReveiver constant
        val updateIntentBroadcast = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            updateIntentBroadcast,
            PendingIntent.FLAG_ONE_SHOT
        )
        //customise the notification builder
        val _notificationBuilder =
            context?.let {
                NotificationCompat.Builder(it, PRIMARY_CHANNEL_ID).apply {
                    setContentIntent(notificationPendingIntent)
                    setAutoCancel(true)
                    setContentTitle("You've been notified!")
                    setContentText("This is your notification text.")
                    setSmallIcon(R.drawable.cooked_egg)
                    /* Note: The high-priority notification will not drop down in front of the active screen unless both the priority and the defaults are set. Setting the priority alone is not enough. */
                    priority = NotificationCompat.PRIORITY_HIGH
                    setDefaults(NotificationCompat.DEFAULT_ALL)
                    //add the Button to the Builder
                    addAction(
                        R.drawable.egg_icon_plain,
                        "Update Notification",
                        updatePendingIntent
                    )
                }
            }
        return _notificationBuilder
    }
}