package com.example.watsonapp;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

public class Apps{
    public long appUsage;
    public String appName;
    public Drawable appIcon;

    public Apps(long appUsage, String appName, Drawable appIcon){
        this.appUsage = appUsage;
        this.appIcon = appIcon;
        this.appName = appName;
    }
    public Apps(String appName, Drawable appIcon){
        this.appIcon = appIcon;
        this.appName = appName;
    }
    public static Comparator<Apps> appTime = new Comparator<Apps>() {

        public int compare(Apps a1, Apps a2) {
            long time1 = a1.appUsage;
            long time2 = a2.appUsage;
            if(time1>time2){
                return -1;
            }
            else if(time2>time1){
                return 1;
            }
            else {
                return 0;
            }

        }};
}
