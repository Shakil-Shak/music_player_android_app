package com.example.gaanbajaw;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Binder;
import android.os.Build;

public class ApplicationClass extends Application {

    public static final String CHANNEL_ID_1 = "chnnel1";
    public static final String CHANNEL_ID_2 = "chnnel2";
    public static final String ACTION_PREVIOU = "actionprevious";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PLAY = "actionplay";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

    }
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_1,"chnnel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel 1 DESC...");

            NotificationChannel channe2 = new NotificationChannel(CHANNEL_ID_2,"chnnel(2)", NotificationManager.IMPORTANCE_HIGH);
            channe2.setDescription("Channel 2 DESC...");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(channe2);

        }
    }
}
