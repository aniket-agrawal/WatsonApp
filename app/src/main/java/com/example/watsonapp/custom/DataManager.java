package com.example.watsonapp.custom;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DataManager {

    private static DataManager mInstance;

    public static void init() {
        mInstance = new DataManager();
    }

    public static DataManager getInstance() {
        return mInstance;
    }

    public List<AppItem> getApps(Activity activity) {

        List<AppItem> items = new ArrayList<>();
        UsageStatsManager manager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
        if (manager != null) {
            String prevPackage = "";
            Map<String, Long> startPoints = new HashMap<>();
            Map<String, ClonedEvent> endPoints = new HashMap<>();
            long[] range = new long[2];
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            range[0] = cal.getTimeInMillis();
            range[1] = System.currentTimeMillis();
            long next_extract_time = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
            UsageEvents usageEvents = manager.queryEvents(range[0], range[1]);
            /*while (usageEvents.hasNextEvent()) {
                UsageEvents.Event event = new UsageEvents.Event();
                usageEvents.getNextEvent(event);
                String packageName = event.getPackageName();
                int actionType = event.getEventType();
                long eventTime = event.getTimeStamp();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(eventTime);

                PackageManager pm = activity.getPackageManager();
                if (pm.getLaunchIntentForPackage(packageName) != null
                        & ((actionType == 1 & usageEvents.hasNextEvent()) || actionType == 2)
                        & eventTime > next_extract_time) {

                }
            }*/
            while (usageEvents.hasNextEvent()) {
                UsageEvents.Event event = new UsageEvents.Event();
                usageEvents.getNextEvent(event);
                int eventType = event.getEventType();
                long eventTime = event.getTimeStamp();
                String eventPackage = event.getPackageName();
                if (eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    AppItem item = containItem(items, eventPackage);
                    if (item == null) {
                        item = new AppItem();
                        item.mPackageName = eventPackage;
                        items.add(item);
                    }
                    if (!startPoints.containsKey(eventPackage)) {
                        startPoints.put(eventPackage, eventTime);
                    }
                }
                if (eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                    if (startPoints.size() > 0 && startPoints.containsKey(eventPackage)) {
                        endPoints.put(eventPackage, new ClonedEvent(event));
                    }
                }
                if (TextUtils.isEmpty(prevPackage)) prevPackage = eventPackage;
                if (!prevPackage.equals(eventPackage)) {
                    if (startPoints.containsKey(prevPackage) && endPoints.containsKey(prevPackage)) {
                        ClonedEvent lastEndEvent = endPoints.get(prevPackage);
                        AppItem listItem = containItem(items, prevPackage);
                        if (listItem != null) {
                            listItem.mEventTime = lastEndEvent.timeStamp;
                            long duration = lastEndEvent.timeStamp - startPoints.get(prevPackage);
                            if (duration <= 0) duration = 0;
                            listItem.mUsageTime += duration+1000;
                            if (duration > 5000) {
                                listItem.mCount++;
                            }
                        }
                        startPoints.remove(prevPackage);
                        endPoints.remove(prevPackage);
                    }
                    prevPackage = eventPackage;
                }
            }
        }

        return items;
    }

    private AppItem containItem(List<AppItem> items, String packageName) {
        for (AppItem item : items) {
            if (item.mPackageName.equals(packageName)) return item;
        }
        return null;
    }

    class ClonedEvent {

        String packageName;
        String eventClass;
        long timeStamp;
        int eventType;

        ClonedEvent(UsageEvents.Event event) {
            packageName = event.getPackageName();
            eventClass = event.getClassName();
            timeStamp = event.getTimeStamp();
            eventType = event.getEventType();
        }
    }

}
