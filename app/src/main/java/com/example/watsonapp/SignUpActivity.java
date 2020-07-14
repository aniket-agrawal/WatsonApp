package com.example.watsonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class SignUpActivity extends AppCompatActivity {

    EditText email,password,confirmPassword;
    FirebaseAuth mAuth;
    String mail,pass,confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = (EditText)findViewById(R.id.signup_user_name);
        password = (EditText)findViewById(R.id.signup_password);
        confirmPassword = (EditText)findViewById(R.id.confirm_password_signup);
        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(View view) {
        startActivity(new Intent(SignUpActivity.this,SignInUpActivity.class));
        finish();
    }

    public void Logup(View view) {
        mail = email.getText().toString().trim();
        pass = password.getText().toString().trim();
        confirmPass = confirmPassword.getText().toString().trim();
        if(checkInput()) {
            mAuth.createUserWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this,"Signed Up",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                finish();

                            } else {
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
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
        if(confirmPass.isEmpty()){
            confirmPassword.setError("Confirm your password");
            b = false;
        }
        else if(!confirmPass.equals(pass)){
            confirmPassword.setError("Both password should match");
            b = false;
        }
        return b;
    }
}