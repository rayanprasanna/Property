package com.universl.realestate;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.universl.realestate.adapter.MainAdsAdapter;
import com.universl.realestate.adapter.SpinnerAdapter;
import com.universl.realestate.response.PropertyResponse;
import com.universl.realestate.utils.AppController;
import com.universl.realestate.utils.Constant;
import com.universl.realestate.utils.ItemData;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LandActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    SearchView searchView;
    ArrayList<String> image_paths;
    List<PropertyResponse> propertyResponseList,propertyResponseList_photo;
    MainAdsAdapter adsAdapter;
    private DatabaseReference databaseQuotes;
    GridView gridView;
    Spinner ads;
    SpinnerAdapter spinnerAdapter;
    ArrayList<ItemData> ads_types;
    private AdView adView;

    @Override
    protected void onStart() {
        super.onStart();
        databaseQuotes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                propertyResponseList_photo.clear();
                image_paths.clear();
                //quotesResponseList.clear();
                for (DataSnapshot quotesSnapshot : dataSnapshot.getChildren()){
                    PropertyResponse quotes = quotesSnapshot.getValue(PropertyResponse.class);

                    propertyResponseList_photo.add(quotes);
                }
                for (int i = 0; i < propertyResponseList_photo.size(); i++){
                    image_paths.add(propertyResponseList_photo.get(i).image_path_1);
                }
                image_paths.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);

        Toolbar toolbar = findViewById(R.id.search_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ads_types = new ArrayList<>();

        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>ඉඩම්</font>"));
            ads_types.add(new ItemData("දැන්වීම් වර්ගය",R.drawable.ads));
            ads_types.add(new ItemData("ඉඩම් විකිණීමට",R.drawable.land));
            ads_types.add(new ItemData("ඉඩම් කුලියට හෝ බදු දීමට",R.drawable.land));
        }else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>Lands</font>"));
            ads_types.add(new ItemData("Type of Advertisements",R.drawable.ads));
            ads_types.add(new ItemData("Land For Sale",R.drawable.land));
            ads_types.add(new ItemData("Land For Rent or Lease",R.drawable.land));
        }

        databaseQuotes = FirebaseDatabase.getInstance().getReference("favorite_property");
        propertyResponseList = new ArrayList<>(); propertyResponseList_photo = new ArrayList<>(); image_paths = new ArrayList<>();
        ads = findViewById(R.id.ads_type);
        gridView = findViewById(R.id.gridView);


        spinnerAdapter = new SpinnerAdapter(this,R.layout.spinner_layout,R.id.ads_type,ads_types);
        ads.setAdapter(spinnerAdapter);

        ads.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position = parent.getSelectedItemPosition();
                if (position == 1){
                    adsAdapter = new MainAdsAdapter(LandActivity.this,propertyResponseList,propertyResponseList_photo,image_paths,getIntent().getStringExtra("language"));
                    gridView.setAdapter(adsAdapter);
                    lord_product("For Sale");
                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>ඉඩම් විකිණීම</font>"));
                    }else {
                        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>Land For Sale</font>"));
                    }
                }else if (position == 2){
                    adsAdapter = new MainAdsAdapter(LandActivity.this,propertyResponseList,propertyResponseList_photo,image_paths,getIntent().getStringExtra("language"));
                    gridView.setAdapter(adsAdapter);
                    lord_product("For Rent Or Lease");
                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>කුලියට / බදු දීමට</font>"));
                    }else {
                        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>For Rent / Lease</font>"));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adsAdapter = new MainAdsAdapter(LandActivity.this,propertyResponseList,propertyResponseList_photo,image_paths,getIntent().getStringExtra("language"));
        gridView.setAdapter(adsAdapter);
        lord_product();
        initAds();
    }

    private void initAds() {

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                getSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                getSearch(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.power) {
            logout();
            return true;
        }
        if (id == R.id.search){
            return true;
        }
        if (id == android.R.id.home){
            Intent intent = new Intent(LandActivity.this, MainActivity.class);
            intent.putExtra("language",getIntent().getStringExtra("language"));
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void logout() {
        System.exit(0);
    }

    private static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    private void lord_product(){
        JsonArrayRequest request = new JsonArrayRequest(Constant.BASE_URL_GET_PROPERTY,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<PropertyResponse> items = new Gson().fromJson(response.toString(), new TypeToken<List<PropertyResponse>>() {
                        }.getType());

                        // adding contacts to contacts list
                        propertyResponseList.clear();
                        for (int i = 0; i < items.size();i++){
                            if (items.get(i).status.equalsIgnoreCase("true") && items.get(i).category.equalsIgnoreCase("Land")){
                                propertyResponseList.add(items.get(i));
                            }
                        }

                        // refreshing recycler view
                        adsAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);

        deleteCache(LandActivity.this);
    }
    private void lord_product(final String type){
        JsonArrayRequest request = new JsonArrayRequest(Constant.BASE_URL_GET_PROPERTY,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<PropertyResponse> items = new Gson().fromJson(response.toString(), new TypeToken<List<PropertyResponse>>() {
                        }.getType());

                        // adding contacts to contacts list
                        propertyResponseList.clear();
                        for (int i = 0; i < items.size();i++){
                            if (items.get(i).status.equalsIgnoreCase("true") && items.get(i).category.equalsIgnoreCase("Land") && items.get(i).property_type.equalsIgnoreCase(type)){
                                propertyResponseList.add(items.get(i));
                            }
                        }

                        // refreshing recycler view
                        adsAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);

        deleteCache(LandActivity.this);
    }
    private void getSearch(String query){
        List<PropertyResponse> filtered_output = new ArrayList<>();

        if (searchView != null){
            for (PropertyResponse item : propertyResponseList){
                if (item.title.toLowerCase(Locale.getDefault()).contains(query))
                    filtered_output.add(item);
            }
        }else
            filtered_output = propertyResponseList;

        adsAdapter = new MainAdsAdapter(LandActivity.this,filtered_output,propertyResponseList_photo,image_paths,getIntent().getStringExtra("language"));
        gridView.setAdapter(adsAdapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LandActivity.this, MainActivity.class);
        intent.putExtra("language",getIntent().getStringExtra("language"));
        startActivity(intent);
        finish();
    }
}
