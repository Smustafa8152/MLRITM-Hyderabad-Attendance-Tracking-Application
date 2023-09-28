package com.example.mlritm;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.mlritm.databinding.ActivityMainBinding;
import android.widget.CheckBox;
import android.widget.TextView;
import android.os.StrictMode;

import java.util.Calendar;

public class
MainActivity extends AppCompatActivity {
    public static final String rolmsg="ROLL";
    public static final String pasmsg="PASS";
    public static String ROLLNO,PASSWORD;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    public SharedPreferences sharedPreferences;
    public static boolean radiocheck;
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("003");
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String storedUsername = sharedPreferences.getString("username", null);
        String storedPassword = sharedPreferences.getString("password", null);
        System.out.println("LKLKLK");
        System.out.println(storedUsername+"PPIII");
        MainActivity.context=MainActivity.this;
        if(storedUsername != null)
        {
            MainActivity.fore();
            AlarmManager alarmManager = (AlarmManager) MainActivity.context.getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(MainActivity.context,Board.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 20);
            calendar.set(Calendar.MINUTE, 52);
            calendar.set(Calendar.SECOND, 00);
            System.out.println("GOTIpppNN");
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
            openLoading(storedUsername,storedPassword);
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView rollno = (TextView) findViewById(R.id.rollno);
        TextView password = (TextView) findViewById(R.id.password);
        MaterialButton login = (MaterialButton) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.ROLLNO=rollno.getText().toString();
                MainActivity.PASSWORD=password.getText().toString();
                System.out.println(rollno.getText().toString() + " " + password.getText().toString());
                openLoading(rollno.getText().toString(),password.getText().toString());

            }

        });
        CheckBox radioButton=(CheckBox) findViewById(R.id.radioButton);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButton.isChecked()) {
                    MainActivity.radiocheck = true;
                }
            }
        });
    }

    public void openLoading(String rollno,String password) {
        Intent intent = new Intent(this, FetchDetails.class);
        intent.putExtra(rolmsg,rollno);
        intent.putExtra(pasmsg,password);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    public static void fore()
    {
        NotificationChannel ch= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ch = new NotificationChannel("Absent","ForAbsent", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("You've Been Marked --");
            NotificationManager nm=MainActivity.context.getSystemService(NotificationManager.class);
            nm.createNotificationChannel(ch);
        }

        }
    }