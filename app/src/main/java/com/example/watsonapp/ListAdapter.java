package com.example.watsonapp;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
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
    ArrayList<Pair<String, Pair<String, Drawable>>> apps = new ArrayList<Pair<String, Pair<String,Drawable>>>();
    String myDate = "";
    Calendar calendar;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    long startMillis;
    String myDate1 = "";
    long endMillis;

    public ListAdapter(Activity activity, ArrayList<Pair<String, Pair<String,Drawable>>> apps) {
        this.activity = activity;
        this.apps = apps;
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
        return apps.size();
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
            String usage = apps.get(position).first;
            String name = apps.get(position).second.first;
            Drawable icon = apps.get(position).second.second;
            appUsage.setText(usage);
            appIcon.setImageDrawable(icon);
            appName.setText(name);
        }


        @Override
        public void onClick(View v) {

        }
    }
}
