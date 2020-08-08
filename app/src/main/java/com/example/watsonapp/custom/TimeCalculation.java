package com.example.watsonapp.custom;

import android.app.Activity;

public class TimeCalculation {
    Activity activity;

    public TimeCalculation(Activity activity) {
        this.activity = activity;
    }

    public long usage(long st, long et, String packageName){
        return 0;
    }

    public long usageAll(long st, long et){
        return usageBad(st,et) + usageGood(st, et) + usageNeutral(st,et);
    }

    public long usageBad(long st, long et){
        return 0;
    }

    public long usageGood(long st, long et){
        return 0;
    }

    public long usageNeutral(long st, long et){
        return 0;
    }
}
