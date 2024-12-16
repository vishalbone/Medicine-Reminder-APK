package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import android.Manifest;  // This imports the Manifest class which contains POST_NOTIFICATIONS


import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText edittextloginEmail, editTextloginPwd;
    private FirebaseAuth authProfile;
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getSupportActionBar().setTitle("Login");

        edittextloginEmail = findViewById(R.id.username_email);
        editTextloginPwd = findViewById(R.id.password_input);

        authProfile = FirebaseAuth.getInstance();

        Button loginbutton = findViewById(R.id.login_btn);
        loginbutton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textEmail = edittextloginEmail.getText().toString();
                String textPwd = editTextloginPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(MainActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    edittextloginEmail.setError("Email is required");
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(MainActivity.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    editTextloginPwd.setError("Password required");
                }else {
                    loginUser(textEmail,textPwd);
                }
            }
        }));


        Button signupbtn = findViewById(R.id.button_register);
        signupbtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        }));

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginUser(String textEmail, String textPwd) {
        authProfile.signInWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_LONG).show();

                    // Redirect to MedicalDosageActivity
                    Intent intent = new Intent(MainActivity.this, MedicineDosageActivity.class);
                    startActivity(intent);

                    // Finish the login activity so the user can't return using the back button
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Log in failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}