package com.dal.tourism;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    static MultiFactorAuthenticationContinuation mfac;
    static ForgotPasswordContinuation fpc;

    EditText input_email;
    EditText input_password;
    TextView txt_createAccount;
    TextView txt_forgotPassword;
    Button btn_login;

    CognitoUser user;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG);
        }
        else{
            connected = false;
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG);
        }

        Log.d(TAG, "onCreate: connection status: "+ connected);

        GetDetailsHandler getDetailsHandler = new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                // The user detail are in cognitoUserDetails
                Log.d(TAG, "onSuccess: user details "+cognitoUserDetails);

                Intent intent = new Intent(getApplicationContext(), ViewLocationsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Exception exception) {
                // Fetch user details failed, check exception for the cause
                Log.d(TAG, "onFailure: error "+exception);
            }
        };



        CognitoSettings cognitoSettings = new CognitoSettings(LoginActivity.this);
        cognitoSettings.getUserPool().getCurrentUser().getDetailsInBackground(getDetailsHandler);





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
                    resetPassword(input_email.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                    intent.putExtra("userId", input_email.getText().toString());
                    startActivity(intent);
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
                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
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

    private void resetPassword(String userId) {

        CognitoSettings cognitoSettings = new CognitoSettings(getApplicationContext());
        CognitoUser user = cognitoSettings.getUserPool().getUser(userId);

        ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: Password Reset Success");
                Toast.makeText(getApplicationContext(), "Password Reset Successful. Login to continue", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }

            @Override
            public void getResetCode(ForgotPasswordContinuation continuation) {
                Log.d(TAG, "getResetCode: Password Reset code sent");
                fpc = continuation;
//                continuation.setVerificationCode(input_code.getText().toString());
//                continuation.setPassword(input_password.getText().toString());
//                continuation.continueTask();

            }

            @Override
            public void onFailure(Exception exception) {
                Log.d(TAG, "onFailure: password reset failed "+ exception);
            }
        };


        user.forgotPassword(forgotPasswordHandler);


    }


    public static MultiFactorAuthenticationContinuation getMFAC(){
        return mfac;
    }

    public static ForgotPasswordContinuation getFPC(){
        return fpc;
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
