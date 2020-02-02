package com.universl.realestate;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.universl.realestate.adapter.DetailsAdapter;
import com.universl.realestate.adapter.MainAdsAdapter;
import com.universl.realestate.response.PropertyResponse;
import com.universl.realestate.utils.AppController;
import com.universl.realestate.utils.Constant;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class DisplayActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    List<PropertyResponse> propertyResponseList;
    String image_path,getTag;
    ViewHolder viewHolder;
    ArrayList<String> image_path_1 = new ArrayList<>();
    DetailsAdapter detailsAdapter;
    MainAdsAdapter mainAdsAdapter;
    private AdView adView;

    @Override
    protected void onStart() {
        super.onStart();
        lord_product(image_path);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Toolbar toolbar = findViewById(R.id.search_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>විස්තර</font>"));
        }else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>Details</font>"));
        }


        viewHolder = new ViewHolder();
        viewHolder.recyclerView = findViewById(R.id.recycler_view);
        viewHolder.call = findViewById(R.id.call_button);
        viewHolder.title = findViewById(R.id.title);
        viewHolder.beds_layout = findViewById(R.id.beds_layout);
        viewHolder.baths_layout = findViewById(R.id.baths_layout);
        viewHolder.house_size_layout = findViewById(R.id.house_size_layout);
        viewHolder.land_type_layout = findViewById(R.id.land_type_layout);
        viewHolder.location = findViewById(R.id.location); viewHolder.location_text = findViewById(R.id.location_text);
        viewHolder.address = findViewById(R.id.address); viewHolder.address_text = findViewById(R.id.address_text);
        viewHolder.beds = findViewById(R.id.beds);
        viewHolder.baths = findViewById(R.id.baths);
        viewHolder.land_type = findViewById(R.id.land_type);
        viewHolder.house_size = findViewById(R.id.house_size);
        viewHolder.land_size = findViewById(R.id.land_size); viewHolder.land_size_text = findViewById(R.id.land_size_text);
        viewHolder.category = findViewById(R.id.category); viewHolder.category_text = findViewById(R.id.category_text);
        viewHolder.post = findViewById(R.id.post); viewHolder.post_text = findViewById(R.id.post_text);
        viewHolder.description = findViewById(R.id.description); viewHolder.description_text = findViewById(R.id.description_text);
        viewHolder.price = findViewById(R.id.price); viewHolder.price_text = findViewById(R.id.price_text);
        viewHolder.beds_text = findViewById(R.id.beds_text);
        viewHolder.baths_text = findViewById(R.id.baths_text);
        viewHolder.house_size_text = findViewById(R.id.house_size_text);
        viewHolder.land_type_text = findViewById(R.id.land_type_text);

        propertyResponseList = new ArrayList<>();
        image_path = getIntent().getStringExtra("ads_image");
        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
            viewHolder.call.setTitle("දැන්ම අමතන්න");viewHolder.location_text.setText("පිහිටීම :");viewHolder.address_text.setText("ලිපිනය :");viewHolder.land_size_text.setText("ඉඩමේ විශාලත්වය :");viewHolder.land_type_text.setText("ඉඩම් වර්ගය :");viewHolder.category_text.setText("ප්\u200Dරවර්ගය :");viewHolder.post_text.setText("පළ කර දිනය :");viewHolder.beds_text.setText("කාමර :");viewHolder.baths_text.setText("නාන කාමර :");viewHolder.house_size_text.setText("නිවසේ විශාලත්වය :"); viewHolder.description_text.setText("විස්තර :");
        }else {
            viewHolder.call.setTitle("Call Now");
        }
        initAds();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.power) {
            logout();
            return true;
        }
        if (id == R.id.share){
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, "# දේපළ");
            share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.universl.selfieguru");

            startActivity(Intent.createChooser(share, "Share link!"));
            return true;
        }
        if (id == android.R.id.home){
            if (viewHolder.activity.equalsIgnoreCase("House")){
                mainAdsAdapter = new MainAdsAdapter();
                Intent intent = new Intent(DisplayActivity.this, HouseActivity.class);
                intent.putExtra("language",getIntent().getStringExtra("language"));
                startActivity(intent);
                finish();
            }
            if (viewHolder.getActivity.equalsIgnoreCase("Land")){
                Intent intent = new Intent(DisplayActivity.this, LandActivity.class);
                intent.putExtra("language",getIntent().getStringExtra("language"));
                startActivity(intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void logout() {
        System.exit(0);
    }
    public class ViewHolder{
        LinearLayout beds_layout,baths_layout,house_size_layout,land_type_layout;
        TextView location,location_text,address,address_text,beds,baths,land_type,house_size,land_size,land_size_text,category,category_text,post,post_text,description,description_text,price,price_text,title,title_text;
        TextView beds_text,baths_text,house_size_text,land_type_text;
        FloatingTextButton call;
        RecyclerView recyclerView;
        String activity;
        String getActivity;
    }
    private void lord_product(final String image){
        JsonArrayRequest request = new JsonArrayRequest(Constant.BASE_URL_GET_PROPERTY,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<PropertyResponse> items = new Gson().fromJson(response.toString(), new TypeToken<List<PropertyResponse>>() {
                        }.getType());

                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + getIntent().getStringExtra("language"));

                        // adding contacts to contacts list
                        propertyResponseList.clear();
                        for (int i = 0; i < items.size();i++){
                            if (items.get(i).image_path_1.equalsIgnoreCase(image)){
                                propertyResponseList.add(items.get(i));
                            }
                        }
                        if (!propertyResponseList.get(0).image_path_1.equalsIgnoreCase("")
                                || !propertyResponseList.get(0).image_path_1.equalsIgnoreCase("Null")){
                            image_path_1.add(propertyResponseList.get(0).image_path_1);
                        }
                        if (!propertyResponseList.get(0).image_path_2.equalsIgnoreCase("")
                                || !propertyResponseList.get(0).image_path_2.equalsIgnoreCase("Null")){
                            image_path_1.add(propertyResponseList.get(0).image_path_2);
                        }
                        if (!propertyResponseList.get(0).image_path3.equalsIgnoreCase("")
                                || !propertyResponseList.get(0).image_path3.equalsIgnoreCase("Null")){
                            image_path_1.add(propertyResponseList.get(0).image_path3);
                        }
                        if (!propertyResponseList.get(0).image_path_4.equalsIgnoreCase("")
                                || !propertyResponseList.get(0).image_path_4.equalsIgnoreCase("Null")){
                            image_path_1.add(propertyResponseList.get(0).image_path_4);
                        }
                        if (!propertyResponseList.get(0).image_path_5.equalsIgnoreCase("")
                                || !propertyResponseList.get(0).image_path_5.equalsIgnoreCase("Null")){
                            image_path_1.add(propertyResponseList.get(0).image_path_5);
                        }
                        if (propertyResponseList.get(0).category.equalsIgnoreCase("House")){
                            viewHolder.land_type_layout.setVisibility(View.GONE);
                            viewHolder.land_type_text.setVisibility(View.GONE);
                            viewHolder.land_type.setVisibility(View.GONE);

                            viewHolder.activity = "House";
                            viewHolder.getActivity = "";

                            LinearLayoutManager layoutManager = new LinearLayoutManager(DisplayActivity.this,LinearLayoutManager.HORIZONTAL,false);
                            viewHolder.recyclerView.setLayoutManager(layoutManager);
                            detailsAdapter = new DetailsAdapter(DisplayActivity.this,image_path_1);
                            viewHolder.recyclerView.setAdapter(detailsAdapter);

                            viewHolder.title.setText(propertyResponseList.get(0).title);
                            viewHolder.price.setText(propertyResponseList.get(0).price);
                            viewHolder.location.setText(propertyResponseList.get(0).district + ", " + propertyResponseList.get(0).city);
                            viewHolder.address.setText(propertyResponseList.get(0).address);
                            viewHolder.beds.setText(propertyResponseList.get(0).bed);
                            viewHolder.baths.setText(propertyResponseList.get(0).bath);
                            viewHolder.house_size.setText(propertyResponseList.get(0).house_size);
                            viewHolder.land_size.setText(propertyResponseList.get(0).land_size);
                            viewHolder.category.setText(propertyResponseList.get(0).category);
                            viewHolder.post.setText(propertyResponseList.get(0).post);
                            viewHolder.description.setText(propertyResponseList.get(0).description);
                            viewHolder.call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:"+propertyResponseList.get(0).contact));
                                    startActivity(intent);
                                }
                            });
                        }else {
                            viewHolder.beds_layout.setVisibility(View.GONE); viewHolder.beds_text.setVisibility(View.GONE); viewHolder.beds.setVisibility(View.GONE);
                            viewHolder.baths_layout.setVisibility(View.GONE); viewHolder.baths_text.setVisibility(View.GONE); viewHolder.baths.setVisibility(View.GONE);
                            viewHolder.house_size_layout.setVisibility(View.GONE); viewHolder.house_size_text.setVisibility(View.GONE); viewHolder.house_size.setVisibility(View.GONE);

                            viewHolder.getActivity = "Land";
                            viewHolder.activity = "";
                            LinearLayoutManager layoutManager = new LinearLayoutManager(DisplayActivity.this,LinearLayoutManager.HORIZONTAL,false);
                            viewHolder.recyclerView.setLayoutManager(layoutManager);
                            detailsAdapter = new DetailsAdapter(DisplayActivity.this,image_path_1);
                            viewHolder.recyclerView.setAdapter(detailsAdapter);

                            viewHolder.title.setText(propertyResponseList.get(0).title);
                            viewHolder.price.setText(propertyResponseList.get(0).price);
                            viewHolder.location.setText(propertyResponseList.get(0).district + ", " + propertyResponseList.get(0).city);
                            viewHolder.address.setText(propertyResponseList.get(0).address);
                            viewHolder.land_type.setText(propertyResponseList.get(0).land_type);
                            viewHolder.land_size.setText(propertyResponseList.get(0).land_size);
                            viewHolder.category.setText(propertyResponseList.get(0).category);
                            viewHolder.post.setText(propertyResponseList.get(0).post);
                            viewHolder.description.setText(propertyResponseList.get(0).description);
                            viewHolder.call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:"+propertyResponseList.get(0).contact));
                                    startActivity(intent);
                                }
                            });
                        }
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

        deleteCache(DisplayActivity.this);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (viewHolder.activity.equalsIgnoreCase("House")){
            Intent intent = new Intent(DisplayActivity.this, HouseActivity.class);
            intent.putExtra("language",getIntent().getStringExtra("language"));
            startActivity(intent);
            finish();
        }
        if (viewHolder.getActivity.equalsIgnoreCase("Land")){
            Intent intent = new Intent(DisplayActivity.this, LandActivity.class);
            intent.putExtra("language",getIntent().getStringExtra("language"));
            startActivity(intent);
            finish();
        }
    }

    private void initAds() {

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
