package com.dal.tourism;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dal.tourism.AuthenticationActivity;
import com.dal.tourism.MainActivity;
import com.dal.tourism.R;
import com.dal.tourism.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText input_email;
    EditText input_password;
    TextView txt_createAccount;
    TextView txt_forgotPassword;
    Button btn_login;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        txt_createAccount = findViewById(R.id.txt_createAccount);
        txt_forgotPassword = findViewById(R.id.txt_forgotPassword);
        btn_login = findViewById(R.id.btn_login);

        txt_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        txt_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_email.getText().toString().isEmpty()){
                    input_email.setError("Enter email");
                    input_email.requestFocus();
                }else{
                    mAuth.sendPasswordResetEmail(input_email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        System.out.println("Email sent.");
                                        Toast email_sent = Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_LONG);
                                        email_sent.show();
                                    }
                                }
                            });
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_str = input_email.getText().toString();
                String password_str = input_password.getText().toString();
                mAuth.signInWithEmailAndPassword(email_str, password_str)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast login_successful = Toast.makeText(getApplicationContext(), "Login Successful: " , Toast.LENGTH_LONG);
                                    login_successful.show();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        // Name, email address, and profile photo Url
                                        String name = user.getDisplayName();
                                        String email = user.getEmail();
                                        Uri photoUrl = user.getPhotoUrl();

                                        // Check if user's email is verified
                                        boolean emailVerified = user.isEmailVerified();


                                        System.out.println(name + email + String.valueOf(photoUrl) + String.valueOf(emailVerified));

                                        startActivity(new Intent(LoginActivity.this, AuthenticationActivity.class));
                                        finish();
                                    }

                                }
                                else{
                                    Toast login_unsuccessful = Toast.makeText(getApplicationContext(), "Login Unsuccessful: " + task.getException(), Toast.LENGTH_LONG);
                                    login_unsuccessful.show();
                                }
                            }
                        });
            }
        });
    }

}
