package com.dal.tourism;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class ViewLocationsActivity extends AppCompatActivity {

    private static final String TAG = "ViewLocationsActivity";

    private ArrayList<String>  mLocations = new ArrayList<>();;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_locations);
        setTitle("Locations");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getLocations();
        initRecyclerView();
    }

    public void getLocations(){
        try {
            String url = "http://10.0.2.2:5000/locations/";
            URL locationURL = new URL(url);
            HttpURLConnection con = (HttpURLConnection) locationURL.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            JSONObject myResponse = new JSONObject(content.toString());
            JSONArray locations = ((JSONArray) myResponse.get("result"));
            Log.d(TAG, "getLocations: "+ locations);
            for (int i=0; i<locations.length(); i++) {
                mLocations.add(locations.getString(i));
            }
            Collections.sort(mLocations);
            Log.d(TAG, "getLocations: mLocations" + mLocations);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mLocations, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
