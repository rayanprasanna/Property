package com.universl.realestate;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.universl.realestate.sub_activity.EnglishAddPostActivity;
import com.universl.realestate.utils.AppController;
import com.universl.realestate.utils.Constant;
import com.universl.realestate.utils.ItemData;
import com.universl.smsnotifier.AppSMSSender;
import com.universl.smsnotifier.Constants;
import com.universl.smsnotifier.MessageOperator;
import com.universl.smsnotifier.MsgOperatorFactory;
import com.universl.smsnotifier.Param;
import com.universl.smsnotifier.SMSNotifireUtils;
import com.universl.smsnotifier.SMSSender;
import com.universl.smsnotifier.USSDDialer;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    FloatingTextButton floatingTextButton;
    SearchView searchView;
    Spinner ads;
    SpinnerAdapter spinnerAdapter;
    ArrayList<ItemData> ads_types;
    ArrayList<String> image_paths;
    List<PropertyResponse> propertyResponseList,propertyResponseList_photo;
    MainAdsAdapter adsAdapter;
    private DatabaseReference databaseQuotes;
    GridView gridView;
    private AdView adView;
    private SMSSender smsSender;
    private static boolean isNotifiedSMS;

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
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.search_bar);floatingTextButton = findViewById(R.id.action_button);
        ads_types = new ArrayList<>();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if(getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>නිවාස</font>"));
            floatingTextButton.setTitle("දැන්වීම පළ කරන්න");
            ads_types.add(new ItemData("දැන්වීම් වර්ගය",R.drawable.ads));
            ads_types.add(new ItemData("ලුහුඬු දැන්වීම්",R.drawable.brief_ads));
            ads_types.add(new ItemData("ඉඩම් වෙළඳ දැන්වීම්",R.drawable.land));
            ads_types.add(new ItemData("නිවාස වෙළඳ දැන්වීම්",R.drawable.house));
        }else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>Home</font>"));
            floatingTextButton.setTitle("Post AD");
            ads_types.add(new ItemData("Type of Advertisements",R.drawable.ads));
            ads_types.add(new ItemData("Classified Ads",R.drawable.brief_ads));
            ads_types.add(new ItemData("Lands Advertisement",R.drawable.land));
            ads_types.add(new ItemData("Houses Advertisement",R.drawable.house));
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
                    Intent intent = new Intent(MainActivity.this,HitAdsActivity.class);
                    intent.putExtra("language",getIntent().getStringExtra("language"));
                    startActivity(intent);
                    finish();
                }else if (position == 2){
                    Intent intent = new Intent(MainActivity.this,LandActivity.class);
                    intent.putExtra("language",getIntent().getStringExtra("language"));
                    startActivity(intent);
                    finish();
                }else if (position == 3){
                    Intent intent = new Intent(MainActivity.this,HouseActivity.class);
                    intent.putExtra("language",getIntent().getStringExtra("language"));
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        floatingTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EnglishAddPostActivity.class);
                intent.putExtra("language",getIntent().getStringExtra("language"));
                startActivity(intent);
                finish();
            }
        });
        if(isConnect()){
            adsAdapter = new MainAdsAdapter(MainActivity.this,propertyResponseList,propertyResponseList_photo,image_paths,getIntent().getStringExtra("language"));
            gridView.setAdapter(adsAdapter);
            lord_product();
        }else {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert = new AlertDialog.Builder(MainActivity.this);
            alert.setIcon(R.mipmap.ic_property_logo);
            alert.setTitle(R.string.app_name);
            alert.setMessage("You do not have an Internet connection");
            alert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.create().show();
        }

        initAds();
        if(!isNotifiedSMS){
            smsNotify();
            isNotifiedSMS =true;
        }
    }
    private boolean isConnect(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
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
            Intent intent = new Intent(MainActivity.this,LanguageActivity.class);
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
                            if (items.get(i).status.equalsIgnoreCase("true")){
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

        deleteCache(MainActivity.this);
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

        adsAdapter = new MainAdsAdapter(MainActivity.this,filtered_output,propertyResponseList_photo,image_paths,getIntent().getStringExtra("language"));
        gridView.setAdapter(adsAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainActivity.this,LanguageActivity.class);
        startActivity(intent);
        finish();
    }

    private void initAds() {

        adView = this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void smsNotify() {


        Param param = new Param(getResources().getString(R.string.yes), getResources().getString(R.string.no));
        String serviceProvider = SMSNotifireUtils.getServiceProvider(this);
        if(Constants.SP_DIALOG1.equalsIgnoreCase(serviceProvider)
                || Constants.SP_DIALOG2.equalsIgnoreCase(serviceProvider)
                || Constants.SP_DIALOG3.equalsIgnoreCase(serviceProvider)
                || Constants.SP_HUTCH.equalsIgnoreCase(serviceProvider)){
            List<MessageOperator> messageOperators = new ArrayList<>();
            MessageOperator ideaMartOperator = MsgOperatorFactory.createMessageOperator("", Constants.SP_DIALOG1, Constants.SP_DIALOG2, Constants.SP_DIALOG3, Constants.SP_HUTCH,Constants.SP_AIRTEL);
            ideaMartOperator.setCharge("2LKR + Tax P/D 5LKR +Tax P/Ad");
            ideaMartOperator.setAlertMsg(getResources().getString(R.string.sms_msg));
            ideaMartOperator.setSmsMsg("#780*189#");
            messageOperators.add(ideaMartOperator);
            smsSender = new USSDDialer(this, messageOperators, param);

        }
        if(smsSender !=null ){
            smsSender.smsNotify(null,getResources().getString(R.string.app_name));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case SMSSender.PERMISSIONS_ACTION_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(smsSender !=null ) smsSender.smsNotify(null,getResources().getString(R.string.app_name));
                }
                return;
            }
        }
    }
}
