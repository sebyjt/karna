package in.iodev.karna;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


public class ExampleService extends Service {
    public static final String CHANNEL_ID = "exampleServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.karna)
                .setContentTitle("Ads will run in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(2, notification);




        //do heavy work on a background thread
        //stopSelf();
        long after=5000,interval=5000;
        new Timer().scheduleAtFixedRate(timerTask,after,interval);


        return START_NOT_STICKY;
    }
  TimerTask timerTask=new TimerTask() {
      @Override
      public void run() {
          startService(new Intent(ExampleService.this, FloatingService.class));
      }
  };


    @Override
    public void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        Intent floatingIntent = new Intent(ExampleService.this, FloatingService.class);
        stopService(floatingIntent);



    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}