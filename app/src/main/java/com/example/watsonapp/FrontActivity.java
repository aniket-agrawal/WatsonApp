package com.example.watsonapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.pm.PackageManager.MATCH_ALL;

public class FrontActivity extends AppCompatActivity {

    private final static String TAG = "Soumil";
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;
    PackageManager pm;
    ArrayList<Apps> apps = new ArrayList<Apps>();
    ArrayList<Apps> badApps = new ArrayList<>();
    ArrayList<String> tempList;
    ArrayList<Integer> hourList,minList;
    ArrayList<String> tempListGood;
    RecyclerView recyclerViewApps, recyclerViewAppsGood;
    Activity activity;
    Context context;
    long startMillis;
    long endMillis;
    BarChart barChart;
    Dialog myDialog,finalDialog;
    String packagename;
    BarData barData;
    int type;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String BAD_APP_LIST = "nameList";
    public static final String GOOD_APP_LIST = "nameListGood";
    public static final String USAGE_HOUR = "hour";
    public static final String USAGE_MIN = "min";


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        barChart = findViewById(R.id.graph_usage);
        myDialog = new Dialog(this);
        finalDialog = new Dialog(this);
        pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        activity = this;
        context = getApplicationContext();
        apps.clear();
        badApps.clear();
        barChart.setData(usage("com.whatsapp"));
        recyclerViewApps = findViewById(R.id.recycler_view_show_icons);
        recyclerViewAppsGood = findViewById(R.id.recycler_view_show_icons_good);



        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();


        String json = sharedPreferences.getString(BAD_APP_LIST, null);
        String json1 = sharedPreferences.getString(GOOD_APP_LIST, null);
        String jsh = sharedPreferences.getString(USAGE_HOUR,null);
        String jsm = sharedPreferences.getString(USAGE_MIN,null);
        Type typer = new TypeToken<ArrayList<String>>() {}.getType();
        Type type1 = new TypeToken<ArrayList<Integer>>(){}.getType();
        tempList = gson.fromJson(json, typer);
        tempListGood = gson.fromJson(json1, typer);
        hourList = gson.fromJson(jsh,type1);
        minList = gson.fromJson(jsm,type1);

