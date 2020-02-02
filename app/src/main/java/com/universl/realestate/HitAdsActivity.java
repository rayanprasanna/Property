package com.universl.realestate;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.universl.realestate.adapter.HitAdsAdapter;
import com.universl.realestate.adapter.MainAdsAdapter;
import com.universl.realestate.response.HitAdsResponse;
import com.universl.realestate.response.PropertyResponse;
import com.universl.realestate.sub_activity.EnglishAddPostActivity;
import com.universl.realestate.utils.AppController;
import com.universl.realestate.utils.Constant;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HitAdsActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    List<HitAdsResponse> hitAdsResponseList;
    HitAdsAdapter adsAdapter;
    SearchView searchView;
    GridView gridView;
    private AdView adView;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hit_ads);

        Toolbar toolbar = findViewById(R.id.search_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>ලුහුඬු දැන්වීම්</font>"));
        }else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>Classified Ads</font>"));
        }

        hitAdsResponseList = new ArrayList<>();
        gridView = findViewById(R.id.gridView);
        adsAdapter = new HitAdsAdapter(HitAdsActivity.this,hitAdsResponseList);
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
            Intent intent = new Intent(HitAdsActivity.this, MainActivity.class);
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
        JsonArrayRequest request = new JsonArrayRequest(Constant.BASE_URL_GET_HIT_ADS,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<HitAdsResponse> items = new Gson().fromJson(response.toString(), new TypeToken<List<HitAdsResponse>>() {
                        }.getType());

                        // adding contacts to contacts list
                        hitAdsResponseList.clear();
                        hitAdsResponseList.addAll(items);

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

        deleteCache(HitAdsActivity.this);
    }
    private void getSearch(String query){
        List<HitAdsResponse> filtered_output = new ArrayList<>();

        if (searchView != null){
            for (HitAdsResponse item : hitAdsResponseList){
                if (item.title.toLowerCase(Locale.getDefault()).contains(query))
                    filtered_output.add(item);
            }
        }else
            filtered_output = hitAdsResponseList;

        adsAdapter = new HitAdsAdapter(HitAdsActivity.this,filtered_output);
        gridView.setAdapter(adsAdapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HitAdsActivity.this, MainActivity.class);
        intent.putExtra("language",getIntent().getStringExtra("language"));
        startActivity(intent);
        finish();
    }
}
