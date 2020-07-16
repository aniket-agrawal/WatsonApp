package com.example.watsonapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ShowUsage extends AppCompatActivity {
    private final static String TAG = "Aniket";
    PackageManager pm;
    ArrayList<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();
    RecyclerView recyclerView;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_usage);
        pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        activity = this;
        int size = packages.size();
        recyclerView = (RecyclerView)findViewById(R.id.usage);
        installedApps.clear();
        int i = 0;
        for(ApplicationInfo app : packages) {
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                installedApps.add(app);
            }
            else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            }
            else {
                installedApps.add(app);
            }
            i++;
        }
        try {
            initReceivedRecyclerView();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initReceivedRecyclerView() throws ParseException {
        ListAdapter listAdapter = new ListAdapter(activity,installedApps,pm);
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }
}



