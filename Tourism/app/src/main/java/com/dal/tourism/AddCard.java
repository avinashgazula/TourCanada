package com.dal.tourism;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class AddCard extends AppCompatActivity {
    Toolbar toolbar;
    EditText noteTitle, cardnum, expiry, cvv;
    Calendar c;
    String todaysDate;
    String currentTime;
    ImageView imageicon;
    Button btn_buy_tickets;
    TextView txt_name_val;
    TextView txt_email_val;
    TextView txt_destination_val;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New card");

        cardnum = findViewById(R.id.CardNumber);
        expiry = findViewById(R.id.Expirydate);
        cvv = findViewById(R.id.cvv);
        noteTitle = findViewById(R.id.noteTitle);
        imageicon = findViewById(R.id.cardicon);
        btn_buy_tickets = findViewById(R.id.btn_buy_tickets);

        final String name = getIntent().getStringExtra("name");
        final String email = getIntent().getStringExtra("email");
        final String phone_number = getIntent().getStringExtra("phone_number");
        final String destinationName = getIntent().getStringExtra("destinationName");



        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    getSupportActionBar().setTitle(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cardnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    if (s.charAt(0) == 1 || s.charAt(0) == 4) {
                        imageicon.setImageResource(R.drawable.visa);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // set current date and time
        c = Calendar.getInstance();
        todaysDate = c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH);
        Log.d("DATE", "Date: " + todaysDate);
        currentTime = pad(c.get(Calendar.HOUR)) + ":" + pad(c.get(Calendar.MINUTE));
        Log.d("TIME", "Time: " + currentTime);


        btn_buy_tickets.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View view){
                Intent intent = new Intent(AddCard.this, TicketConfirmationActivity.class);
                intent.putExtra("destinationName", destinationName);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone_number", phone_number);
                startActivity(intent);
            }
        });

    }

    private String pad(int time) {
        if (time < 10)
            return "0" + time;
        return String.valueOf(time);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (noteTitle.getText().length() != 0) {
                Card card = new Card(noteTitle.getText().toString(), cardnum.getText().toString(), expiry.getText().toString(), cvv.getText().toString(), todaysDate, currentTime);
                SimpleDatabase sDB = new SimpleDatabase(this);
                long id = sDB.addNote(card);
                Card check = sDB.getNote(id);
                Log.d("inserted", "card: " + id + " -> Title:" + check.getTitle() + " Date: " + check.getDate());
                onBackPressed();

                Toast.makeText(this, "Card details added successfully", Toast.LENGTH_LONG).show();
            } else {
                noteTitle.setError("Title Can not be Blank.");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }









}





