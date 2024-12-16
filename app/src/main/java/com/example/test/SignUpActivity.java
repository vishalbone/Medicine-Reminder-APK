package com.example.test;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, EditTextRegisterDOB, EditTextRegisterNumber, EditTextRegisterPwd, EditTextConfirmPwd;

    private ProgressBar progressbar;

    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;

    private static  final String TAG =  "SignUpActivity";

    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //getSupportActionBar().setTitle("Register");

        Toast.makeText(SignUpActivity.this, "You can register now", Toast.LENGTH_LONG).show();

        progressbar = findViewById(R.id.progressbar);
        editTextRegisterFullName = findViewById(R.id.edittext_register_full_name);
        editTextRegisterEmail = findViewById(R.id.edittext_register_email);
        EditTextRegisterDOB = findViewById(R.id.edittext_register_DOB);
        EditTextRegisterNumber = findViewById(R.id.edittext_register_Number);
        EditTextRegisterPwd = findViewById(R.id.edittext_register_Password);
        EditTextConfirmPwd = findViewById(R.id.edittext_register_ConfirmPassword);


        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        EditTextRegisterDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        EditTextRegisterDOB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int selectedGenderId =  radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                String textfullname = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textNumber = EditTextRegisterNumber.getText().toString();
                String textPassword = EditTextRegisterPwd.getText().toString();
                String textConfirmpwd = EditTextConfirmPwd.getText().toString();
                String textDOB = EditTextRegisterDOB.getText().toString();
                String textGender;

                if(TextUtils.isEmpty(textfullname)){
                    Toast.makeText(SignUpActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    editTextRegisterFullName.setError("Full name required");
                    editTextRegisterFullName.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(SignUpActivity.this, "Please the email address", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Email Id required");
                    editTextRegisterFullName.requestFocus();
                }else if(TextUtils.isEmpty(textNumber)){
                    Toast.makeText(SignUpActivity.this, "Please Phone Number", Toast.LENGTH_SHORT).show();
                    EditTextRegisterNumber.setError("Number required");
                    EditTextRegisterNumber.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(SignUpActivity.this, "Please enter the new password", Toast.LENGTH_SHORT).show();
                    EditTextRegisterPwd.setError("Password is required");
                    EditTextRegisterPwd.requestFocus();
                }else if(textPassword.length()<6){
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 digit", Toast.LENGTH_SHORT).show();
                    EditTextRegisterPwd.setError("Password is too weak");
                    EditTextRegisterPwd.requestFocus();
                }else if(TextUtils.isEmpty(textConfirmpwd)){
                    Toast.makeText(SignUpActivity.this, "Please confirm the password", Toast.LENGTH_SHORT).show();
                    EditTextConfirmPwd.setError("Password confirmation required");
                    EditTextConfirmPwd.requestFocus();
                } else if (!textPassword.equals(textConfirmpwd)) {
                    Toast.makeText(SignUpActivity.this, "Please re-enter your password ", Toast.LENGTH_SHORT).show();
                    EditTextConfirmPwd.setError("Password confirmation required");
                    EditTextConfirmPwd.requestFocus();
                    EditTextRegisterPwd.clearComposingText();
                    EditTextConfirmPwd.clearComposingText();

                }else if (TextUtils.isEmpty(textDOB)) {
                    Toast.makeText(SignUpActivity.this, "Please enter Date of Birth", Toast.LENGTH_SHORT).show();
                    EditTextRegisterDOB.setError("DOB is required");
                    EditTextRegisterDOB.requestFocus();
                }else if(radioGroupRegisterGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(SignUpActivity.this, "Please select the gender", Toast.LENGTH_SHORT).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }else if(textNumber.length()!=10){
                    Toast.makeText(SignUpActivity.this, "Please enter 10 digit Number", Toast.LENGTH_SHORT).show();
                    EditTextRegisterNumber.setError("10 digit number is required");
                    EditTextRegisterNumber.requestFocus();
                }else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressbar.setVisibility(View.VISIBLE);
                    registerUser(textfullname,textEmail,textNumber,textPassword, textDOB,textGender);
                }
            }
        });
    }

    private void registerUser(String textfullname, String textEmail, String textNumber, String textPassword, String textDOB, String textGender) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener(SignUpActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){

                           FirebaseUser firebaseUser = auth.getCurrentUser();

                           UserProfileChangeRequest profilechangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textfullname).build();
                           firebaseUser.updateProfile(profilechangeRequest);

                           ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDOB,textGender,textNumber);

                           DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registred Users");

                           referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {

                                   if(task.isSuccessful()){
                                       firebaseUser.sendEmailVerification();
                                       Toast.makeText(SignUpActivity.this,"User Registered Successfully",Toast.LENGTH_LONG).show();
                                       /*Intent intent = new Intent(SignUpActivity.this,UserProfileActivity.class);
                                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                               | Intent.FLAG_ACTIVITY_NEW_TASK);
                                       startActivity(intent);
                                       finish();*/
                                   }else {
                                       Toast.makeText(SignUpActivity.this,"User Registered failed",Toast.LENGTH_LONG).show();
                                       progressbar.setVisibility(View.GONE);
                                   }
                                   progressbar.setVisibility(View.GONE);
                               }
                           });

                        }else{
                           try{
                               throw  task.getException();
                           }catch (FirebaseAuthWeakPasswordException e){
                               EditTextRegisterPwd.setError("Your Password is too weak, kindly use alpha numeric");
                               EditTextRegisterPwd.requestFocus();
                           } catch (FirebaseAuthInvalidCredentialsException e) {
                               EditTextRegisterPwd.setError("your email is invalid or already in use. kindly re-enter");
                               EditTextRegisterPwd.requestFocus();
                           } catch (FirebaseAuthUserCollisionException e) {
                               EditTextRegisterPwd.setError("Your already registred with this email. Use another email.");
                               EditTextRegisterPwd.requestFocus();
                           } catch (Exception e) {
                               Log.e(TAG,e.getMessage());
                               Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                           }finally {
                               progressbar.setVisibility(View.GONE);
                           }
                       }
                    }
                });
    }
}