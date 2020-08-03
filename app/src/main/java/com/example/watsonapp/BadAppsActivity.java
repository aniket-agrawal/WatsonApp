package com.example.watsonapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.watsonapp.FrontActivity.BAD_APP_LIST;
import static com.example.watsonapp.FrontActivity.GOOD_APP_LIST;
import static com.example.watsonapp.FrontActivity.SHARED_PREFS;
import static com.example.watsonapp.FrontActivity.USAGE_HOUR;

public class BadAppsActivity extends AppCompatActivity {

    private final static String TAG = "Soumil";
    PackageManager pm;
    ArrayList<Apps> apps = new ArrayList<Apps>();
    RecyclerView recyclerViewBadApps;
    Activity activity;
    RecyclerBadAppsAdapter listAdapter;
    int type;
    ArrayList<String> tempList;
    ArrayList<String> tempListGood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bad_apps);
        Log.d("A","B");
        pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        activity = this;
        apps.clear();
        recyclerViewBadApps = (RecyclerView)findViewById(R.id.bad_apps_recycler);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();


        String json = sharedPreferences.getString(BAD_APP_LIST, null);
        String json1 = sharedPreferences.getString(GOOD_APP_LIST, null);
        Type typer = new TypeToken<ArrayList<String>>() {}.getType();
        tempList = gson.fromJson(json, typer);
        tempListGood = gson.fromJson(json1, typer);

        if(tempList == null) {
            tempList = new ArrayList<String>();
            tempList.clear();
        }

        if(tempListGood == null){
            tempListGood = new ArrayList<String>();
            tempListGood.clear();
        }

        for(ApplicationInfo app : packages) {
            String packageName = app.packageName;
            if((!tempList.contains(packageName)) && (!tempListGood.contains(packageName))) {
                if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    addApps(app);
                } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                } else {
                    addApps(app);
                }
            }
        }
        initReceivedRecyclerView();

        Log.d(TAG, String.valueOf(type));
    }

    private void initReceivedRecyclerView(){
        listAdapter  = new RecyclerBadAppsAdapter(activity,apps,type);
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
            apps.add(new Apps(app.loadLabel(pm).toString(), app.loadIcon(pm),app.packageName));
        } catch (Exception e) {
            Log.d("Soumil", app.packageName);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.apps_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem backItem = menu.findItem(R.id.back_button);
        backItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(BadAppsActivity.this, FrontActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });
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