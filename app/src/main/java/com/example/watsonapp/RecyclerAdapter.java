package com.example.watsonapp;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import static com.example.watsonapp.FrontActivity.GOOD_APP_LIST;
import static com.example.watsonapp.FrontActivity.SHARED_PREFS;
import static com.example.watsonapp.FrontActivity.USAGE_HOUR;
import static com.example.watsonapp.FrontActivity.USAGE_MIN;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {
    private final static String TAG = "Soumil";

    Activity activity;
    ArrayList<Apps> apps;
    ArrayList<Apps> appsFull;
    Dialog finalDialog;
    ArrayList<Integer> hourList,minList;
    BarData barData;
    int type;
    ArrayList<String> tempList;

    public RecyclerAdapter(Activity activity, ArrayList<Apps> apps,int type) {
        this.activity = activity;
        this.apps = apps;
        this.type = type;
        appsFull = new ArrayList<Apps>(apps);
        finalDialog = new Dialog(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_recycler_card,parent,false);
//        View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_for_usage,parent,false);
//        if(activity.getClass().equals(BadAppsActivity.class)){
//            return new ViewHolder(newView);
//        }
//        else{
//            Log.d(TAG, activity.toString());
//            Toast.makeText(activity, activity.toString(), Toast.LENGTH_SHORT).show();
        return new ViewHolder(view);
//    }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ((ViewHolder) holder).bindView(position);
    }


    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imageIcon;
        TextView nameApp,pname,timeLeft;
        PieChart pieChart;
        AnyChartView anyChartView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.icon_image_card);
            nameApp = itemView.findViewById(R.id.card_app_name);
            pname = itemView.findViewById(R.id.card_app_package_name);
//            anyChartView = itemView.findViewById(R.id.pie_time_left);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOrShow(pname.getText().toString());
                }
            });
            timeLeft = itemView.findViewById(R.id.text_time_left);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(int position)
        {
            Apps app = apps.get(position);
            Drawable icon = app.appIcon;
            String name = app.appName;
            imageIcon.setImageDrawable(icon);
            nameApp.setText(name);
            pname.setText(app.pname);
            pname.setVisibility(View.INVISIBLE);
            if (type == 0){
                timeLeft.setText(app.hour+"h"+":"+app.min+"m");
            }

//            pieChart.setUsePercentValues(true);
//            pieChart.getDescription().setEnabled(false);
//            pieChart.setExtraOffsets(2, 4, 2, 2);
//            pieChart.setDragDecelerationFrictionCoef(0.9f);
//            pieChart.setTransparentCircleRadius(5f);
//            pieChart.setHoleColor(Color.WHITE);
////            pieChart.animateY(1000, Easing.EaseInOutCubic);
//            ArrayList<PieEntry> yValues = new ArrayList<>();
//            yValues.add(new PieEntry(3.4f,"I"));
//            yValues.add(new PieEntry(5.6f,"T"));
//
//
//            PieDataSet dataSet = new PieDataSet(yValues, "D");
//                    dataSet.setSliceSpace(0.2f);
//            dataSet.setSelectionShift(2f);
//            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//            PieData pieData = new PieData((dataSet));
//            pieData.setValueTextSize(1f);
//            pieData.setValueTextColor(Color.YELLOW);
//            pieChart.setData(pieData);

//            Pie pie = AnyChart.pie();
//            List<DataEntry> dataEntries = new ArrayList<>();
//            dataEntries.add(new ValueDataEntry("time left", 36));
//            dataEntries.add(new ValueDataEntry("time used", 24));
//
//            pie.data(dataEntries);
//            anyChartView.setChart(pie);


        }
    }

    private void deleteOrShow(final String packagename) {
        finalDialog.setContentView(R.layout.bad_app_delete);
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
            applicationInfo = activity.getPackageManager().getApplicationInfo(packagename, PackageManager.MATCH_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Button add = finalDialog.findViewById(R.id.add);
        TextView hour = finalDialog.findViewById(R.id.hour);
        TextView min = finalDialog.findViewById(R.id.min);
        if(type==0) showTime(packagename,hour,min);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBadApps(packagename);
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

    private void deleteBadApps(String pname){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json;
        if(type==0) {
            json = sharedPreferences.getString(BAD_APP_LIST, null);
        }
        else {
            json = sharedPreferences.getString(GOOD_APP_LIST, null);
        }
        Type typer = new TypeToken<ArrayList<String>>() {}.getType();
        tempList = gson.fromJson(json, typer);
        int i = tempList.indexOf(pname);
        tempList.remove(pname);

        if(type==0){
            String jsh = sharedPreferences.getString(USAGE_HOUR,null);
            String jsm = sharedPreferences.getString(USAGE_MIN,null);
            Type type1 = new TypeToken<ArrayList<Integer>>(){}.getType();
            hourList = gson.fromJson(jsh,type1);
            minList = gson.fromJson(jsm,type1);
        }


        SharedPreferences sharedPreferences1 = activity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(tempList);
        if(type==0) {
            editor.putString(BAD_APP_LIST, json1);
            hourList.remove(i);
            minList.remove(i);
            String jsh1 = gson1.toJson(hourList);
            String jsm1 = gson1.toJson(minList);
            editor.putString(USAGE_HOUR, jsh1);
            editor.putString(USAGE_MIN, jsm1);
        }
        else {
            editor.putString(GOOD_APP_LIST, json1);
        }
        editor.apply();
        activity.startActivity(new Intent(activity,FrontActivity.class));

        finalDialog.dismiss();
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

    private void showTime(String pname, TextView hour, TextView min) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString(BAD_APP_LIST, null);
        String jsh = sharedPreferences.getString(USAGE_HOUR,null);
        String jsm = sharedPreferences.getString(USAGE_MIN,null);
        Type typer = new TypeToken<ArrayList<String>>() {}.getType();
        Type type1 = new TypeToken<ArrayList<Integer>>(){}.getType();
        tempList = gson.fromJson(json, typer);
        hourList = gson.fromJson(jsh,type1);
        minList = gson.fromJson(jsm,type1);

        int i = tempList.indexOf(pname);
        hour.setText(hourList.get(i).toString());
        min.setText(minList.get(i).toString());

    }

}
