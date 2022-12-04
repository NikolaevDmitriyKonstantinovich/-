package org.hyperskill.stopwatch

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class MainActivity : AppCompatActivity() {

    val CHANNEL_ID = "org.hyperskill"
//    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
var notificationManager: NotificationManager? = null
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "desc"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.createNotificationChannel(channel)
        }
    }




    var builder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("wha")
        .setContentText("yusc dsmc")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)


    fun myNotify() {
        val notification = builder.build()
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE or Notification.FLAG_INSISTENT
//        with(NotificationManagerCompat.from(this)) {
//            // notificationId is a unique int for each notification that you must define
//            notify(393939, notification)
//        }
        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.notify(393939, notification)
    }

    //-----------------------------------------------------------------------

    var proBar: ProgressBar? = null
    private var limit = ""
    private val handler = Handler(Looper.getMainLooper())
    private var time = 0
        set(value) {
            field = value
            val min = value / 60
            val sec = value % 60
            findViewById<TextView>(R.id.textView).text = "${min.padTime()}:${sec.padTime()}"
        }
    var check = true

    private val increaseTime = object : Runnable {
        override fun run() {

            ++time
            if((!limit.equals("")) && limit.toInt() > 0 && time == limit.toInt()) {
                createNotificationChannel()
                myNotify()
            }
            if(limit.matches("\\d".toRegex()) && time == limit.toInt()) {
                findViewById<TextView>(R.id.textView).setTextColor(Color.RED)
            }
            if(check) {
                proBar?.indeterminateTintList = ColorStateList.valueOf(Color.RED)
                check = false
            } else {
                proBar?.indeterminateTintList = ColorStateList.valueOf(Color.GREEN)
                check = true
            }
            handler.postDelayed(this, 1000)
        }
    }
    private val change = object : Runnable {
        override fun run() {
            proBar?.indeterminateTintList = ColorStateList.valueOf(Color.RED)
            handler.postDelayed(this, 1000)
        }
    }

    private val change2 = object : Runnable {
        override fun run() {
            proBar?.indeterminateTintList = ColorStateList.valueOf(Color.GREEN)
            handler.postDelayed(this, 1000)
        }
    }

    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        proBar = findViewById<ProgressBar>(R.id.progressBar)
        proBar?.visibility = View.INVISIBLE
        findViewById<Button>(R.id.startButton).setOnClickListener { start() }
        findViewById<Button>(R.id.resetButton).setOnClickListener { reset() }
        findViewById<Button>(R.id.settingsButton).setOnClickListener { createDialog() }
    }

    private fun start() {
//        if((!limit.equals("")) && limit.toInt() > 0 && time == limit.toInt()) {
//            createNotificationChannel()
//            myNotify()
//        }
        findViewById<Button>(R.id.settingsButton).isEnabled = false
        findViewById<Button>(R.id.settingsButton).isClickable = false
        proBar?.visibility = View.VISIBLE
        if (isRunning) return
        isRunning = true
        handler.postDelayed(increaseTime, 1000)

//        proBar?.indeterminateTintList = ColorStateList.valueOf(Color.RED)
    }

    private fun reset() {
        notificationManager?.cancel(393939)
        findViewById<TextView>(R.id.textView).setTextColor(Color.GRAY)
        findViewById<Button>(R.id.settingsButton).isEnabled = true
        findViewById<Button>(R.id.settingsButton).isClickable = true
        proBar?.visibility = View.INVISIBLE
        isRunning = false
        time = 0
        handler.removeCallbacks(increaseTime)
    }

    private fun Int.padTime() = this.toString().padStart(2, '0')

    private fun createDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set upper limit in seconds")
        val editText = EditText(this)
        editText.id = R.id.upperLimitEditText
        builder.setView(editText)
        builder.setPositiveButton("OK") { dialog,i ->
            limit = editText.text.toString()
        }

        builder.setNegativeButton("Cancel") { dialog,i ->

        }
        builder.show()
    }
}