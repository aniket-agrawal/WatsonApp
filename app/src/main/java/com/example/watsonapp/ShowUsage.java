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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ShowUsage extends AppCompatActivity {
    private final static String TAG = "Aniket";
    PackageManager pm;
    ArrayList<Pair<String, Pair<String,Drawable>>> apps = new ArrayList<Pair<String, Pair<String,Drawable>>>();
    RecyclerView recyclerView;
    Activity activity;
    String myDate = "";
    Calendar calendar;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    long startMillis;
    String myDate1 = "";
    long endMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_usage);
        pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        activity = this;
        recyclerView = (RecyclerView)findViewById(R.id.usage);
        calendar = Calendar.getInstance();
        myDate1 = sdf.format(calendar.getTime());
        startMillis = calendar.getTimeInMillis() - 604800000;
        endMillis = calendar.getTimeInMillis();
        myDate = sdf.format(startMillis);
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.
                queryAndAggregateUsageStats(startMillis, endMillis);
        for(ApplicationInfo app : packages) {
            if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                try {
                    long totalTimeUsageInMillis = lUsageStatsMap.get(app.packageName).
                            getTotalTimeInForeground();
                    long timeInSec = totalTimeUsageInMillis / 1000;
                    long hour = timeInSec / 3600;
                    long min = (timeInSec - (hour * 3600)) / 60;
                    if (hour != 0 && min != 0) {
                        String usage = hour + " hr, " + min + " min";
                        Pair<String, Drawable> appNameIcon = new Pair<>(app.loadLabel(pm).toString(), app.loadIcon(pm));
                        apps.add(new Pair<String, Pair<String, Drawable>>(usage, appNameIcon));
                    }
                } catch (Exception e) {
                    Log.d("Aniket", app.packageName);
                }
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            } else {
                try {
                    long totalTimeUsageInMillis = lUsageStatsMap.get(app.packageName).
                            getTotalTimeInForeground();
                    long timeInSec = totalTimeUsageInMillis / 1000;
                    long hour = timeInSec / 3600;
                    long min = (timeInSec - (hour * 3600)) / 60;
                    if (hour != 0 && min != 0) {
                        String usage = hour + " hr, " + min + " min";
                        Pair<String, Drawable> appNameIcon = new Pair<>(app.loadLabel(pm).toString(), app.loadIcon(pm));
                        apps.add(new Pair<String, Pair<String, Drawable>>(usage, appNameIcon));
                    }
                } catch (Exception e) {
                    Log.d("Aniket", app.packageName);
                }
            }
        }
        initReceivedRecyclerView();
    }

    private void initReceivedRecyclerView(){
        ListAdapter listAdapter = new ListAdapter(activity,apps);
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }
}



