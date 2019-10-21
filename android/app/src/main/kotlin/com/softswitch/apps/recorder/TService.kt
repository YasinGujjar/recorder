package com.softswitch.apps.recorder

import android.annotation.TargetApi
import android.app.*
import android.app.Notification.PRIORITY_MIN
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import java.io.File
import java.io.IOException
import java.util.*

class TService : Service() {

    private var recorder: MediaRecorder? = null
    private var audiofile: File? = null
    private var recordstarted = false


    override fun onBind(arg0: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

    override fun onDestroy() {
        Log.d("service", "destroy")

        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("StartService", "TService")
        val filter = IntentFilter()
        filter.addAction(ACTION_OUT)
        filter.addAction(ACTION_IN)
        this.registerReceiver(CallReceiver(), filter)
        //        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //            Intent notificationIntent = new Intent(this, MainActivity.class);
        //            PendingIntent pendingIntent =  PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //            // return super.onStartCommand(intent, flags, startId);
        //            Notification notification = new Notification.Builder(this, Notification.CATEGORY_ALARM)
        //                    .setContentTitle("Recording Application")
        //                    .setContentText("My application")
        //                    .setSmallIcon(R.drawable.ic_android)
        //                    .setContentIntent(pendingIntent)
        //                    .build();
        //
        //            startForeground(1, notification);
        //          return START_STICKY;
        //        }


        return Service.START_STICKY

    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        //startForeground()
        var notification = createNotification()
        startForeground(1, notification)


    }

//    @TargetApi(Build.VERSION_CODES.ECLAIR)
//    private fun startForeground() {
//
//        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channelId =
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    createNotificationChannel()
//                } else {
//                    // If earlier version channel ID is not used
//                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
//                    ""
//                }
//
//        val notificationBuilder =  NotificationCompat.Builder(this, channelId )
//        val notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.ic_android)
//                .setPriority(PRIORITY_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .build()
//        startForeground(101, notification)
//    }
//
//    @TargetApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(): String{
//        val channelId = "my_service"
//        val channelName = "My Background Service"
//        val chan = NotificationChannel(channelId,
//                channelName, NotificationManager.IMPORTANCE_HIGH)
//        chan.lightColor = Color.BLUE
//        chan.importance = NotificationManager.IMPORTANCE_NONE
//        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
//        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        service.createNotificationChannel(chan)
//        return channelId
//    }














    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val notificationChannelId = "ENDLESS SERVICE CHANNEL"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
            val channel = NotificationChannel(
                    notificationChannelId,
                    "Endless Service notifications channel",
                    NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Endless Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this,
                notificationChannelId
        ) else Notification.Builder(this)

        return builder
                .setContentTitle("Endless Service")
                .setContentText("This is your favorite endless service working")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_android)
                .setTicker("Ticker text")
                .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
                .build()
    }









    private fun startRecording() {
        val sampleDir = File(Environment.getExternalStorageDirectory(), "/FlutterRecorder")
        if (!sampleDir.exists()) {
            sampleDir.mkdirs()
        }
        val file_name = "Record"
        try {
            audiofile = File.createTempFile(file_name, ".amr", sampleDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val path = Environment.getExternalStorageDirectory().absolutePath

        recorder = MediaRecorder()
        // recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

        recorder!!.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder!!.setOutputFile(audiofile!!.absolutePath)
        try {
            recorder!!.prepare()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        recorder!!.start()
        recordstarted = true
    }

    private fun stopRecording() {
        if (recordstarted) {
            recorder!!.stop()
            recordstarted = false
        }
    }


    abstract inner class PhonecallReceiver : BroadcastReceiver() {

        //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var callStartTime: Date? = null
        private var isIncoming: Boolean = false
        private var savedNumber: String? = null  //because the passed incoming is only valid in ringing


        override fun onReceive(context: Context, intent: Intent) {
            //        startRecording();
            //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
            if (intent.action == "android.intent.action.NEW_OUTGOING_CALL") {
                savedNumber = intent.extras!!.getString("android.intent.extra.PHONE_NUMBER")
            } else {
                val stateStr = intent.extras!!.getString(TelephonyManager.EXTRA_STATE)
                val number = intent.extras!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                var state = 0
                if (stateStr == TelephonyManager.EXTRA_STATE_IDLE) {
                    state = TelephonyManager.CALL_STATE_IDLE
                } else if (stateStr == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK
                } else if (stateStr == TelephonyManager.EXTRA_STATE_RINGING) {
                    state = TelephonyManager.CALL_STATE_RINGING
                }


                onCallStateChanged(context, state, number)
            }
        }

        //Derived classes should override these to respond to specific events of interest
        protected abstract fun onIncomingCallReceived(ctx: Context, number: String?, start: Date)

        protected abstract fun onIncomingCallAnswered(ctx: Context, number: String?, start: Date)

        protected abstract fun onIncomingCallEnded(ctx: Context, number: String?, start: Date?, end: Date)

        protected abstract fun onOutgoingCallStarted(ctx: Context, number: String?, start: Date)

        protected abstract fun onOutgoingCallEnded(ctx: Context, number: String?, start: Date?, end: Date)

        protected abstract fun onMissedCall(ctx: Context, number: String?, start: Date?)

        //Deals with actual events

        //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
        //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
        fun onCallStateChanged(context: Context, state: Int, number: String?) {
            if (lastState == state) {
                //No change, debounce extras
                return
            }
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    isIncoming = true
                    callStartTime = Date()
                    savedNumber = number
                    onIncomingCallReceived(context, number, callStartTime!!)
                }
                TelephonyManager.CALL_STATE_OFFHOOK ->
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                    if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                        isIncoming = false
                        callStartTime = Date()
                        startRecording()
                        onOutgoingCallStarted(context, savedNumber, callStartTime!!)
                    } else {
                        isIncoming = true
                        callStartTime = Date()
                        startRecording()
                        onIncomingCallAnswered(context, savedNumber, callStartTime!!)
                    }
                TelephonyManager.CALL_STATE_IDLE ->
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                        //Ring but no pickup-  a miss
                        onMissedCall(context, savedNumber, callStartTime)
                    } else if (isIncoming) {
                        stopRecording()
                        Log.d("man", "yes it is here")
                        onIncomingCallEnded(context, savedNumber, callStartTime, Date())
                    } else {
                        stopRecording()
                        onOutgoingCallEnded(context, savedNumber, callStartTime, Date())
                    }
            }
            lastState = state
        }

    }

    inner class CallReceiver : PhonecallReceiver() {

        override fun onIncomingCallReceived(ctx: Context, number: String?, start: Date) {
            Log.d("onIncomingCallReceived", "$number $start")
        }

        override fun onIncomingCallAnswered(ctx: Context, number: String?, start: Date) {
            Log.d("onIncomingCallAnswered", "$number $start")
        }

        override fun onIncomingCallEnded(ctx: Context, number: String?, start: Date?, end: Date) {
            Log.d("onIncomingCallEnded", number + " " + start!!.toString() + "\t" + end.toString())
        }

        override fun onOutgoingCallStarted(ctx: Context, number: String?, start: Date) {
            Log.d("onOutgoingCallStarted", "$number $start")
        }

        override fun onOutgoingCallEnded(ctx: Context, number: String?, start: Date?, end: Date) {
            Log.d("onOutgoingCallEnded", number + " " + start!!.toString() + "\t" + end.toString())
        }

        override fun onMissedCall(ctx: Context, number: String?, start: Date?) {
            Log.d("onMissedCall", number + " " + start!!.toString())
            //        PostCallHandler postCallHandler = new PostCallHandler(number, "janskd" , "")
        }

    }

    companion object {
        private val ID_SERVICE = 101

        private val ACTION_IN = "android.intent.action.PHONE_STATE"
        private val ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL"
    }

}
