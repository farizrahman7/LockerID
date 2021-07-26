package com.example.lokerid.Notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class App extends Application {
    public static final String CHANNEL_ID = "LockerIn";
    public static final String CHANNEL_NAME = "LockerIn";
    public static final String CHANNEL_DESC = "LockerIn Notifications";
    private Context context;

    public App(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        channelInit();
    }

    private void channelInit() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel channel = new NotificationChannel(CHANNEL_NAME, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(CHANNEL_DESC);
                NotificationManager manager = context.getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            } catch (Exception e) {
                Log.d("TAG", e.toString());
            }
        }
    }
}
