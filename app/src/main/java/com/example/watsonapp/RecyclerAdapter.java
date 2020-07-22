package com.example.watsonapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private final static String TAG = "Soumil";

    Activity activity;
    ArrayList<Apps> apps = new ArrayList<Apps>();

    public RecyclerAdapter(Activity activity, ArrayList<Apps> apps) {
        this.activity = activity;
        this.apps = apps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_recycler_card,parent,false);
        return new ViewHolder(view);
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
        TextView nameApp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.icon_image_card);
            nameApp = itemView.findViewById(R.id.card_app_name);
        }

        public void bindView(int position)
        {
            Apps app = apps.get(position);
            Drawable icon = app.appIcon;
            String name = app.appName;
            imageIcon.setImageDrawable(icon);
            nameApp.setText(name);
        }
    }

}
