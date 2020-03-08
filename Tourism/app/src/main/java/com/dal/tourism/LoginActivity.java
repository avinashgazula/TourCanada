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


import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    static MultiFactorAuthenticationContinuation mfac;

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

//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            // User is signed in
//            startActivity(new Intent(LoginActivity.this, ViewLocationsActivity.class));
//            finish();
//        }

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

                }
            }
        });


        final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {


            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Log.d(TAG, "onSuccess: Login Successful");

                Intent intent = new Intent(LoginActivity.this, ViewLocationsActivity.class);
                startActivity(intent);
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                // The API needs user sign-in credentials to continue
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId,
                        input_password.getText().toString(), null);

                // Pass the user sign-in credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                // Allow the sign-in to continue
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
                mfac = multiFactorAuthenticationContinuation;

                Log.d(TAG, "getMFACode: MFA Required");
                Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                startActivity(intent);
//                MFA code is verified in AuthenticationActivity
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.d(TAG, "authenticationChallenge: "+continuation);
            }

            @Override
            public void onFailure(Exception exception) {
                // Sign-in failed, check exception for the cause
                Log.d(TAG, "onFailure: sign-in failed "+exception);
            }
        };



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_str = input_email.getText().toString();
                String password_str = input_password.getText().toString();

                CognitoSettings cognitoSettings = new CognitoSettings(LoginActivity.this);
                CognitoUser user = cognitoSettings.getUserPool().getUser(email_str);

                user.getSessionInBackground(authenticationHandler);



            }
        });
    }

    public static MultiFactorAuthenticationContinuation getMFAC(){
        return mfac;
    }

}



//public class LoginActivity extends AppCompatActivity {
//
//    private FirebaseAuth mAuth;
//
//    EditText input_email;
//    EditText input_password;
//    TextView txt_createAccount;
//    TextView txt_forgotPassword;
//    Button btn_login;
//
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            // User is signed in
//            startActivity(new Intent(LoginActivity.this, ViewLocationsActivity.class));
//            finish();
//        }
//
//        input_email = findViewById(R.id.input_email);
//        input_password = findViewById(R.id.input_password);
//        txt_createAccount = findViewById(R.id.txt_createAccount);
//        txt_forgotPassword = findViewById(R.id.txt_forgotPassword);
//        btn_login = findViewById(R.id.btn_login);
//
//        txt_createAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
//            }
//        });
//
//        txt_forgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (input_email.getText().toString().isEmpty()){
//                    input_email.setError("Enter email");
//                    input_email.requestFocus();
//                }else{
//                    mAuth.sendPasswordResetEmail(input_email.getText().toString())
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        System.out.println("Email sent.");
//                                        Toast email_sent = Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_LONG);
//                                        email_sent.show();
//                                    }
//                                }
//                            });
//                }
//            }
//        });
//
//        btn_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email_str = input_email.getText().toString();
//                String password_str = input_password.getText().toString();
//                mAuth.signInWithEmailAndPassword(email_str, password_str)
//                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()){
//                                    Toast login_successful = Toast.makeText(getApplicationContext(), "Login Successful: " , Toast.LENGTH_LONG);
//                                    login_successful.show();
//                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                    if (user != null) {
//                                        // Name, email address, and profile photo Url
//                                        String name = user.getDisplayName();
//                                        String email = user.getEmail();
//                                        Uri photoUrl = user.getPhotoUrl();
//
//                                        // Check if user's email is verified
//                                        boolean emailVerified = user.isEmailVerified();
//
//
//                                        System.out.println(name + email + String.valueOf(photoUrl) + String.valueOf(emailVerified));
//
//                                        startActivity(new Intent(LoginActivity.this, AuthenticationActivity.class));
//                                        finish();
//                                    }
//
//                                }
//                                else{
//                                    Toast login_unsuccessful = Toast.makeText(getApplicationContext(), "Login Unsuccessful: " + task.getException(), Toast.LENGTH_LONG);
//                                    login_unsuccessful.show();
//                                }
//                            }
//                        });
//            }
//        });
//    }
//
//}
