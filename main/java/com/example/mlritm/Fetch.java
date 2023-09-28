package com.example.mlritm;

import android.os.StrictMode;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class Fetch {
    public static String[] slice(String[] array, int startIndex, int endIndex) {
        String[] slicedArray = Arrays.copyOfRange(array, startIndex, endIndex);
        return slicedArray;
    }

    public static Map<String, List<String>> toHashMap(String[] dates) {
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
        return hashMap;
    }


    public void fetch(String rollno, String pass) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
            attendance = Fetch.slice(attendance, 15, attendance.length);
            Map<String, List<String>> attendancehs =Fetch.toHashMap(attendance);
            boolean absent = hasAForToday(attendancehs);
            if(absent) {
                new Board().noti();
            }
        } catch (org.jsoup.HttpStatusException he) {
            System.out.println("Invalid Password");
        } catch (IOException ex) {
        } catch (Exception f) {
            f.printStackTrace();
        }

    }
    public static boolean hasAForToday(Map<String, List<String>> dateData) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy(EEE)");
        String today = dateFormat.format(new Date());
        System.out.println(today);
        if (dateData.containsKey(today)) {
            List<String> values = dateData.get(today);
            if(values.contains("A")) {
                System.out.println("THERE IS AA");
            }
            return values.contains("A");
        }

        return false; // Today's date is not in the hashmap
    }
}