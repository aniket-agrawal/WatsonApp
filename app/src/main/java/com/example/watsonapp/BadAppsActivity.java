package com.example.watsonapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class BadAppsActivity extends AppCompatActivity {

    private final static String TAG = "Soumil";
    PackageManager pm;
    ArrayList<Apps> apps = new ArrayList<Apps>();
    RecyclerView recyclerViewBadApps;
    Activity activity;
    RecyclerBadAppsAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bad_apps);

        pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        activity = this;
        apps.clear();
        recyclerViewBadApps = (RecyclerView)findViewById(R.id.bad_apps_recycler);

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
        listAdapter  = new RecyclerBadAppsAdapter(activity,apps);
        recyclerViewBadApps.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewBadApps.setLayoutManager(layoutManager);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.apps_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}