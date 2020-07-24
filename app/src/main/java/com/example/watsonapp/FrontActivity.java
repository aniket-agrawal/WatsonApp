package com.example.watsonapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontActivity extends AppCompatActivity {

    private final static String TAG = "Soumil";
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;
    PackageManager pm;
    ArrayList<Apps> apps = new ArrayList<Apps>();
    ArrayList<Apps> badApps = new ArrayList<Apps>();
    RecyclerView recyclerViewApps, recyclerViewAppsGood;
    Activity activity;
    Context context;
    long startMillis;
    long endMillis;
    BarChart barChart;
    Dialog myDialog,finalDialog;
    String packagename;
    BarData barData;


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
        usage();
        recyclerViewApps = findViewById(R.id.recycler_view_show_icons);
        recyclerViewAppsGood = findViewById(R.id.recycler_view_show_icons_good);
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
        RecyclerAdapter listAdapter1 = new RecyclerAdapter(activity,badApps);
        recyclerViewApps.setAdapter(listAdapter1);
        recyclerViewAppsGood.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManagerGood = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewApps.setLayoutManager(layoutManager);
        recyclerViewAppsGood.setLayoutManager(layoutManagerGood);
    }

    void usage() {
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
            Log.d("Aniket",sdf.format(startMillis));
            Log.d("Aniket",sdf.format(endMillis));
            for (UsageStats usageStats : lUsageStatsMap){
                if("com.whatsapp".equals(usageStats.getPackageName())){
                    long totalTimeUsageInMillis = usageStats.getTotalTimeInForeground();
                    long timeInSec = totalTimeUsageInMillis/1000;
                    float hour = (float) ((timeInSec*1.0)/3600);
                    Log.d(String.valueOf(i), String.valueOf(hour));
                    barEntries.add(new BarEntry(i,hour));
                }
            }
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"usage");
        barData = new BarData();
        barData.addDataSet(barDataSet);
        barChart.setData(barData);

    }

    public void addApps(ApplicationInfo app){
        try {
            apps.add(new Apps(app.loadLabel(pm).toString(), app.loadIcon(pm)));
        } catch (Exception e) {
            Log.d("Soumil", app.packageName);
        }
    }

    public void badAdd(View view) throws PackageManager.NameNotFoundException {
        getPermission();
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
        setApp(i4,n4,"com.tencent.ig");
        setApp(i5,n5,"com.ludo.king");
        final ImageView i6 = myDialog.findViewById(R.id.i6);
        i6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent badAppsIntent = new Intent(FrontActivity.this, BadAppsActivity.class);
                startActivity(badAppsIntent);
                myDialog.dismiss();
                finish();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }


    public void goodAdd(View view) {
        Intent badAppsIntent = new Intent(FrontActivity.this, BadAppsActivity.class);
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
        i.setImageDrawable(pm.getApplicationIcon(s));
        i.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                packagename = s;
                finalAdd();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        final TextView hour = finalDialog.findViewById(R.id.hour);
        final TextView min = finalDialog.findViewById(R.id.min);
        Button add = finalDialog.findViewById(R.id.add);
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
        barEachApp.setData(barData);
        finalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        finalDialog.show();
    }

    private void addBadApps(String pname, int hour, int min) throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        ApplicationInfo app = pm.getApplicationInfo(pname,PackageManager.MATCH_UNINSTALLED_PACKAGES);
        badApps.add(new Apps(app.name,app.loadIcon(pm)));
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
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
            if(mode == AppOpsManager.MODE_ALLOWED){
            }
            else{
                Toast.makeText(FrontActivity.this,"Give that permission." , Toast.LENGTH_SHORT).show();
            }
        }
    }
}