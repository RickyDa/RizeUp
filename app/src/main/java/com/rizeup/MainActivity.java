package com.rizeup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rizeup.ForgotPassword.ForgotPasswordActivity;
import com.rizeup.MainMenu.MainMenu;
import com.rizeup.SignUp.SignUpActivity;


public class MainActivity extends RiZeUpActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private Button loginBtn, signUpBtn , forgotPassBtn;
    private EditText email, password;
    private Intent signUp, homePage,forgotPassPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
        loginBtn = (Button) findViewById(R.id.btn_login);
        signUpBtn = (Button) findViewById(R.id.btn_signup);
        forgotPassBtn = (Button) findViewById(R.id.btn_forgot_password);
        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        homePage = new Intent(this, MainMenu.class);
        signUp = new Intent(this, SignUpActivity.class);
        forgotPassPage = new Intent(this, ForgotPasswordActivity.class);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(signUp);
            }
        });
        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(forgotPassPage);
            }
        });
    }
    private void loginUserAccount() {
        if(email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please fill all the fields!", Toast.LENGTH_LONG).show();
        }else {
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                                startActivity(homePage);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Login Fail!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
    private void start() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainMenu.class));
            finish();
        }
    }
}

