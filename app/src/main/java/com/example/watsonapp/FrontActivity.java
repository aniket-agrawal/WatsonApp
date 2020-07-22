package com.example.watsonapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FrontActivity extends AppCompatActivity {

    BarChart barChart;
    private final static String TAG = "Soumil";
    PackageManager pm;
    ArrayList<Apps> apps = new ArrayList<Apps>();
    RecyclerView recyclerViewApps, recyclerViewAppsGood;
    Activity activity;
    String myDate = "";
    Calendar calendar;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    long startMillis;
    String myDate1 = "";
    long endMillis;
    Map<String, UsageStats> lUsageStatsMap;
    long totalTimeUsageInMillis;
    long timeInSec;
    float hour;
    Dialog myDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        barChart = findViewById(R.id.graph_usage);
        myDialog = new Dialog(this);
        pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        activity = this;
        apps.clear();
        recyclerViewApps = (RecyclerView)findViewById(R.id.recycler_view_show_icons);
        recyclerViewAppsGood = (RecyclerView)findViewById(R.id.recycler_view_show_icons_good);
        calendar = Calendar.getInstance();
        endMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        startMillis =calendar.getTimeInMillis();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
        lUsageStatsMap = mUsageStatsManager.
                queryAndAggregateUsageStats(startMillis, endMillis);
        totalTimeUsageInMillis = lUsageStatsMap.get("com.whatsapp").
                getTotalTimeInForeground();
        timeInSec = totalTimeUsageInMillis / 1000;
        hour = ((float) (timeInSec*1.0)) / 3600;
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0,hour));
        endMillis = startMillis;
        startMillis = startMillis - 86400000;
        lUsageStatsMap = mUsageStatsManager.
                queryAndAggregateUsageStats(startMillis, endMillis);
        totalTimeUsageInMillis = lUsageStatsMap.get("com.whatsapp").getTotalTimeInForeground();
        timeInSec = totalTimeUsageInMillis / 1000;
        hour = timeInSec / 3600;
        barEntries.add(new BarEntry(1,hour));
        BarDataSet barDataSet = new BarDataSet(barEntries,"Usage");
        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();
        for(ApplicationInfo app : packages) {
            if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                addApps(app);
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            } else {
                addApps(app);
            }
        }
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

    public void badAdd(View view) {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.bad_apps_add);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");
        btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}