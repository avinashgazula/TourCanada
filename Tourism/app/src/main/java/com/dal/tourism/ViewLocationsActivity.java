package com.dal.tourism;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class ViewLocationsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static final String TAG = "ViewLocationsActivity";

    private ArrayList<String>  mLocations = new ArrayList<>();

    RecyclerView recyclerView;
    LocationViewAdapter adapter;

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

        getLocations();
        initRecyclerView();
    }

    private void filter(String text) {

        ArrayList<String> fLocations = new ArrayList<>();

        for (int i=0; i<mLocations.size(); i++){
            if (mLocations.get(i).toLowerCase().contains(text.toLowerCase())){
                fLocations.add(mLocations.get(i));
            }
        }

        adapter.filterList(fLocations);
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
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new LocationViewAdapter(mLocations, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Locations");
        searchView.setOnQueryTextListener(this);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setFocusable(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_sign_out){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        filter(s);
        return true;
    }
}
