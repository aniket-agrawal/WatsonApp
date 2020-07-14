package com.example.watsonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText firstName, lastName, age;
    private String firstNameString="", lastNameSting="",gender="",typeOfReg="Google",currentUserId="";
    private int ageInt=0;
    private Button submitButton;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
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

                if(firstNameString.equals("") || lastName.equals("") || age.equals(0)){
                    Toast.makeText(MainActivity.this, "Please input all values", Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<String, Object> detailsMap = new HashMap<>();
                    detailsMap.put("uid", currentUserId);
                    detailsMap.put("First Name", firstNameString);
                    detailsMap.put("Last Name", lastNameSting);
                    detailsMap.put("Age", ageInt);
                    detailsMap.put("Gender", gender);

                    usersRef.child(currentUserId).updateChildren(detailsMap);
                    SendUserToHomePage();
                }
            }
        });

    }

    private void SendUserToHomePage()
    {

    }
}