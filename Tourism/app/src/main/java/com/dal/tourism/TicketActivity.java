package com.dal.tourism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Random;

public class TicketActivity extends AppCompatActivity {

    private static final String TAG = "TicketActivity";
    TextView txt_name_val;
    TextView txt_email_val;
    TextView txt_destination_val;
    TextView txt_price_val;
    Button btn_buy_tickets;
    ImageView image;
    String name;
    String email;
    String phone_number;
    String destinationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        txt_name_val = findViewById(R.id.txt_name_val);
        txt_email_val = findViewById(R.id.txt_email_val);
        txt_destination_val = findViewById(R.id.txt_destination_val);
        txt_price_val = findViewById(R.id.txt_price_val);
        btn_buy_tickets = findViewById(R.id.btn_buy_tickets);
        image = findViewById(R.id.image);

        GetDetailsHandler getDetailsHandler = new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                // The user detail are in cognitoUserDetails
                Log.d(TAG, "onSuccess: user details "+cognitoUserDetails.getAttributes().getAttributes());
                CognitoUserAttributes cognitoUserAttributes = cognitoUserDetails.getAttributes();
                Map<String, String> userDetails = cognitoUserAttributes.getAttributes();
                name = userDetails.get("name");
                email = userDetails.get("email");
                phone_number = userDetails.get("phone_number");
                destinationName = getIntent().getStringExtra("destinationName");
                String destinationImage = getIntent().getStringExtra("destinationImage");

                txt_name_val.setText(name);
                txt_name_val.setAllCaps(true);
                txt_email_val.setText(email);
                txt_email_val.setAllCaps(true);
                txt_destination_val.setText(destinationName);
                txt_destination_val.setAllCaps(true);
                int price = new Random().nextInt((20 - 11) + 1) + 11;
                price = price*100;
                txt_price_val.setText("$" + price);
                Picasso.get().load(destinationImage).into(image);

                Log.d(TAG, "onSuccess: name: "+ userDetails.get("name"));
                Log.d(TAG, "onSuccess: email: "+ userDetails.get("email"));
                Log.d(TAG, "onSuccess: phone_number: "+ userDetails.get("phone_number"));

            }

            @Override
            public void onFailure(Exception exception) {
                // Fetch user details failed, check exception for the cause
                Log.d(TAG, "onSuccess: user details "+exception);
            }
        };

        CognitoSettings cognitoSettings = new CognitoSettings(TicketActivity.this);
        cognitoSettings.getUserPool().getCurrentUser().getDetailsInBackground(getDetailsHandler);

        btn_buy_tickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicketActivity.this, AddCard.class);
                intent.putExtra("destinationName", destinationName);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone_number", phone_number);
                startActivity(intent);
            }
        });

    }
}
