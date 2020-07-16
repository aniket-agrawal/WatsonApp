package com.example.watsonapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.se.omapi.Session;
import android.service.textservice.SpellCheckerService;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.login.LoginBehavior.NATIVE_WITH_FALLBACK;

public class MainPage extends AppCompatActivity {

    int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;
    FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;
    LoginManager loginManager;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                type = snapshot.child("Type of Registration").getValue().toString();
                findViewById(R.id.logout).setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        loginManager = LoginManager.getInstance();

    }

    public void LogOut(View view) {
        if (type.equals("google")) {
            googleSignInClient.signOut().addOnCompleteListener(MainPage.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mAuth.signOut();
                    Toast.makeText(MainPage.this, "Signed Out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainPage.this, SignInUpActivity.class));
                    finish();
                }
            });
        }
        else if(type.equals("facebook")){
            loginManager.logOut();
            mAuth.signOut();
            Toast.makeText(MainPage.this, "Signed Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainPage.this, SignInUpActivity.class));
            finish();
        }
        else{
            mAuth.signOut();
            Toast.makeText(MainPage.this, "Signed Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainPage.this, SignInUpActivity.class));
            finish();
        }
    }

    public void usage(View view) {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if(mode == AppOpsManager.MODE_ALLOWED){
            startActivity(new Intent(MainPage.this,ShowUsage.class));
            finish();
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
                startActivity(new Intent(MainPage.this,ShowUsage.class));
                finish();
            }
            else{
                Toast.makeText(MainPage.this,"This APP is not for you." , Toast.LENGTH_SHORT).show();
            }
        }
    }
}