package com.example.watsonapp;

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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.example.watsonapp.FrontActivity.BAD_APP_LIST;
import static com.example.watsonapp.FrontActivity.SHARED_PREFS;

public class RecyclerBadAppsAdapter extends RecyclerView.Adapter<RecyclerBadAppsAdapter.ViewHolder> implements Filterable {

    private final static String TAG = "Soumil";
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;

    Activity activity;
    ArrayList<Apps> apps;
    ArrayList<Apps> appsFull;
    Dialog finalDialog;
    String dialogName;
    BarData barData;
    ArrayList<String> tempList;
    int type;

    public RecyclerBadAppsAdapter(Activity activity, ArrayList<Apps> apps, int type) {
        this.activity = activity;
        this.apps = apps;
        this.type = type;
        appsFull = new ArrayList<>(apps);
        finalDialog = new Dialog(activity);
    }

    @NonNull
    @Override
    public RecyclerBadAppsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_for_usage,parent,false);
//        View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_for_usage,parent,false);
//        if(activity.getClass().equals(BadAppsActivity.class)){
//            return new ViewHolder(newView);
//        }
//        else{
//            Log.d(TAG, activity.toString());
//            Toast.makeText(activity, activity.toString(), Toast.LENGTH_SHORT).show();
        return new RecyclerBadAppsAdapter.ViewHolder(view);
//    }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerBadAppsAdapter.ViewHolder holder, int position) {
        ((RecyclerBadAppsAdapter.ViewHolder) holder).bindView(position);
    }


    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageIcon;
        TextView nameApp;
        TextView pname;

        public ViewHolder(@NonNull View view) {
            super(view);
            imageIcon = view.findViewById(R.id.app_icon);
            pname = view.findViewById(R.id.app_package);
            nameApp = view.findViewById(R.id.app_name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAdd(pname.getText().toString());
                }
            });
        }

        public void bindView(int position)
        {
            Apps app = apps.get(position);
            Drawable icon = app.appIcon;
            String name = app.appName;
            dialogName = name;
            imageIcon.setImageDrawable(icon);
            nameApp.setText(name);
            pname.setText(app.pname);
        }
    }

    @Override
    public Filter getFilter() {
        return appsFilter;
    }

    private Filter appsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Apps> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(appsFull);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Apps app : appsFull){
                    if(app.appName.toLowerCase().contains(filterPattern)){
                        filteredList.add(app);

                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            apps.clear();
            apps.addAll((List) results.values);
            notifyDataSetChanged();

        }

    };

    private void finalAdd(final String packagename) {
        /*finalDialog.setContentView(R.layout.activity_detail_app_usage);
        final TextView close = finalDialog.findViewById(R.id.txtclose_detail);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });
        TextView appName = finalDialog.findViewById(R.id.detail_app_name);
        appName.setText(dialogName);
        BarChart barEachApp = finalDialog.findViewById(R.id.graph_usage_per_app);
        barEachApp.setData(usage());
        finalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        finalDialog.show();*/
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
            applicationInfo = activity.getPackageManager().getApplicationInfo(packagename,PackageManager.MATCH_UNINSTALLED_PACKAGES);
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
        appName.setText(activity.getPackageManager().getApplicationLabel(applicationInfo));
        BarChart barEachApp = finalDialog.findViewById(R.id.graph_usage_per_app);
        barEachApp.setData(usage(packagename));
        finalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        finalDialog.show();
    }

    private BarData usage(String packagename) {
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        for(int i= 17;i < 24;i++){
            cal.set(Calendar.DAY_OF_MONTH, i);
            long startMillis = cal.getTimeInMillis();
            long endMillis = startMillis+3600000;
            List<UsageStats> lUsageStatsMap = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startMillis, endMillis);
            for (UsageStats usageStats : lUsageStatsMap){
                if(packagename.equals(usageStats.getPackageName())){
                    long totalTimeUsageInMillis = usageStats.getTotalTimeInForeground();
                    long timeInSec = totalTimeUsageInMillis/1000;
                    float hour = (float) ((timeInSec*1.0)/3600);
                    barEntries.add(new BarEntry(i,hour));
                }
            }
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"usage");
        barData = new BarData();
        barData.addDataSet(barDataSet);
        return barData;
    }

    private void addBadApps(String pname, int hour, int min) throws PackageManager.NameNotFoundException {
        PackageManager pm = activity.getPackageManager();



        ApplicationInfo app = pm.getApplicationInfo(pname,PackageManager.MATCH_UNINSTALLED_PACKAGES);
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(BAD_APP_LIST, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        tempList = gson.fromJson(json, type);

        if(tempList == null){
            tempList = new ArrayList<String>();
            tempList.clear();
        }
        tempList.add(pname);

        SharedPreferences sharedPreferences1 = activity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(tempList);
        editor.putString(BAD_APP_LIST, json1);
        editor.apply();


        finalDialog.dismiss();
    }


}
