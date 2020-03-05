package com.dal.tourism;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewDestinationsActivity extends AppCompatActivity {

    private static final String TAG = "ViewDestinationsActivity";

    private ArrayList<String> mDestinations = new ArrayList<>();
    private ArrayList<String> mDescriptions = new ArrayList<>();
    private ArrayList<String> mImages       = new ArrayList<>();
    private ArrayList<String> mResult       = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        setTitle("Locations");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getDestinations();
        initRecyclerView();
    }

    private void getDestinations() {
        try {
            String url = "http://10.0.2.2:5000/destinations/";
            url += getIntent().getStringExtra("location");
            Log.d(TAG, "getDestinations: url " + url);
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
            JSONArray result = ((JSONArray) myResponse.get("result"));
            Log.d(TAG, "getLocations: "+ result);
            for (int i=0; i<result.length(); i++) {
                mResult.add(result.getString(i));
                Map<String, Object> retMap = new Gson().fromJson(
                        result.getString(i), new TypeToken<HashMap<String, Object>>() {}.getType()
                );
                mDestinations.add(retMap.get("name").toString());
                mDescriptions.add(retMap.get("description").toString());
                mImages.add(retMap.get("photoURL").toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        DestinationViewAdapter adapter = new DestinationViewAdapter(mDestinations, mDescriptions, mImages, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
