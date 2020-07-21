package com.example.watsonapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FrontActivity extends AppCompatActivity {

    private final static String TAG = "Soumil";
    PackageManager pm;
    ArrayList<Apps> apps = new ArrayList<Apps>();
    RecyclerView recyclerViewApps, recyclerViewAppsGood;
    Activity activity;
//    String myDate = "";
//    Calendar calendar;
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//    long startMillis;
//    String myDate1 = "";
//    long endMillis;
//    Map<String, UsageStats> lUsageStatsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        activity = this;
        apps.clear();
        recyclerViewApps = (RecyclerView)findViewById(R.id.recycler_view_show_icons);
        recyclerViewAppsGood = (RecyclerView)findViewById(R.id.recycler_view_show_icons_good);
//        calendar = Calendar.getInstance();
//        myDate1 = sdf.format(calendar.getTime());
//        long t = 86400000;
//        t = t*365;
//        startMillis = calendar.getTimeInMillis() - t;
//        Log.d("Aniket",String.valueOf(t));
//        endMillis = calendar.getTimeInMillis();
//        myDate = sdf.format(startMillis);
//        UsageStatsManager mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
//        lUsageStatsMap = mUsageStatsManager.
//                queryAndAggregateUsageStats(startMillis, endMillis);
        for(ApplicationInfo app : packages) {
            if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                addApps(app);
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            } else {
                addApps(app);
            }
        }
//        Collections.sort(apps, Apps.appTime);
        initReceivedRecyclerView();
    }

    private void initReceivedRecyclerView(){
        RecyclerAdapter listAdapter = new RecyclerAdapter(activity,apps);
        recyclerViewApps.setAdapter(listAdapter);
        recyclerViewAppsGood.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManagerGood = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewApps.setLayoutManager(layoutManager);
        recyclerViewAppsGood.setLayoutManager(layoutManagerGood);
    }

    public void addApps(ApplicationInfo app){
        try {
//            long totalTimeUsageInMillis = lUsageStatsMap.get(app.packageName).
//                    getTotalTimeInForeground();
//            long timeInSec = totalTimeUsageInMillis / 1000;
//            long hour = timeInSec / 3600;
//            long min = (timeInSec - (hour * 3600)) / 60;
//            if (hour!=0 || min!=0) {
//
//            }
            apps.add(new Apps(app.loadLabel(pm).toString(), app.loadIcon(pm)));
        } catch (Exception e) {
            Log.d("Soumil", app.packageName);
        }
    }
}