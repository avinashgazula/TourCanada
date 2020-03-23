package com.dal.tourism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class TicketConfirmationActivity extends AppCompatActivity {
    TextView txt_name_val;
    TextView txt_email_val;
    TextView txt_destination_val;
    ImageView qrCode;
    Button btn_home;
    private static final String TAG = "TicketConfirmationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_confirmation);
        txt_name_val = findViewById(R.id.txt_name_val);
        txt_email_val = findViewById(R.id.txt_email_val);
        txt_destination_val = findViewById(R.id.txt_destination_val);
        btn_home = findViewById(R.id.btn_home);
        qrCode = findViewById(R.id.qrCode);


        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String phone_number = getIntent().getStringExtra("phone_number");
        String destinationName = getIntent().getStringExtra("destinationName");

        txt_name_val.setText(name);
        txt_name_val.setAllCaps(true);
        txt_email_val.setText(email);
        txt_email_val.setAllCaps(true);
        txt_destination_val.setText(destinationName);
        txt_destination_val.setAllCaps(true);

        try {
            // Getting QR-Code as Bitmap
            QRGEncoder qrgEncoder = new QRGEncoder(name+destinationName, null, QRGContents.Type.TEXT, 100);

            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            // Setting Bitmap to ImageView
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }

        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("TourCanada") //The sender ID shown on the device.
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.50") //Sets the max price to 0.50 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Promotional") //Sets the type to promotional.
                .withDataType("String"));

        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA5WTK4BJPARQ7JDBC", "AlT3NjH+hBE7N55wfn1VOU1jzTSqMRj5AQrcDM3d");
//        BasicSessionCredentials awsCreds = new BasicSessionCredentials ("ASIA3W66XB4QUDM4VTLF", "3uaFxMKQwpJFYBpesg1/4gSCIZYNPFKMMV74fIyc", "FwoGZXIvYXdzEFMaDPUatB1gs2qnDZKSPyK+AZqQUv+AiLEqPgqvjzlMbRIF3QmZM6j1mtVZzscmTtt8xFU04bXnG7olpAD+kUdOPIvM58n43seg9l5osGBnOI7cWAUOUD2jRTwXVCVLcUQEWbaCXmSuFc1KUQTUfNsX563miq/t4aiIjcoBfVuzAJ6dsVrGEjEMd7Mq6GBF7FNhKg6vnt5dxqsjzxDWevaWzq1ditqAQ+oTGhk1lUp26sFTkGAUOnT8CC6deHTxStS3eUcYX3AeAV2deNhbsEMo76Pg8wUyLdoa8MVmT0evoyDU4tVSzm+5iA8aasKKAKcDOt8TMrwiRsHl4hCFboZWMCDDcw==");
        AmazonSNSClient snsClient = new AmazonSNSClient(awsCreds);
        String message = "Hello, "+ name +". You have successfully purchased tickets to "+destinationName+". Have a nice trip!";
        Log.d(TAG, "onCreate: phone");
        try{
            sendSMSMessage(snsClient, message, phone_number, smsAttributes);
            sendEmailMessage(name, email, destinationName);
        }catch (Exception e){
            e.printStackTrace();
        }


        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TicketConfirmationActivity.this, ViewLocationsActivity.class));
            }
        });
    }

    public static void sendSMSMessage(AmazonSNSClient snsClient, String message,
                                      String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result); // Prints the message ID.
    }

    public static void sendEmailMessage(String name, String email, String destinationName){
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA5WTK4BJPARQ7JDBC", "AlT3NjH+hBE7N55wfn1VOU1jzTSqMRj5AQrcDM3d");
//        BasicSessionCredentials awsCreds = new BasicSessionCredentials ("ASIA3W66XB4QUDM4VTLF", "3uaFxMKQwpJFYBpesg1/4gSCIZYNPFKMMV74fIyc", "FwoGZXIvYXdzEFMaDPUatB1gs2qnDZKSPyK+AZqQUv+AiLEqPgqvjzlMbRIF3QmZM6j1mtVZzscmTtt8xFU04bXnG7olpAD+kUdOPIvM58n43seg9l5osGBnOI7cWAUOUD2jRTwXVCVLcUQEWbaCXmSuFc1KUQTUfNsX563miq/t4aiIjcoBfVuzAJ6dsVrGEjEMd7Mq6GBF7FNhKg6vnt5dxqsjzxDWevaWzq1ditqAQ+oTGhk1lUp26sFTkGAUOnT8CC6deHTxStS3eUcYX3AeAV2deNhbsEMo76Pg8wUyLdoa8MVmT0evoyDU4tVSzm+5iA8aasKKAKcDOt8TMrwiRsHl4hCFboZWMCDDcw==");
        AmazonSimpleEmailServiceClient sesClient = new AmazonSimpleEmailServiceClient( awsCreds );
        sesClient.setRegion( Region.getRegion( Regions.US_EAST_1 ) );

        String subjectText = "Ticket Confirmation - TourCanada";
        Content subjectContent = new Content(subjectText);
        String bodyText = "Hello, "+ name +". You have successfully purchased tickets to "+destinationName+". Have a nice trip!";
        Body messageBody = new Body(new Content(bodyText));
        Message feedbackMessage = new Message(subjectContent,messageBody);
        Destination destination = new Destination().withToAddresses(email);

        SendEmailRequest request = new SendEmailRequest(email,destination,feedbackMessage);
        SendEmailResult result = sesClient.sendEmail(request);
        System.out.println("sendEmailMessage: result "+ result.toString());
    }



}
