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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText input_email;
    EditText input_password;
    TextView txt_createAccount;
    Button btn_login;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        txt_createAccount = findViewById(R.id.txt_createAccount);
        btn_login = findViewById(R.id.btn_login);

        txt_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
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
