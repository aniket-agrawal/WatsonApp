package com.example.watsonapp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerBadAppsAdapter extends RecyclerView.Adapter<RecyclerBadAppsAdapter.ViewHolder> implements Filterable {

    private final static String TAG = "Soumil";

    Activity activity;
    ArrayList<Apps> apps = new ArrayList<Apps>();
    ArrayList<Apps> appsFull;

    public RecyclerBadAppsAdapter(Activity activity, ArrayList<Apps> apps) {
        this.activity = activity;
        this.apps = apps;
        appsFull = new ArrayList<Apps>(apps);
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imageIcon;
        TextView nameApp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.app_icon);
            nameApp = itemView.findViewById(R.id.app_name);
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

}
