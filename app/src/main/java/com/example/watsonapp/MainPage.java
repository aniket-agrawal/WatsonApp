package com.example.watsonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.os.Handler;
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

}