package com.example.watsonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText email,password,confirmPassword;
    FirebaseAuth mAuth;
    String mail,pass,confirmPass;
    private final static int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = (EditText)findViewById(R.id.signup_user_name);
        password = (EditText)findViewById(R.id.signup_password);
        confirmPassword = (EditText)findViewById(R.id.confirm_password_signup);
        mAuth = FirebaseAuth.getInstance();
        createRequest();
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

    private void createRequest() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    public void GoogleSignUp(View view) {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final DatabaseReference rootref;
                            rootref = FirebaseDatabase.getInstance().getReference();
                            final String uid = mAuth.getUid();
                            rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("users").child(uid).exists()) {
                                        Toast.makeText(SignUpActivity.this, "User already exist", Toast.LENGTH_SHORT).show();
                                    } else {
                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}