package com.example.myroom.Services;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import com.example.myroom.SharedPreferences.AutoLogin;
import com.example.myroom.Activities.FirstAutoLogin_Activity;
import com.example.myroom.R;
import com.example.myroom.ServiceReceivers.AlarmReceiver;
import com.example.myroom.SharedPreferences.ReservationStatusSave;
import com.example.myroom.SharedPreferences.SettingSave;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class MyService extends Service {
    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification Notifi;
    String NOTIFICATION_CHANNEL_ID = "C";
    public static Intent serviceIntent;
    Date time;
    String daytime;
    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd");

    //0530 머훈꺼랑 합친코드
    PowerManager powerManager;

    PowerManager.WakeLock wakeLock;
    //

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent=intent;
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업

    public void onDestroy() {
        super.onDestroy();
        serviceIntent = null;
        setAlarmTimer();
        Thread.currentThread().interrupt();

        if (thread != null) {//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
            thread.interrupt();
            thread = null;
        }
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            time = new Date(System.currentTimeMillis());//현재시간
            daytime=dayFormat.format(time);//날짜단위의 현재시간
            long different = 200; //그냥 겹치면안되서 넣은 임의의값
            for(int i=0;i<ReservationStatusSave.getLength(getApplicationContext())+1;i++)
            {
                //날짜가 같으면 equals 는 같으면 1을 반환한다....
                if(dayFormat.format(ReservationStatusSave.getReservationData(getApplicationContext(),i).getR_date()).equals(daytime))
                {
                    //3600000 : 1시간 60000 : 10분
                    different = ( ReservationStatusSave.getReservationData(getApplicationContext(),i).getR_date().getTime()+
                            (3600000*ReservationStatusSave.getReservationData(getApplicationContext(),i).getStart_time()) - time.getTime()) /60000;
                    //Toast.makeText(getApplicationContext(), ""+different, Toast.LENGTH_LONG).show();
                    //하루에 한번이므로 더이상 반복할 이유가 없다...
                   break;
                }
            }
            //설정에서 알람을켜고 당일 예약 10분전~10분후인 diffrent값이 0이되고 유저가 로그인 한 상태여야 알람이 울린다.
            if(SettingSave.getSettings(getApplicationContext(),"출석전알람") && AutoLogin.getAutoLoginName(getApplicationContext()).length()>0
            && different ==10){

                //0530 머훈꺼랑 합친코드
                powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);

                wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK:No");

                wakeLock.acquire(10000);
                wakeLock.release();
                //

                Intent intent = new Intent(MyService.this, FirstAutoLogin_Activity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상일경우 채널이 필요
                    // Set importance to IMPORTANCE_LOW to mute notification sound on Android 8.0 and above
                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "C", NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription("channel 제자리입니다만");
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                    channel.setVibrationPattern(new long[]{100,100});
                    channel.enableLights(true);
                    channel.enableVibration(true);
                    Notifi_M.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID);
                    Notifi_M.createNotificationChannel(channel);

                    Notifi = new Notification.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                            .setContentTitle("스터디룸 출석체크 알림")
                            .setContentText("금일 예약하신 스터디룸 출석체크 시간입니다!")
                            .setSmallIcon(R.drawable.jjidnoti)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .build();
                    //알림 소리를 한번만 내도록
                    Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                    //확인하면 자동으로 알림이 제거 되도록
                    Notifi.flags = Notification.FLAG_AUTO_CANCEL;

                    Notifi_M.notify(777, Notifi);

                }
                //구버전
                else
                {
                    Notification.Builder builder = new Notification.Builder(getApplicationContext());
                    builder.setContentTitle("스터디룸 출석체크 알림");
                    builder.setContentText("금일 예약하신 스터디룸 출석체크시간 30분 전입니다!");
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
                    builder.setSmallIcon(R.drawable.jjidnoti);
                    builder.setContentIntent(pendingIntent);
                    builder.setDefaults(0);
                    builder.setAutoCancel(true);
                    builder.setOngoing(false);
                    builder.setOnlyAlertOnce(true);

                    Notifi_M.notify(777,builder.build());
                }
            }
        }
    }
    protected void setAlarmTimer() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }
}
