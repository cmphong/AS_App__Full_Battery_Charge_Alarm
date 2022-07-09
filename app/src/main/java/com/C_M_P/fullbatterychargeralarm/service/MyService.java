package com.C_M_P.fullbatterychargeralarm.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.C_M_P.fullbatterychargeralarm.MainActivity;
import com.C_M_P.fullbatterychargeralarm.R;
import com.C_M_P.fullbatterychargeralarm.broadcast_receiver.MyBroadcast;
import com.C_M_P.fullbatterychargeralarm.initial.MyApplication;

public class MyService extends Service {

    public static boolean isServiceRunning = false;

    public static int MY_NOTIFICATION_ID = 111;

    private MyBroadcast myBroadcast;

    private static MediaPlayer mediaPlayer;
    private static int soundAlarm = R.raw.clock_timer_beeps_trimed;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;
        Logd("onCreate() - isServiceRunning: " +isServiceRunning);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logd("onStartCommand() - isServiceRunning: " +isServiceRunning);
        showNotification();

        mediaPlayer = MediaPlayer.create(getApplicationContext(), soundAlarm);

        myBroadcast = new MyBroadcast();
        registerReceiver(myBroadcast, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
        Logd("onDestroy() - isServiceRunning: " +isServiceRunning);

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        unregisterReceiver(myBroadcast);
    }



    private void showNotification() {

        // =======================================================
        // Start Activity from Notification
        Intent intentOpenActivity = new Intent(this, MainActivity.class);
        // Set the Activity to start in a new, empty task
        intentOpenActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intentOpenActivity,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        // =======================================================

        // =======================================================

        Notification notification = new NotificationCompat.Builder(
                this,
                MyApplication.CHANNEL_ID)
                        .setContentTitle(getString(R.string.Charger_Alarm_is_running))
                        .setContentText(getString(R.string.Tap_for_more_infomation))
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setContentIntent(pendingIntent) // tạo hành động khi click lên Notification:
//                                                         // Khi click lên, sẽ chuyển tới activity
//                                                         // Main_Demo_ForegroundService bằng intent
//                        .setCustomContentView(notificationLayout) // Sử dụng Custom layout
                        .build();

        startForeground(MY_NOTIFICATION_ID, notification);
    }

    public static void startAlarm(Context context) {
        if(mediaPlayer == null) {
            // Khởi tạo
            mediaPlayer = MediaPlayer.create(context, soundAlarm);
        }

        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    public static void stopAlarm(Context context) {
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(context, soundAlarm);
        }
    }



    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== MyService.java ==============================\n" + str);
    }
}