        if(tempList == null){
            tempList = new ArrayList<String>();
            tempList.clear();
        }
        if(hourList == null){
            hourList = new ArrayList<Integer>();
            hourList.clear();
        }
        if(minList == null){
            minList = new ArrayList<Integer>();
            minList.clear();
        }
         if(tempListGood == null){
             tempListGood = new ArrayList<String>();
             tempListGood.clear();
         }
        int i = 0;
        for(String pname : tempList){
             ApplicationInfo app = new ApplicationInfo();
            try {
                app = pm.getApplicationInfo(pname, PackageManager.MATCH_UNINSTALLED_PACKAGES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            badApps.add(new Apps((String) app.loadLabel(pm), app.loadIcon(pm),app.packageName,hourList.get(i),minList.get(i)));
            i++;
        }

        for(String pname1 : tempListGood){
            ApplicationInfo app = new ApplicationInfo();
            try {
                app = pm.getApplicationInfo(pname1, MATCH_ALL);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            apps.add(new Apps((String) app.loadLabel(pm), app.loadIcon(pm),app.packageName));
        }

        initReceivedRecyclerView();
    }

    private void initReceivedRecyclerView(){
        RecyclerAdapter listAdapter = new RecyclerAdapter(activity,apps,1);
        RecyclerAdapter listAdapter1 = new RecyclerAdapter(activity,badApps,0);
        recyclerViewApps.setAdapter(listAdapter1);
        recyclerViewAppsGood.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManagerGood = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewApps.setLayoutManager(layoutManager);
        recyclerViewAppsGood.setLayoutManager(layoutManagerGood);
    }

    BarData usage(String pack) {
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        for(int i= 17;i < 24;i++){
            cal.set(Calendar.DAY_OF_MONTH, i);
            startMillis = cal.getTimeInMillis();
            endMillis = startMillis+3600000;
            List<UsageStats> lUsageStatsMap = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startMillis, endMillis);
            //Log.d("Aniket",sdf.format(startMillis));
            //Log.d("Aniket",sdf.format(endMillis));
            for (UsageStats usageStats : lUsageStatsMap){
                if(pack.equals(usageStats.getPackageName())){
                    long totalTimeUsageInMillis = usageStats.getTotalTimeInForeground();
                    long timeInSec = totalTimeUsageInMillis/1000;
                    float hour = (float) ((timeInSec*1.0)/3600);
                    //Log.d(String.valueOf(i), String.valueOf(hour));
                    barEntries.add(new BarEntry(i,hour));
                }
            }
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"usage");
        barData = new BarData();
        barData.addDataSet(barDataSet);
        return barData;
    }


    public void badAdd(View view) throws PackageManager.NameNotFoundException {
        getPermission();
        type = 0;
        myDialog.setContentView(R.layout.bad_apps_add);
        final ImageView i1 = myDialog.findViewById(R.id.i1);
        final ImageView i2 = myDialog.findViewById(R.id.i2);
        final ImageView i3 = myDialog.findViewById(R.id.i3);
        final ImageView i4 = myDialog.findViewById(R.id.i4);
        final ImageView i5 = myDialog.findViewById(R.id.i5);
        final TextView n1 = myDialog.findViewById(R.id.n1);
        final TextView n2 = myDialog.findViewById(R.id.n2);
        final TextView n3 = myDialog.findViewById(R.id.n3);
        final TextView n4 = myDialog.findViewById(R.id.n4);
        final TextView n5 = myDialog.findViewById(R.id.n5);
        setApp(i1,n1,"com.whatsapp");
        setApp(i2,n2,"com.instagram.android");
        setApp(i3,n3,"com.facebook.katana");
        setApp(i4,n4,"com.example.login");
        setApp(i5,n5,"com.ludo.king");
        final ImageView i6 = myDialog.findViewById(R.id.i6);
        i6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent badAppsIntent = new Intent(FrontActivity.this, BadAppsActivity.class);
                badAppsIntent.putExtra("type", type);
                startActivity(badAppsIntent);
                myDialog.dismiss();
                finish();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }


    public void goodAdd(View view) {
        type = 1;
        Intent badAppsIntent = new Intent(FrontActivity.this, BadAppsActivity.class);
        badAppsIntent.putExtra("type", type);
        startActivity(badAppsIntent);
        finish();
    }

    private void setApp(ImageView i, TextView n, final String s) throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        ApplicationInfo applicationInfo = new ApplicationInfo();
        try {
            applicationInfo = pm.getApplicationInfo(s,PackageManager.MATCH_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        n.setText(pm.getApplicationLabel(applicationInfo));
        try {
            i.setImageDrawable(pm.getApplicationIcon(s));
        }catch (Exception e){

        }
        i.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                packagename = s;
                finalAdd();
            }
        });
    }

    private void finalAdd() {
        finalDialog.setContentView(R.layout.activity_detail_app_usage);
        final TextView close = finalDialog.findViewById(R.id.txtclose_detail);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });
        TextView appName = finalDialog.findViewById(R.id.detail_app_name);
        ApplicationInfo applicationInfo = new ApplicationInfo();
        try {
            applicationInfo = pm.getApplicationInfo(packagename,PackageManager.MATCH_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final EditText hour = finalDialog.findViewById(R.id.hour);
        final EditText min = finalDialog.findViewById(R.id.min);
        Button add = finalDialog.findViewById(R.id.add);
        final TextView avgWeek = finalDialog.findViewById(R.id.avg_week);
        final TextView prevDay = finalDialog.findViewById(R.id.previous_day);
        setUsage(packagename,avgWeek,prevDay);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addBadApps(packagename,Integer.parseInt(hour.getText().toString()),Integer.parseInt(min.getText().toString()));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        appName.setText(pm.getApplicationLabel(applicationInfo));
        BarChart barEachApp = finalDialog.findViewById(R.id.graph_usage_per_app);
        barEachApp.setData(usage(packagename));
        finalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        finalDialog.show();
    }


    private void addBadApps(String pname, int hour, int min) throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();



        ApplicationInfo app = pm.getApplicationInfo(pname,PackageManager.MATCH_UNINSTALLED_PACKAGES);
        Apps app1 = new Apps((String) app.loadLabel(pm),app.loadIcon(pm),app.packageName,hour,min);
        badApps.add(app1);

        tempList.add(pname);
        hourList.add(hour);
        minList.add(min);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tempList);
        String jsh = gson.toJson(hourList);
        String jsm = gson.toJson(minList);
        editor.putString(BAD_APP_LIST, json);
        editor.putString(USAGE_HOUR, jsh);
        editor.putString(USAGE_MIN, jsm);
        editor.apply();

        initReceivedRecyclerView();

        finalDialog.dismiss();
    }


    private void getPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                android.os.Process.myUid(), getPackageName());
        if(mode == AppOpsManager.MODE_ALLOWED) {
        }
        else{
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS){
            AppOpsManager appOps = (AppOpsManager)
                    getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                    android.os.Process.myUid(), getPackageName());
            if(mode == AppOpsManager.MODE_ALLOWED){
            }
            else{
                Toast.makeText(FrontActivity.this,"Give that permission." , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUsage(String pack, TextView avgWeek, TextView prevDay) {
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        long start = cal.getTimeInMillis()-86400000;
        long end = start+3600000;
        long total = 0;
        List<UsageStats> lUsageStatsMap = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,start, end);
        for (UsageStats usageStats : lUsageStatsMap){
            if(pack.equals(usageStats.getPackageName())){
                long totalTimeUsageInMillis = usageStats.getTotalTimeInForeground();
                long timeInSec = totalTimeUsageInMillis/1000;
                total = timeInSec;
                long hour = timeInSec/3600;
                long min = (timeInSec - (hour * 3600)) / 60;
                String prev = hour+":"+min;
                prevDay.setText(prev);
            }
        }
        for (int i =0;i<6;i++){
            start -= 86400000;
            end = start+3600000;
            lUsageStatsMap = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,start, end);
            for (UsageStats usageStats : lUsageStatsMap){
                if(pack.equals(usageStats.getPackageName())){
                    long totalTimeUsageInMillis = usageStats.getTotalTimeInForeground();
                    long timeInSec = totalTimeUsageInMillis/1000;
                    total += timeInSec;
                }
            }
        }
        total = total/7;
        long hour = total/3600;
        long min = (total - (hour * 3600)) / 60;
        String avg = hour+":"+min;
        avgWeek.setText(avg);
    }

}