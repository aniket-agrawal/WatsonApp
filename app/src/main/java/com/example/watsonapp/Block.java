package com.example.watsonapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.watsonapp.FrontActivity.GOOD_APP_LIST;
import static com.example.watsonapp.FrontActivity.SHARED_PREFS;

public class Block extends AppCompatActivity {
    ArrayList<String> tempListBlock;
    public static final String INTEGER_PREF = "Integer Block";
    int prefInt;
    String app;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);


        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(GOOD_APP_LIST, null);
        Type type1 = new TypeToken<ArrayList<String>>(){}.getType();
        tempListBlock = gson.fromJson(json, type1);
        if(tempListBlock == null){
            tempListBlock = new ArrayList<String>();
            tempListBlock.clear();
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(2000);
        }
        try {
            prefInt = sharedPreferences.getInt(INTEGER_PREF, 0);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            int next = prefInt + 1;
            next = next % tempListBlock.size();
            editor.putInt(INTEGER_PREF, next);
            editor.apply();
            app = tempListBlock.get(prefInt);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent myIntent = getPackageManager().getLaunchIntentForPackage(app);
                    startActivity(myIntent);
                    finish();
                }
            }, 2000);
        }catch (Exception e){
            Toast.makeText(this, "No Good App Selected.", Toast.LENGTH_SHORT).show();
        }
    }

    public void Front(View view) {
        startActivity(new Intent(Block.this,FrontActivity.class));
        finish();
    }
}