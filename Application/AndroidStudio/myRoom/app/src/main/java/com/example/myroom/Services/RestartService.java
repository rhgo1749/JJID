package com.example.myroom.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.myroom.R;
import com.example.myroom.Services.MyService;

public class RestartService extends Service {
    NotificationManager notificationManager;
    public RestartService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상일경우 채널이 필요
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelId);
            notificationChannel.setSound(null, null);

            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder Notifi_M_R_B = new NotificationCompat.Builder(this, channelId);

            Notifi_M_R_B
                    .setContentTitle("제자리입니다만")
                    .setContentText("제자리입니다만")
                    .setSmallIcon(R.drawable.ic_menu_camera)
                    .setDefaults(0)
                    .build();
            startForeground(9, Notifi_M_R_B.build());
        }
        else
        {

        }
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);


        stopForeground(true);
        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
