package com.dal.tourism;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.UserWriteRecord;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    EditText name;
    EditText email;
    EditText mobile_number;
    EditText password1;
    EditText password2;
    Button btn_signup;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        name = findViewById(R.id.input_name);
        email = findViewById(R.id.input_email);
        mobile_number = findViewById(R.id.input_mobile);
        password1 = findViewById(R.id.input_password);
        password2 = findViewById(R.id.input_reEnterPassword);

        btn_signup = findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password1.getText().toString().equals(password2.getText().toString())){
                    final String name_str = name.getText().toString();
                    final String email_str = email.getText().toString();
                    String password_str = password1.getText().toString();
                    final String mobile_number_str = mobile_number.getText().toString();

                    if (email_str.isEmpty()){
                        email.setError("Enter your email");
                        email.requestFocus();
                    }
                    if (password_str.isEmpty()){
                        password1.setError("Enter your password");
                        password1.requestFocus();
                    }

                    if (!(email_str.isEmpty() || password_str.isEmpty())){

                        mAuth.createUserWithEmailAndPassword(email_str, password_str).
                                addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this.getApplicationContext(),
                                            "SignUp unsuccessful: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    System.out.println("User created");
                                    String userId = mAuth.getCurrentUser().getUid();
                                    System.out.println("userId: " + userId);
                                    User user = new User(name_str, mobile_number_str, email_str);

                                    mDatabase.child("tourism-cloud5409").child(userId).setValue(user);

                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();




                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name_str)
                                            .build();

                                    firebaseUser.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        System.out.println("User profile updated.");
                                                    }
                                                }
                                            });

                                    Toast user_created = Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_LONG);
                                    user_created.show();
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                }
                            }
                        });


                    }

                }
                else{
                    Toast password_mismatch = Toast.makeText(getApplicationContext(), "Password mismatch", Toast.LENGTH_LONG);
                    password_mismatch.show();
                }
            }
        });
    }
}
