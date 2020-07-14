package com.example.watsonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInUpActivity extends AppCompatActivity {

    EditText email,password;
    FirebaseAuth mAuth;
    String mail,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);
        email = (EditText)findViewById(R.id.login_user_name);
        password = (EditText)findViewById(R.id.login_password);
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp(View view) {
        startActivity(new Intent(SignInUpActivity.this,SignUpActivity.class));
        finish();
    }

    public void Login(View view) {
        mail = email.getText().toString().trim();
        pass = password.getText().toString().trim();
        if(checkInput()) {
            mAuth.signInWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInUpActivity.this,"Signed In",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignInUpActivity.this,MainPage.class));
                                finish();

                            } else {
                                Toast.makeText(SignInUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    private boolean checkInput() {
        boolean b = true;
        if(mail.isEmpty()){
            email.setError("Username is required");
            b = false;
        }
        if(pass.isEmpty()){
            password.setError("Password is required");
            b = false;
        }
        return b;
    }
}