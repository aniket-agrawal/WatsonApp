package com.example.watsonapp;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    Activity activity;
    ArrayList<ApplicationInfo> applicationInfos;
    PackageManager pm;
    String myDate = "";
    Calendar calendar;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date;
    long startMillis;
    String myDate1 = "";
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date1;
    long endMillis;

    public ListAdapter(Activity activity, ArrayList<ApplicationInfo> applicationInfos, PackageManager pm) throws ParseException {
        this.activity = activity;
        this.applicationInfos = applicationInfos;
        this.pm = pm;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_for_usage,parent,false);
        calendar = Calendar.getInstance();
        myDate1 = sdf.format(calendar.getTime());
        startMillis = calendar.getTimeInMillis() - 604800000;




        endMillis = calendar.getTimeInMillis();
        myDate = sdf.format(startMillis);
        return new ListViewHolder(view);
}

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return applicationInfos.size();
    }


    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView appName, appUsage;
        CircleImageView appIcon;

        public ListViewHolder(View view){
            super(view);
            appIcon = (CircleImageView)view.findViewById(R.id.app_icon);
            appName = (TextView)view.findViewById(R.id.app_name);
            appUsage = (TextView)view.findViewById(R.id.app_usage);
        }

        public void bindView(int position){
            ApplicationInfo applicationInfo = applicationInfos.get(position);
            appName.setText(applicationInfo.loadLabel(pm).toString());
            appIcon.setImageDrawable(applicationInfo.loadIcon(pm));
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
            Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.
                    queryAndAggregateUsageStats(startMillis, endMillis);
            try {
                long totalTimeUsageInMillis = lUsageStatsMap.get(applicationInfo.packageName).
                        getTotalTimeInForeground();
                long timeInSec = totalTimeUsageInMillis/1000;
                long hour = timeInSec/3600;
                long min = (timeInSec-(hour*3600))/60;
                String t = hour + " hr, " + min + " min";
                appUsage.setText(t);
            }
            catch (Exception e){
                Log.d("Aniket",applicationInfo.packageName);
            }
        }


        @Override
        public void onClick(View v) {

        }
    }
}
