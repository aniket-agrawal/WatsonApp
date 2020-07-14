package com.example.watsonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText firstName, lastName, age;
    private String firstNameString="", lastNameSting="",gender="",typeOfReg="",currentUserId="",username,password;
    private int ageInt=0;
    private Button submitButton;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private boolean b = true;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        typeOfReg = i.getStringExtra("type of reg");
        if(typeOfReg.equals("normal")){
            username = i.getStringExtra("username");
            password = i.getStringExtra("password");
        }
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference().child("Users");
        currentUserId = mAuth.getCurrentUser().getUid();

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        age = findViewById(R.id.age);
        submitButton = findViewById(R.id.submit_details_button);


        spinner = (Spinner) findViewById(R.id.input_gender);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.typeofgender));

        spinner.setAdapter(myAdapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstNameString = firstName.getText().toString();
                lastNameSting = lastName.getText().toString();
                ageInt = Integer.parseInt(age.getText().toString());
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        gender = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if(firstNameString.equals("") || lastName.equals("") || age.equals(0) || gender.equals("") || gender.equals("Gender")){
                    Toast.makeText(MainActivity.this, "Please input all values", Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<String, Object> detailsMap = new HashMap<>();
                    detailsMap.put("First Name", firstNameString);
                    detailsMap.put("Last Name", lastNameSting);
                    detailsMap.put("Age", ageInt);
                    detailsMap.put("Gender", gender);
                    detailsMap.put("Type of Registration", typeOfReg);
                    if(typeOfReg.equals("normal")){
                        detailsMap.put("Username",username);
                        detailsMap.put("Password",password);
                    }

                    usersRef.child(currentUserId).updateChildren(detailsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                b = false;
                                startActivity(new Intent(MainActivity.this,MainPage.class));
                                finish();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Please retry.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        if(b){
            user.delete();
        }
        super.onDestroy();
    }
}