package com.example.watsonapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(GOOD_APP_LIST, null);
        Type type1 = new TypeToken<ArrayList<String>>(){}.getType();
        tempListBlock = gson.fromJson(json, type1);
        if(tempListBlock == null){
            tempListBlock = new ArrayList<String>();
            tempListBlock.clear();
        }


        prefInt = sharedPreferences.getInt(INTEGER_PREF, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INTEGER_PREF, prefInt);
        editor.apply();


    }



    @Override
    protected void onResume() {
        super.onResume();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(5000);
        }
    }

    public void Front(View view) {
        startActivity(new Intent(Block.this,FrontActivity.class));
        finish();
    }
}