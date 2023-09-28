package com.example.mlritm;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class Board extends BroadcastReceiver {
    public static Context context;
    public static Intent intent;
    @Override
    public void onReceive(Context context, Intent intent) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Board.context =context;
        Board.intent =intent;
        System.out.println("GOTINN");
        Handler h = new Handler();
       SharedPreferences sharedPreferences = context.getSharedPreferences("login", MODE_PRIVATE);
        String storedUsername = sharedPreferences.getString("username", null);
        String storedPassword = sharedPreferences.getString("password", null);
        new Handler(new Handler().getLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = context.getSharedPreferences("login", MODE_PRIVATE);
                String storedUsername = sharedPreferences.getString("username", null);
                String storedPassword = sharedPreferences.getString("password", null);
                System.out.println("POPPP");
                new Fetch().fetch(storedUsername, storedPassword);
            }
        }, 1000);
    }
        public void noti() {
            System.out.println("NOTIII");
            Intent i = new Intent(Board.context.getApplicationContext(), MainActivity.class);
            System.out.println("NOTIII");
            PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
            System.out.println("NOTIII");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Absent")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("MLRITM")
                    .setContentText("You have been marked Absent for Today...Open and check to know more!!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pi);
            System.out.println("NOTIII");
        }


    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
}