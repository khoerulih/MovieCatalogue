package com.example.moviecatalogue

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.moviecatalogue.entity.NotificationItems
import com.example.moviecatalogue.ui.ReleaseFragment
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    private var idNotification = 0
    private var totalIdNotification = 0

    private val stackNotif = ArrayList<NotificationItems>()

    companion object{
        const val TYPE_DAILY = "Daily Reminder"
        const val TYPE_RELEASE = "Release Reminder"
        const val EXTRA_TYPE = "type"


        private const val ID_DAILY = 100
        private const val ID_RELEASE = 101
        private const val API_KEY = "8472c517d8542b00b656d218b02ad968"
        private const val TIME_FORMAT = "HH:mm"

        private const val CHANNEL_NAME = "Reminder channel"
        private const val GROUP_KEY_REMINDER = "group_key_reminder"
        private const val NOTIFICATION_REQUEST_CODE = 200
        private const val MAX_NOTIFICATION = 4
    }

    val listMovie = MutableLiveData<ArrayList<NotificationItems>>()

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE)

        val title = if(type.equals(TYPE_DAILY, ignoreCase = true)) TYPE_DAILY else TYPE_RELEASE
        val notifId = if(type.equals(TYPE_DAILY, ignoreCase = true)) ID_DAILY else ID_RELEASE

        val date = getCurrentDate()

        if(type != TYPE_DAILY){
            setMovie(date, context)
        }else{
            showDailyNotification(context, title, notifId)
        }
    }

    fun setDailyReminder(context: Context, type: String, time: String){
        if(isDateInvalid(time, TIME_FORMAT)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray =  time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_DAILY, intent, 0)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)

        Toast.makeText(context, R.string.setup_daily, Toast.LENGTH_SHORT).show()
    }

    fun setReleaseReminder(context: Context, type: String, time: String){
        if(isDateInvalid(time, TIME_FORMAT)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray =  time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_RELEASE, intent, 0)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)

        Toast.makeText(context, R.string.setup_release, Toast.LENGTH_SHORT).show()
    }

    fun cancelReminder(context: Context, type: String){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = if(type.equals(TYPE_DAILY, ignoreCase = true)) ID_DAILY else ID_RELEASE
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)

        if(type == TYPE_DAILY){
            Toast.makeText(context, R.string.cancel_daily, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, R.string.cancel_release, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDailyNotification(context: Context, title: String, notifId: Int){
        val CHANNEL_ID = "Channel_1"
        val CHANNEL_NAME = "Daily Channel"

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_movie_white_24dp)
            .setContentTitle(title)
            .setContentText(context.resources.getString(R.string.daily_message))

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()

        mNotificationManager.notify(notifId, notification)
    }

    private fun showStackNotif(context: Context, message: String, notifId: Int){
        Log.d("Debug : ", "ShowStackNotif")
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_movie_white_24dp)
        val intent = Intent(context, ReleaseFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mBuilder: NotificationCompat.Builder

        val CHANNEL_ID = "channel_02"
        if(idNotification < MAX_NOTIFICATION){
            mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("${stackNotif[idNotification].title}")
                .setContentText(message +" "+ context.resources.getString(R.string.released_today))
                .setSmallIcon(R.drawable.ic_movie_white_24dp)
                .setLargeIcon(largeIcon)
                .setGroup(GROUP_KEY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }else {
            val inboxStyle = NotificationCompat.InboxStyle()
                .addLine("${stackNotif[idNotification].title} " + context.resources.getString(R.string.released_today))
                .addLine("${stackNotif[idNotification-1].title} " + context.resources.getString(R.string.released_today))
                .addLine("${stackNotif[idNotification-2].title} " + context.resources.getString(R.string.released_today))
                .addLine("${stackNotif[idNotification-3].title} " + context.resources.getString(R.string.released_today))
                .setBigContentTitle(context.resources.getString(R.string.release_message))
                .setSummaryText("$idNotification " + context.resources.getString(R.string.new_notification))
            mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.resources.getString(R.string.release_message))
                .setContentText("${idNotification + 1} "+ context.resources.getString(R.string.movie_released_today))
                .setSmallIcon(R.drawable.ic_movie_white_24dp)
                .setGroup(GROUP_KEY_REMINDER)
                .setGroupSummary(true)
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle)
                .setAutoCancel(true)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            /* Create or update. */
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

            mBuilder.setChannelId(CHANNEL_ID)

            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()

        mNotificationManager.notify(notifId, notification)
    }

    private fun isDateInvalid(date: String, format: String): Boolean{
        return try{
            val df = SimpleDateFormat(format, Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        } catch (e: ParseException){
            true
        }
    }

    private fun setMovie(date: String, context: Context){
        val client = AsyncHttpClient()

        val url = "https://api.themoviedb.org/3/discover/movie?api_key=$API_KEY&primary_release_date.gte=$date&primary_release_date.lte=$date"

        client.get(url, object: AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                try{
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val list = responseObject.getJSONArray("results")
                    for(i in 0 until list.length()){
                        val movie = list.getJSONObject(i)
                        val movieItems = NotificationItems()
                        movieItems.title = movie.getString("title")
                        stackNotif.add(movieItems)
                        Log.d("Test_Add", "${stackNotif[idNotification].title}")
                        showStackNotif(context,  "${stackNotif[idNotification].title}", 2)
                        idNotification++
                    }
                    totalIdNotification = idNotification
                    listMovie.postValue(stackNotif)
                }catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }

    private fun getCurrentDate(): String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

}
