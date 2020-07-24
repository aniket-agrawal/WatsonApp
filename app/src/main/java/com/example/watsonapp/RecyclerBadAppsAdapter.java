package com.example.watsonapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerBadAppsAdapter extends RecyclerView.Adapter<RecyclerBadAppsAdapter.ViewHolder> implements Filterable {

    private final static String TAG = "Soumil";

    Activity activity;
    ArrayList<Apps> apps;
    ArrayList<Apps> appsFull;
    Dialog finalDialog;
    String dialogName;
    BarData barData;

    public RecyclerBadAppsAdapter(Activity activity, ArrayList<Apps> apps) {
        this.activity = activity;
        this.apps = apps;
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

        public ViewHolder(@NonNull View view) {
            super(view);
            imageIcon = itemView.findViewById(R.id.app_icon);
            nameApp = itemView.findViewById(R.id.app_name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAdd();
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
        appName.setText(dialogName);
        BarChart barEachApp = finalDialog.findViewById(R.id.graph_usage_per_app);
        barEachApp.setData(usage());
        finalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        finalDialog.show();
    }

    private BarData usage() {
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
        return barData;
    }
}
