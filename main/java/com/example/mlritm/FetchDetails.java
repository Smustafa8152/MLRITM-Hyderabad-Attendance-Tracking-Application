package com.example.mlritm;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.WindowManager;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FetchDetails extends AppCompatActivity{
    private static Context context;
    private static Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent=getIntent();
        con=FetchDetails.this;
       String rollno=intent.getStringExtra(MainActivity.rolmsg);
       String password=intent.getStringExtra(MainActivity.pasmsg);
        context = this.getApplicationContext();
        Handler h= new Handler();
        new Handler(new Handler().getLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("OKKKKSKSDK");
                new FetchDetails().fetch(rollno,password);
            }
        }, 2000);
    }
    public static String[] slice(String[] array, int startIndex, int endIndex) {
        String[] slicedArray = Arrays.copyOfRange(array, startIndex, endIndex);
        return slicedArray;
    }


    public void fetch(String rollno, String pass) {
        System.out.println("OKPPPPKKKSKSDK");

        Map<String, String> loginUserData = new HashMap<>();
        Map<String, String> loginPassData = new HashMap<>();
        Map<String, String> studPageData = new HashMap<>();

        try {
            System.out.println("AVV");

            Connection.Response initialResponse = Jsoup.connect("https://mlritmexams.com/BeesERP/Login.aspx")
                    .method(Connection.Method.GET)
                    .execute();
            System.out.println("AOO");

            Document initialDocument = initialResponse.parse();
            Elements inputs = initialDocument.select("input");
            for (Element input : inputs) {
                String name = input.attr("name");
                String value = input.attr("value");
                if (!name.isEmpty()) {
                    loginUserData.put(name, value);
                }
            }
            loginUserData.put("txtUserName", rollno);
            loginUserData.put("btnNext", "Next");
            System.out.println("A");

            Connection.Response loginResponse = Jsoup.connect("https://mlritmexams.com/BeesERP/Login.aspx")
                    .data(loginUserData)
                    .method(Connection.Method.POST)
                    .cookies(initialResponse.cookies())
                    .timeout(10000)
                    .execute();
            Document loginDocument = loginResponse.parse();
            Elements warning = loginDocument.select("span[id=lblWarning]:contains(User Name is Incorrect)");
            if (warning.text().equals("User Name is Incorrect")) {
                throw new Exception("Invalid Username");
            }
            Elements inputs1 = loginDocument.select("input");
            for (Element input : inputs1) {
                String name = input.attr("name");
                String value = input.attr("value");
                if (!name.isEmpty()) {
                    loginPassData.put(name, value);
                }
            }
            System.out.println("B");

            loginPassData.put("txtPassword", pass);
            loginPassData.put("btnSubmit", "Submit");
            Connection.Response loginResponse1 = Jsoup.connect("https://mlritmexams.com/BeesERP/Login.aspx")
                    .data(loginPassData)
                    .method(Connection.Method.POST)
                    .timeout(500000)
                    .execute();
            if(MainActivity.radiocheck)
            {
                System.out.println("001");
                SharedPreferences sharedPreferences = con.getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", rollno);
                editor.putString("password", pass);
                editor.apply();
                MainActivity.fore();
                AlarmManager alarmManager = (AlarmManager) MainActivity.context.getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.context,Board.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 20);
                calendar.set(Calendar.MINUTE, 28);
                calendar.set(Calendar.SECOND, 00);
                System.out.println("GOTIpppNN");
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
            }
            Document loginDocument1 = loginResponse1.parse();
            Elements studDetails = loginDocument1.getElementsByTag("input");
            studPageData.put("__EVENTTARGET", "ctl00$cpStud$lnkStudentMain");
            for (Element input : studDetails) {
                String name = input.attr("name");
                String value = input.attr("value");
                if (!value.isEmpty()) {
                    studPageData.put(name, value);
                }
            }
            System.out.println(Thread.currentThread().getName());
            Connection.Response studPage = Jsoup.connect("https://mlritmexams.com/BeesERP/StudentLogin/StudLoginDashboard.aspx")
                    .data(studPageData)
                    .cookies(loginResponse1.cookies())
                    .timeout(500000)
                    .method(Connection.Method.POST)
                    .execute();
            Document studPageDetails = studPage.parse();
            Element studUserDetails = studPageDetails.getElementById("ctl00_cpStud_grdDaywise");
            Element totalpercentage = studPageDetails.getElementById("ctl00_cpStud_lblTotalPercentage");
            Element name = studPageDetails.getElementById("ctl00_cpHeader_ucStud_lblStudentName");
            String[] attendance = studUserDetails.text().split(" ");
            System.out.println(name.text());
            System.out.println(totalpercentage.text());
            attendance = FetchDetails.slice(attendance, 15, attendance.length);
            openhome(attendance,name.text(),totalpercentage.text());
        } catch (org.jsoup.HttpStatusException he)
        {
            Toast.makeText(FetchDetails.context, "Invalid Password", Toast.LENGTH_SHORT).show();
            opensign();
            System.out.println("Invalid Password");
            } catch (IOException ex) {
    }  catch (Exception f) {
            Toast.makeText(FetchDetails.context, f.getMessage(), Toast.LENGTH_SHORT).show();
            opensign();
            System.out.println(f.getMessage());
        };
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    public void opensign(){
        String p=FetchDetails.context.getPackageName();
        Intent intent=new Intent(FetchDetails.context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public void openhome(String[] attendance,String name,String percentage)
    {
        ArrayList<String> attendanceal=new ArrayList( Arrays.asList(attendance) );
        attendanceal.add(0,name);
        attendanceal.add(1,percentage);
        String p=FetchDetails.context.getPackageName();
        Intent intent=new Intent(FetchDetails.context,Homepage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putStringArrayListExtra("attendance",attendanceal);
        context.startActivity(intent);
    }
}