package com.example.watsonapp;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static com.example.watsonapp.FrontActivity.BAD_APP_LIST;
import static com.example.watsonapp.FrontActivity.SHARED_PREFS;

public class BackgroundService extends Service {

    ArrayList<String> tempList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Timer timer;
    Intent blockIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("ANIKET");
        blockIntent = new Intent(this,Block.class);
        blockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startTimer();
    }

    public void startTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                              block();
                                      }
                                  },
                0, 1000);
    }

    private void block(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(BAD_APP_LIST, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        tempList = gson.fromJson(json, type);

        if(tempList == null){
            tempList = new ArrayList<String>();
            tempList.clear();
        }
        if(tempList.contains(printForegroundTask())){
            Intent myIntent = getPackageManager().getLaunchIntentForPackage("com.example.watsonapp");
            //Intent myIntent = new Intent(BackgroundService.this,Block.class);
            myIntent.addCategory(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            myIntent.putExtra("A",1);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);

        }

    }

    private String printForegroundTask() {
        String currentApp = "NULL";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager)this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        Log.d("mssg",currentApp);
        return currentApp;
    }
}