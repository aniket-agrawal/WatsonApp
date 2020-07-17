package com.example.watsonapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ShowUsage extends AppCompatActivity {
    private final static String TAG = "Aniket";
    PackageManager pm;
    ArrayList<Apps> apps = new ArrayList<Apps>();
    RecyclerView recyclerView;
    Activity activity;
    String myDate = "";
    Calendar calendar;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    long startMillis;
    String myDate1 = "";
    long endMillis;
    Map<String, UsageStats> lUsageStatsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_usage);
        pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        activity = this;
        apps.clear();
        recyclerView = (RecyclerView)findViewById(R.id.usage);
        calendar = Calendar.getInstance();
        myDate1 = sdf.format(calendar.getTime());
        long t = 86400000;
        t = t*365;
        startMillis = calendar.getTimeInMillis() - t;
        Log.d("Aniket",String.valueOf(t));
        endMillis = calendar.getTimeInMillis();
        myDate = sdf.format(startMillis);
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
        lUsageStatsMap = mUsageStatsManager.
                queryAndAggregateUsageStats(startMillis, endMillis);
        for(ApplicationInfo app : packages) {
            if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                addApps(app);
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            } else {
                addApps(app);
            }
        }
        Collections.sort(apps, Apps.appTime);
        initReceivedRecyclerView();
    }

    private void initReceivedRecyclerView(){
        ListAdapter listAdapter = new ListAdapter(activity,apps);
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    public void addApps(ApplicationInfo app){
        try {
            long totalTimeUsageInMillis = lUsageStatsMap.get(app.packageName).
                    getTotalTimeInForeground();
            long timeInSec = totalTimeUsageInMillis / 1000;
            long hour = timeInSec / 3600;
            long min = (timeInSec - (hour * 3600)) / 60;
            if (hour!=0 || min!=0) {
                apps.add(new Apps(timeInSec,app.loadLabel(pm).toString(), app.loadIcon(pm)));
            }
        } catch (Exception e) {
            Log.d("Aniket", app.packageName);
        }
    }
}



