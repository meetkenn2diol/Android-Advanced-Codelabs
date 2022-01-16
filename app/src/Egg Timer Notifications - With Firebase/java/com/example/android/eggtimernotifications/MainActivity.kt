/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.eggtimernotifications


import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android.eggtimernotifications.receiver.AlarmingReceiver
import com.example.android.eggtimernotifications.receiver.CustomReceiver
import com.example.android.eggtimernotifications.ui.EggTimerFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EggTimerFragment.newInstance())
                .commitNow()
        }

        //region Below i am testing new knowledge
        alarmTutorial()
        notificationTutorial()
        //endregion
    }

    //region ALARM TUTORIAL FROM ANDROID_JAVA

    /**
     *  To guarantee that alarms execute, you can use setAndAllowWhileIdle() or setExactAndAllowWhileIdle(). You can also use the new WorkManager API, which is built to perform background work either once or periodically. For details, see Schedule tasks with WorkManager.
     */
    private fun alarmTutorial() {
        val alarmingIntent = Intent(this, AlarmingReceiver::class.java)
        val notifyPendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            alarmingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Check the state of the alarm
        val alarmUp: Boolean = PendingIntent.getBroadcast(
            this, NOTIFICATION_ID, alarmingIntent,
            PendingIntent.FLAG_NO_CREATE
        ) != null


        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmSwitch = findViewById<SwitchCompat>(R.id.alarm_toggle)
        //ensure the alarm is not turned off when you restart the app
        alarmSwitch.isChecked = alarmUp

        alarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            var toastMessage = ""
            when (isChecked) {
                true -> {
                    buttonView.text = resources.getString(R.string.alarm_on)
                    toastMessage = "Stand Up Alarm On!"


                    val repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES
                    val triggerTime = (SystemClock.elapsedRealtime()
                            )

//If the Toggle is turned on, set the repeating alarm with a 15 minute interval

//If the Toggle is turned on, set the repeating alarm with a 15 minute interval
                    alarmManager.setInexactRepeating(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime, repeatInterval, notifyPendingIntent
                    )
                }
                false -> {
                    buttonView.text = resources.getString(R.string.alarm_off)
                    toastMessage = "Stand Up Alarm Off!"
                    notificationManager?.cancelAll()
                    alarmManager.cancel(notifyPendingIntent)
                }
            }
            //Show a toast to say the alarm is turned on or off.
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT)
                .show();
        }

    }

    private fun deliverNotification(context: Context) {
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder.setContentIntent(contentPendingIntent)
        //make the notification
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
//endregion

    //region NOTIFICATION TUTORIAL FROM ANDROID_JAVA
    val ACTION_UPDATE_NOTIFICATION =
        "${BuildConfig.APPLICATION_ID}.ACTION_UPDATE_NOTIFICATION"

    private val customReceiver = CustomReceiver()
    private val notificationReceiver = NotificationReceiver()
    private val alarmingReceiver = AlarmingReceiver()
    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private val NOTIFICATION_ID = 12
    private var notificationManager: NotificationManager? = null
    private lateinit var notificationBuilder: NotificationCompat.Builder


    private fun notificationTutorial() {
        //Intent filter for a broadcast Receiver
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        //register the receiver dynamically to the Android system
        registerReceiver(customReceiver, filter)
        //register a local broadcast receiver
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                customReceiver,
                IntentFilter(CustomReceiver.ACTION_CUSTOM_EGG_BROADCAST)
            )
        //register notification Receiver::Don't use LoacalBroadcastReceiver because your notification uses PendingIntent
        registerReceiver(
            notificationReceiver,
            IntentFilter(ACTION_UPDATE_NOTIFICATION)
        )

        //register the Spinner clickListener in activity_main
        notificationOptionSelected()
    }


    fun sendCustomBroadcast(view: View) {
        val customBroadcastIntent = Intent(CustomReceiver.ACTION_CUSTOM_EGG_BROADCAST)
        LocalBroadcastManager.getInstance(this).sendBroadcast(customBroadcastIntent)
    }

    override fun onDestroy() {
        //unregister customReceiver
        unregisterReceiver(customReceiver)
        //unregister NotificationReciever
        unregisterReceiver(notificationReceiver)
        //unregister Local receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(customReceiver)
        super.onDestroy()
    }

    /**
     * A method used to set ClickListener for the Spinner Widget For Notification
     */
    private fun notificationOptionSelected() {
        val spinner = findViewById<Spinner>(R.id.notification_spinner)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //note: onItemSelectedListener will only respond if a new item is selected. if its the same item clicked, nothing  happens
                when (position) {
                    1 -> notifyMe()
                    2 -> updateMe()
                    3 -> cancelMe()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@MainActivity, "No item Selected", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun notifyMe() {
        Toast.makeText(this@MainActivity, "Sending Notification...", Toast.LENGTH_LONG).show()
        notificationManager = getNotificationManager()
        createNotificationChannel()
        notificationBuilder = getNotificationBuilder()
        //make the notification
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getNotificationManager(): NotificationManager? {
        if (notificationManager == null) {
            return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }

    private fun createNotificationChannel(descriptionMessage: String = "Notification from Mascot") {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Mascot Notification", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = descriptionMessage
            }

            notificationManager?.createNotificationChannel(notificationChannel);
        }
    }

    private fun getNotificationBuilder(
        title: String = "You've been notified!",
        text: String = "This is your notification text."
    ): NotificationCompat.Builder {
        //intent is used to add responsiveness to the notifications when tapped
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        //intent to add a Button to the notification. This intent contains the BroadcastReveiver constant
        val updateIntentBroadcast = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            updateIntentBroadcast,
            PendingIntent.FLAG_ONE_SHOT
        )
        //customise the notification builder
        val _notificationBuilder = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID).apply {
            setContentIntent(notificationPendingIntent)
            setAutoCancel(true)
            setContentTitle(title)
            setContentText(text)
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
        return _notificationBuilder
    }

    private fun updateMe() {
        Toast.makeText(this, "Updating Notification...", Toast.LENGTH_LONG).show()
        val androidImage = BitmapFactory
            .decodeResource(resources, R.drawable.egg_notification)
        //you need to get this builder again because updateMe() method can be called from another class
        notificationBuilder = getNotificationBuilder().setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification has been Updated!!")
        )
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun updateNotification() {
        updateMe()
    }

    private fun cancelMe() {
        Toast.makeText(this@MainActivity, "Notification Cancelled", Toast.LENGTH_LONG).show()
        notificationManager?.cancel(NOTIFICATION_ID);
    }


    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Update the notification
            if (intent.action == ACTION_UPDATE_NOTIFICATION) {
                updateNotification()
            }
        }
    }
    //endregion
}