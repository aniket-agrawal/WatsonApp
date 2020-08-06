package com.example.watsonapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.watsonapp.FrontActivity.BAD_APP_LIST;
import static com.example.watsonapp.FrontActivity.GOOD_APP_LIST;
import static com.example.watsonapp.FrontActivity.SHARED_PREFS;
import static com.example.watsonapp.FrontActivity.USAGE_HOUR;
import static com.example.watsonapp.FrontActivity.USAGE_MIN;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = getIntent();
        int a = intent.getIntExtra("A", 0);
        if (a == 1) {
            startActivity(new Intent(SplashActivity.this, Block.class));
            finish();
        } else {
//            zoom();
            usage();
        /*SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();


        SharedPreferences.Editor editor = sharedPreferences.edit();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<Integer> list1 = new ArrayList<>();
        list1.clear();
        list.clear();
        String json = gson.toJson(list);
        String json1 = gson.toJson(list1);
        editor.putString(GOOD_APP_LIST, json);
        editor.putString(BAD_APP_LIST, json);
        editor.putString(USAGE_MIN, json1);
        editor.putString(USAGE_HOUR, json1);
        editor.apply();*/
        mAuth = FirebaseAuth.getInstance();
        }
    }

    private void call(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(SplashActivity.this, BackgroundService.class));
                if (mAuth.getCurrentUser() == null) {
                    Intent i = new Intent(SplashActivity.this, SignInUpActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, MainPage.class));
                    finish();
                }
            }
        }, 2000);
    }


//    private void zoom() {
//        ImageView image = (ImageView)findViewById(R.id.imageView);
//        Animation animation1 =
//                AnimationUtils.loadAnimation(getApplicationContext(),
//                        R.anim.zoomin);
//        image.startAnimation(animation1);
//    }

    public void usage() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if(mode == AppOpsManager.MODE_ALLOWED){
            call();
        }
        else{
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS){
            AppOpsManager appOps = (AppOpsManager)
                    getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
            if(mode == AppOpsManager.MODE_ALLOWED){
                call();
            }
            else{
                Toast.makeText(SplashActivity.this,"This APP is not for you." , Toast.LENGTH_SHORT).show();
            }
        }
    }
}