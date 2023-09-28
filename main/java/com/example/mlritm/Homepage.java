package com.example.mlritm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Homepage extends AppCompatActivity {
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        context = this.getApplicationContext();
        MaterialButton signout = (MaterialButton) findViewById(R.id.signout);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("002");
                SharedPreferences sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("username");
                editor.remove("password");
                editor.apply();
                System.out.println("JHAJAJ");
                opensign();
            }

        });
        Intent intent=getIntent();
        ArrayList<String> attendance=intent.getStringArrayListExtra("attendance");
        TextView name=(TextView) findViewById(R.id.name);
        TextView percentage=(TextView) findViewById(R.id.totalpercentage);
        percentage.setText("Your Total Attendance iaas "+attendance.get(1));
        name.setText(attendance.get(0));
        attendance.remove(0);
        attendance.remove(0);
        System.out.println(name.getText().toString());
      TableLayout tableLayout=(TableLayout) findViewById(R.id.tablelayout2);
      //TableLayout.LayoutParams par=new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT,1f);
      //tableLayout.setLayoutParams(par);
        TableRow currentRow = null;
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 8);
        for (String data : attendance) {
            if (currentRow == null) {
                currentRow = new TableRow(this);
                currentRow.setLayoutParams(params);



            }
            if (data.matches("\\d{1,2}/\\d{1,2}/\\d{4}\\(\\w+\\)")) {
                // Start a new row when encountering a date
                currentRow = new TableRow(this);
                currentRow.setLayoutParams(params);
                tableLayout.addView(currentRow);
            }
            TextView textView = new TextView(this);
            textView.setText(data);
            if(data.equals("A"))
            {
                textView.setTextColor(Color.parseColor("#FF0000"));
            }
            textView.setTextSize(14);
            textView.setPadding(20,16,20,16);
            TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 8);
            textView.setLayoutParams(params2);
            currentRow.addView(textView);
            currentRow.setLayoutParams(params);


        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    public static void toHashMap(ArrayList<String> dates) {
        Map<String, List<String>> hashMap = new LinkedHashMap<>();
        String currentKey = "";
        List<String> currentLetters = new ArrayList<>();
        for (String data : dates) {
            if (data.matches("\\d{1,2}/\\d{1,2}/\\d{4}\\(\\w+\\)")) {
                currentKey = data;
                currentLetters = new ArrayList<>();
                hashMap.put(currentKey, currentLetters);
            } else if (!currentKey.isEmpty()) {
                currentLetters.add(data);
            }
        }

        for (Map.Entry<String, List<String>> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

    }
    public void opensign(){
        Toast.makeText(Homepage.context, "Sign-out Successful", Toast.LENGTH_SHORT).show();
        String p=Homepage.context.getPackageName();
        Intent intent=new Intent(Homepage.context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}