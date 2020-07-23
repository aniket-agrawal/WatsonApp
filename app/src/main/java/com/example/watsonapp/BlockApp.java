package com.example.watsonapp;

import android.content.pm.ApplicationInfo;

public class BlockApp {
    public ApplicationInfo applicationInfo;
    public int hour;
    public int min;

    public BlockApp(ApplicationInfo applicationInfo, int hour, int min) {
        this.applicationInfo = applicationInfo;
        this.hour = hour;
        this.min = min;
    }
}
