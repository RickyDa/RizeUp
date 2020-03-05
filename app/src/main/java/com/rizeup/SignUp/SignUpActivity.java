package com.rizeup.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.rizeup.Login.LoginActivity;
import com.rizeup.MainMenu.MainMenu;
import com.rizeup.R;

public class SignUpActivity extends AppCompatActivity {

    private Intent login, homePage;
    private FirebaseAuth mAuth;
    private Button registerBtn;
    private EditText email,fullName,password,re_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        login = new Intent(this, LoginActivity.class);
        homePage = new Intent(this, MainMenu.class);
        registerBtn = (Button)findViewById(R.id.btn_register);
        email = (EditText)findViewById(R.id.et_email);
        fullName = (EditText)findViewById(R.id.et_full_name);
        password = (EditText)findViewById(R.id.et_password);
        re_password = (EditText)findViewById(R.id.et_retype_password);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUpUserAccount();
            }
        });
    }

    private void signUpUserAccount() {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_LONG).show();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName.getText().toString()).build();
                    mAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(homePage);
                            finish();
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
