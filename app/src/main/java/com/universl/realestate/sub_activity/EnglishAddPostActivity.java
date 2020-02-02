package com.universl.realestate.sub_activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.androidbuts.multispinnerfilter.MultiSpinner;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.universl.realestate.MainActivity;
import com.universl.realestate.R;
import com.universl.realestate.adapter.UploadImageAdapter;
import com.universl.realestate.helper.DocumentHelper;
import com.universl.realestate.response.DistrictResponse;
import com.universl.realestate.response.ImageResponse;
import com.universl.realestate.response.PropertyResponse;
import com.universl.realestate.service.ImgurService;
import com.universl.realestate.utils.ApiService;
import com.universl.realestate.utils.AppController;
import com.universl.realestate.utils.Constant;
import com.universl.realestate.utils.FileUtils;
import com.universl.realestate.utils.InternetConnection;
import com.universl.realestate.utils.Upload;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.universl.realestate.utils.Constant.PICK_IMAGE_REQUEST_1;
import static com.universl.realestate.utils.Constant.PICK_IMAGE_REQUEST_2;
import static com.universl.realestate.utils.Constant.PICK_IMAGE_REQUEST_3;
import static com.universl.realestate.utils.Constant.PICK_IMAGE_REQUEST_4;
import static com.universl.realestate.utils.Constant.PICK_IMAGE_REQUEST_5;
import static com.universl.realestate.utils.Constant.READ_WRITE_EXTERNAL;

public class EnglishAddPostActivity extends AppCompatActivity {
    private View parentView;
    ArrayList<String> image_urls;
    File chosenFile_1,chosenFile_2,chosenFile_3,chosenFile_4,chosenFile_5;
    private Uri returnUri_1,returnUri_2,returnUri_3,returnUri_4,returnUri_5;
    private static final String TAG = "Tag";
    CountryCodePicker codePicker;
    Spinner type,district,city,bed,bath,land_size_unit;
    MultiSpinner land_type,price_unit;
    ImageView select_image_1,select_image_2,select_image_3,select_image_4,select_image_5;
    Button upload_1,upload_2,upload_3,upload_4,upload_5,select_1,select_2,select_3,select_4,select_5,save;
    HorizontalScrollView scrollView;
    ArrayAdapter<String> types,land_size_units,districts,cities,beds,baths;
    EditText address,land_size,house_size,title,description,price,contact_number,rent;
    ArrayList<String> typesArrayList,land_size_UnitArrayList,bedList,bathList;
    LinkedHashMap<String,Boolean> landTypeArrayList,priceUnitArraylist;
    TextView ad_type,advertisement,district_text,city_text,image_details,image_error,land_type_text,
            bath_text,bed_text,land_size_text,land_size_unit_text,house_size_text,address_text,
            title_text,description_text,price_text,price_type_text,contact_text,detail_text,rent_text;
    LinearLayout district_layout,city_layout,image_layout,land_type_layout,
            bed_layout,bath_layout,land_size_layout,land_size_unit_layout,house_size_layout,
            address_layout,title_layout,description_layout,price_layout,land_price_type_layout
            ,contact_layout,details_layout,contact_number_layout,rent_layout;
    LinearLayout upload_1_layout,upload_2_layout,upload_3_layout,upload_4_layout,upload_5_layout;
    List<String> districtResponses,cityResponse;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseRef;
    List<String> image_Urls;AlertDialog.Builder alert;
    private UploadImageAdapter uploadImageAdapter;
    ArrayList<Uri> arrayList;String android_id;
    Boolean isHouseForSale = false,isHouseForRent = false,isLandForSale = false,isLandForRent = false;
    @Override
    protected void onStart() {
        super.onStart();
        getDistricts();
    }

    @SuppressLint("HardwareIds")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english_add_post);

        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        final List<String> permissionsList = new ArrayList<>();
        /*addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE);
        addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);*/

        image_urls = new ArrayList<>(); upload_1_layout = findViewById(R.id.img_layout_1); upload_2_layout = findViewById(R.id.img_layout_2); upload_3_layout = findViewById(R.id.img_layout_3); upload_4_layout = findViewById(R.id.img_layout_4); upload_5_layout = findViewById(R.id.img_layout_5);
        scrollView = findViewById(R.id.image_scroll); upload_1 = findViewById(R.id.upload_btn_1);upload_2 = findViewById(R.id.upload_btn_2);upload_3 = findViewById(R.id.upload_btn_3);upload_4 = findViewById(R.id.upload_btn_4);upload_5 = findViewById(R.id.upload_btn_5);select_image_5 = findViewById(R.id.selected_image_5);
        rent = findViewById(R.id.rent); rent_text = findViewById(R.id.rent_text); rent_layout = findViewById(R.id.rent_layout);select_image_1 = findViewById(R.id.selected_image_1); select_image_2 = findViewById(R.id.selected_image_2);select_image_3 = findViewById(R.id.selected_image_3);select_image_4 = findViewById(R.id.selected_image_4);
        district_layout = findViewById(R.id.district_layout);               city_text = findViewById(R.id.city_text);select_1 = findViewById(R.id.select_upload_btn_1); select_2 = findViewById(R.id.select_upload_btn_2);select_3 = findViewById(R.id.select_upload_btn_3);select_4 = findViewById(R.id.select_upload_btn_4);select_5 = findViewById(R.id.select_upload_btn_5);
        type = findViewById(R.id.ad_type_spinner);              district_text = findViewById(R.id.district_text);                   city = findViewById(R.id.city);
        ad_type = findViewById(R.id.ad_type);                   district = findViewById(R.id.district);                             image_layout = findViewById(R.id.image_layout);
        advertisement = findViewById(R.id.advertisement);       city_layout = findViewById(R.id.city_layout);                       image_details = findViewById(R.id.images_details);
        image_error = findViewById(R.id.ads_image_error);       land_type_layout = findViewById(R.id.land_type_layout);             land_type = (MultiSpinner) findViewById(R.id.land_type);
        land_type_text = findViewById(R.id.land_type_text);     bed_layout = findViewById(R.id.beds_layout);                        bed_text = findViewById(R.id.bed_text);
        bed = findViewById(R.id.beds);                          bath_layout = findViewById(R.id.baths_layout);                      bath_text = findViewById(R.id.baths_text);
        bath = findViewById(R.id.baths);                        land_size_layout = findViewById(R.id.land_size_layout);             land_size_text = findViewById(R.id.land_size_text);
        land_size = findViewById(R.id.land_size);               land_size_unit_layout = findViewById(R.id.land_size_unit_layout);   land_size_unit_text = findViewById(R.id.land_size_unit_text);
        land_size_unit = findViewById(R.id.land_size_unit);     house_size_layout = findViewById(R.id.house_size_layout);           house_size_text = findViewById(R.id.house_size_text);
        house_size = findViewById(R.id.house_size);             address_layout = findViewById(R.id.address_layout);                 address_text = findViewById(R.id.address_text);
        address = findViewById(R.id.address);                   title_layout = findViewById(R.id.title_layout);                     title_text = findViewById(R.id.title_text);
        title = findViewById(R.id.title);                       description_layout = findViewById(R.id.description_layout);         description_text = findViewById(R.id.description_text);
        description = findViewById(R.id.description);           price_layout = findViewById(R.id.price_layout);                     price_text = findViewById(R.id.price_text);
        price = findViewById(R.id.price);                       land_price_type_layout = findViewById(R.id.land_price_type_layout); price_type_text = findViewById(R.id.land_price_type_text);
        price_unit = findViewById(R.id.land_price_type);        contact_layout = findViewById(R.id.contact_layout);                 detail_text = findViewById(R.id.your_details);
        details_layout = findViewById(R.id.your_details_layout);contact_text = findViewById(R.id.your_telephone);                   contact_number_layout = findViewById(R.id.telephone_num_layout);
        codePicker = findViewById(R.id.country_code);           contact_number = findViewById(R.id.telephone_num);                  save = findViewById(R.id.save);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        bedList = new ArrayList<>(); bathList = new ArrayList<>(); typesArrayList = new ArrayList<>(); landTypeArrayList = new LinkedHashMap<>(); land_size_UnitArrayList = new ArrayList<>(); priceUnitArraylist = new LinkedHashMap<>(); districtResponses = new ArrayList<>(); cityResponse = new ArrayList<>();image_Urls = new ArrayList<>();
        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
            getSupportActionBar().setTitle("දැන්වීම පළ කරන්න");
            typesArrayList.add("දැන්වීම් වර්ගය");
            typesArrayList.add("ඉඩම් විකිණීමට");   landTypeArrayList.put("කෘෂිකාර්මික",false); land_size_UnitArrayList.add("ඒකකය තෝරන්න"); priceUnitArraylist.put("මුළු මුදල",false);
            typesArrayList.add("නිවාස විකිණීමට");  landTypeArrayList.put("ව්\u200Dයාපාරික",false);   land_size_UnitArrayList.add("පර්චස්");     priceUnitArraylist.put("පර්චසයක්",false);
            typesArrayList.add("ඉඩම් කුලියට හෝ බදු දීමට");   landTypeArrayList.put("නේවාසික",false);  land_size_UnitArrayList.add("අක්කර");       priceUnitArraylist.put("අක්කරයකට",false);
            typesArrayList.add("නිවාස කුලියට හෝ බදු දීමට");  landTypeArrayList.put("වෙනත්",false);        priceUnitArraylist.put("සියලු",false);

            bathList.add("නාන කාමර"); bathList.add("1"); bathList.add("2"); bathList.add("3"); bathList.add("4"); bathList.add("5"); bathList.add("6"); bathList.add("7"); bathList.add("8"); bathList.add("9"); bathList.add("10");bathList.add("10+");
            bedList.add("කාමර"); bedList.add("1"); bedList.add("2"); bedList.add("3"); bedList.add("4"); bedList.add("5"); bedList.add("6"); bedList.add("7"); bedList.add("8"); bedList.add("9"); bedList.add("10"); bedList.add("10+");
            district_text.setText("ප්\u200Dරදේශය");city_text.setText("නගරය");image_details.setText("අවම වශයෙන් එක් ඡායාරූපයක් ඇතුලත් කරන්න");land_type_text.setText("ඉඩම් වර්ගය"); land_size_text.setText("ඉඩමේ විශාලත්වය"); land_size_unit_text.setText("ඉඩමේ විශාලත්වය ඒකකය"); address_text.setText("ලිපිනය (තෝරාගත හැක)");title_text.setText("දැන්වීමේ මාතෘකාව");description_text.setText("විස්තර");price_text.setText("මිල (රු)");price_type_text.setText("මිල ඒකකය");
            ad_type.setText("ඔබ දැන්වීම පළ කිරීමට කැමති දැන්වීම් වර්ගය තෝරන්න"); land_size.setHint("ඉඩමේ විශාලත්වය");address.setHint("වීදි නම, නිවසේ අංකය හෝ / සහ තැපැල් කේතය ඇතුලත් කරන්න");title.setHint("එය කෙටියෙන් සදහන් කරන්න!");description.setHint("වැඩි විස්තර ඇතුළත් කරන්න");price.setHint("හොඳ මිලක් තෝරා ගන්න");contact_text.setText("දුරකතන අංකය");save.setText("දැන්වීම පළ කරන්න");
        }else {
            getSupportActionBar().setTitle("Upload Your Post");
            typesArrayList.add("Ads Type");
            typesArrayList.add("Land For Sale");   landTypeArrayList.put("Agricultural",false); land_size_UnitArrayList.add("Select Unit"); priceUnitArraylist.put("total price",false);
            typesArrayList.add("House For Sale");  landTypeArrayList.put("Commercial",false);   land_size_UnitArrayList.add("Perches");     priceUnitArraylist.put("per perch",false);
            typesArrayList.add("Land For Rent");   landTypeArrayList.put("Residential",false);  land_size_UnitArrayList.add("Acres");       priceUnitArraylist.put("per acres",false);
            typesArrayList.add("House For Rent");  landTypeArrayList.put("Other",false);        priceUnitArraylist.put("All",false);

            bathList.add("Baths"); bathList.add("1"); bathList.add("2"); bathList.add("3"); bathList.add("4"); bathList.add("5"); bathList.add("6"); bathList.add("7"); bathList.add("8"); bathList.add("9"); bathList.add("10");bathList.add("10+");
            bedList.add("Beds"); bedList.add("1"); bedList.add("2"); bedList.add("3"); bedList.add("4"); bedList.add("5"); bedList.add("6"); bedList.add("7"); bedList.add("8"); bedList.add("9"); bedList.add("10"); bedList.add("10+");
        }

        mStorage = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        //bedList = new ArrayList<>(); bathList = new ArrayList<>(); typesArrayList = new ArrayList<>(); landTypeArrayList = new LinkedHashMap<>(); land_size_UnitArrayList = new ArrayList<>(); priceUnitArraylist = new LinkedHashMap<>(); districtResponses = new ArrayList<>(); cityResponse = new ArrayList<>();image_Urls = new ArrayList<>();

        getCities();
        beds = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,bedList);
        baths = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,bathList);
        districts = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,districtResponses);
        cities = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,cityResponse);

        types = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,typesArrayList);    land_size_units = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,land_size_UnitArrayList);
        type.setAdapter(types);                                                                                 land_size_unit.setAdapter(land_size_units);

        upload_1_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_1);*/
                if (askForPermission_1())
                    showChooser_1();
            }
        });
        select_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_1);
            }
        });
        upload_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        upload_2_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_2);*/
                if (askForPermission_2())
                    showChooser_2();
            }
        });
        select_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_2);
            }
        });
        upload_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        upload_3_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_3);*/
                if (askForPermission_3())
                    showChooser_3();
            }
        });
        select_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_3);
            }
        });
        upload_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        upload_4_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_4);*/
                if (askForPermission_4())
                    showChooser_4();
            }
        });
        select_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_4);
            }
        });
        upload_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        upload_5_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_5);*/
                if (askForPermission_5())
                    showChooser_5();
            }
        });
        select_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_5);
            }
        });
        upload_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        address.addTextChangedListener(new TextWatcher() {
            boolean hint;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    // no text, hint is visible
                    hint = true;
                    address.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                } else if(hint) {
                    // no hint, text is visible
                    hint = false;
                    address.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position = parent.getSelectedItemPosition();

                if (position == 1){
                    isLandForSale = true;
                    ad_type.setVisibility(View.GONE);
                    type.setVisibility(View.GONE); scrollView.setVisibility(View.VISIBLE);
                    district_layout.setVisibility(View.VISIBLE);       district_text.setVisibility(View.VISIBLE);         image_layout.setVisibility(View.VISIBLE);
                    land_type_layout.setVisibility(View.VISIBLE);      district.setVisibility(View.VISIBLE);
                    land_type_text.setVisibility(View.VISIBLE);        city_layout.setVisibility(View.VISIBLE);           image_details.setVisibility(View.VISIBLE); land_size_layout.setVisibility(View.VISIBLE);
                    land_type.setVisibility(View.VISIBLE);             city.setVisibility(View.VISIBLE);                  city_text.setVisibility(View.VISIBLE);     land_size.setVisibility(View.VISIBLE);
                    land_size_text.setVisibility(View.VISIBLE);        land_size_unit_layout.setVisibility(View.VISIBLE); land_size_unit.setVisibility(View.VISIBLE); address_layout.setVisibility(View.VISIBLE);
                    address_text.setVisibility(View.VISIBLE);          address.setVisibility(View.VISIBLE);               title_layout.setVisibility(View.VISIBLE);   title_text.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);                 description_layout.setVisibility(View.VISIBLE);    description_text.setVisibility(View.VISIBLE); description.setVisibility(View.VISIBLE);
                    price_layout.setVisibility(View.VISIBLE);          price_text.setVisibility(View.VISIBLE);            price.setVisibility(View.VISIBLE);            land_price_type_layout.setVisibility(View.VISIBLE);
                    price_type_text.setVisibility(View.VISIBLE);       price_unit.setVisibility(View.VISIBLE);            contact_layout.setVisibility(View.VISIBLE);   contact_text.setVisibility(View.VISIBLE);
                    contact_number_layout.setVisibility(View.VISIBLE); contact_number.setVisibility(View.VISIBLE);        details_layout.setVisibility(View.VISIBLE);   detail_text.setVisibility(View.VISIBLE);
                    codePicker.setVisibility(View.VISIBLE);            save.setVisibility(View.VISIBLE);                  land_size_unit_text.setVisibility(View.VISIBLE);

                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                        cityResponse.add("නගරය තෝරන්න");
                    }else {
                        cityResponse.add("Select City");
                    }
                    district.setAdapter(districts);
                    city.setAdapter(cities);

                    land_type.setItems(landTypeArrayList, new MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] booleans) {
                            for(int i=0; i<booleans.length; i++) {
                                if(booleans[i]) {
                                    Log.i("TAG", i + " : "+ landTypeArrayList.get(i));
                                }
                            }
                        }
                    });
                    price_unit.setItems(priceUnitArraylist, new MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] booleans) {
                            for(int i=0; i<booleans.length; i++) {
                                if(booleans[i]) {
                                    Log.i("TAG", i + " : "+ priceUnitArraylist.get(i));
                                }
                            }
                        }
                    });
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!land_size.getText().toString().isEmpty() && !price.getText().toString().isEmpty() && !contact_number.getText().toString().isEmpty()){
                                if (!arrayList.isEmpty()){
                                    /*uploadLandProperty("Land",price_unit.getSelectedItem().toString(),"Land For Sale",land_size.getText().toString()
                                            ,land_size_unit.getSelectedItem().toString(),address.getText().toString());*/
                                    dataUpload();
                                }else {
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                            alert.setTitle(R.string.app_name);
                                            alert.setIcon(R.mipmap.ic_property_logo);
                                            alert.setMessage("කරුණාකර තෝරාගත් ඡායාරූප උඩුගත කරන්න !");
                                            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.create().show();
                                        }else {
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                            alert.setTitle(R.string.app_name);
                                            alert.setIcon(R.mipmap.ic_property_logo);
                                            alert.setMessage("Please upload the selected photos !");
                                            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.create().show();
                                        }
                                    }
                                }
                            }else {
                                if (land_size.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("ඉඩමේ විශාලත්වය හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("The size of the land can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }else if (price.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("මිල හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("Price can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }else if (contact_number.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("දුරකථන අංකය හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("The phone number can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }
                            }
                        }
                    });
                }else if (position == 2){
                    isHouseForSale = true;
                    ad_type.setVisibility(View.GONE);scrollView.setVisibility(View.VISIBLE);
                    type.setVisibility(View.GONE);
                    district_layout.setVisibility(View.VISIBLE);       district_text.setVisibility(View.VISIBLE);         image_layout.setVisibility(View.VISIBLE);
                    district.setVisibility(View.VISIBLE);
                    city_layout.setVisibility(View.VISIBLE);           image_details.setVisibility(View.VISIBLE); land_size_layout.setVisibility(View.VISIBLE);
                    city.setVisibility(View.VISIBLE);                  city_text.setVisibility(View.VISIBLE);     land_size.setVisibility(View.VISIBLE);
                    land_size_text.setVisibility(View.VISIBLE);        land_size_unit_layout.setVisibility(View.VISIBLE); land_size_unit.setVisibility(View.VISIBLE); address_layout.setVisibility(View.VISIBLE);
                    address_text.setVisibility(View.VISIBLE);          address.setVisibility(View.VISIBLE);               title_layout.setVisibility(View.VISIBLE);   title_text.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);                 description_layout.setVisibility(View.VISIBLE);    description_text.setVisibility(View.VISIBLE); description.setVisibility(View.VISIBLE);
                    price_layout.setVisibility(View.VISIBLE);          price_text.setVisibility(View.VISIBLE);            price.setVisibility(View.VISIBLE);
                    contact_layout.setVisibility(View.VISIBLE);   contact_text.setVisibility(View.VISIBLE);
                    contact_number_layout.setVisibility(View.VISIBLE); contact_number.setVisibility(View.VISIBLE);        details_layout.setVisibility(View.VISIBLE);   detail_text.setVisibility(View.VISIBLE);
                    codePicker.setVisibility(View.VISIBLE);            save.setVisibility(View.VISIBLE);                  land_size_unit_text.setVisibility(View.VISIBLE); house_size_layout.setVisibility(View.VISIBLE);
                    house_size.setVisibility(View.VISIBLE);           house_size_text.setVisibility(View.VISIBLE);        bed_layout.setVisibility(View.VISIBLE);          bed.setVisibility(View.VISIBLE);
                    bed_text.setVisibility(View.VISIBLE);             bath_layout.setVisibility(View.VISIBLE);            bath.setVisibility(View.VISIBLE);               bath_text.setVisibility(View.VISIBLE);

                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                        cityResponse.add("නගරය තෝරන්න");
                    }else {
                        cityResponse.add("Select City");
                    }
                    district.setAdapter(districts);
                    city.setAdapter(cities);

                    bed.setAdapter(beds);
                    bath.setAdapter(baths);

                    land_type.setItems(landTypeArrayList, new MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] booleans) {
                            for(int i=0; i<booleans.length; i++) {
                                if(booleans[i]) {
                                    Log.i("TAG", i + " : "+ landTypeArrayList.get(i));
                                }
                            }
                        }
                    });
                    price_unit.setItems(priceUnitArraylist, new MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] booleans) {
                            for(int i=0; i<booleans.length; i++) {
                                if(booleans[i]) {
                                    Log.i("TAG", i + " : "+ priceUnitArraylist.get(i));
                                }
                            }
                        }
                    });
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!land_size.getText().toString().isEmpty() && !price.getText().toString().isEmpty()
                                    && !house_size.getText().toString().isEmpty() && !contact_number.getText().toString().isEmpty()){
                                if (!arrayList.isEmpty()){
                                    /*uploadHouseProperty("House",bed.getSelectedItem().toString(),bath.getSelectedItem()
                                                    .toString(),house_size.getText().toString(),"House For Sale",address.getText().toString(),
                                            land_size.getText().toString(),land_size_unit.getSelectedItem().toString());*/
                                    dataUpload();
                                }else {
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                            alert.setTitle(R.string.app_name);
                                            alert.setIcon(R.mipmap.ic_property_logo);
                                            alert.setMessage("කරුණාකර තෝරාගත් ඡායාරූප උඩුගත කරන්න !");
                                            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.create().show();
                                        }else {
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                            alert.setTitle(R.string.app_name);
                                            alert.setIcon(R.mipmap.ic_property_logo);
                                            alert.setMessage("Please upload the selected photos !");
                                            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.create().show();
                                        }
                                    }
                                }
                            }else {
                                if (land_size.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("ඉඩමේ විශාලත්වය හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("The size of the land can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }else if (price.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("මිල හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("Price can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }else if (contact_number.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("දුරකථන අංකය හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("The phone number can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }else if (house_size.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("නිවසේ විශාලත්වය හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("The size of the house can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }
                                else if (image_urls.isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                            alert.setTitle(R.string.app_name);
                                            alert.setIcon(R.mipmap.ic_property_logo);
                                            alert.setMessage("කරුණාකර තෝරාගත් ඡායාරූප උඩුගත කරන්න !");
                                            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.create().show();
                                        }else {
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                            alert.setTitle(R.string.app_name);
                                            alert.setIcon(R.mipmap.ic_property_logo);
                                            alert.setMessage("Please upload the selected photos !");
                                            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.create().show();
                                        }
                                    }
                                }
                            }
                        }
                    });
                }else if (position == 3){
                    isLandForRent = true;
                    ad_type.setVisibility(View.GONE);scrollView.setVisibility(View.VISIBLE);
                    type.setVisibility(View.GONE);
                    rent.setVisibility(View.VISIBLE); rent_layout.setVisibility(View.VISIBLE); rent_text.setVisibility(View.VISIBLE);
                    district_layout.setVisibility(View.VISIBLE);       district_text.setVisibility(View.VISIBLE);         image_layout.setVisibility(View.VISIBLE);
                    land_type_layout.setVisibility(View.VISIBLE);      district.setVisibility(View.VISIBLE);
                    land_type_text.setVisibility(View.VISIBLE);        city_layout.setVisibility(View.VISIBLE);           image_details.setVisibility(View.VISIBLE); land_size_layout.setVisibility(View.VISIBLE);
                    land_type.setVisibility(View.VISIBLE);             city.setVisibility(View.VISIBLE);                  city_text.setVisibility(View.VISIBLE);     land_size.setVisibility(View.VISIBLE);
                    land_size_text.setVisibility(View.VISIBLE);        land_size_unit_layout.setVisibility(View.VISIBLE); land_size_unit.setVisibility(View.VISIBLE); address_layout.setVisibility(View.VISIBLE);
                    address_text.setVisibility(View.VISIBLE);          address.setVisibility(View.VISIBLE);               title_layout.setVisibility(View.VISIBLE);   title_text.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);                 description_layout.setVisibility(View.VISIBLE);    description_text.setVisibility(View.VISIBLE); description.setVisibility(View.VISIBLE);
                    contact_layout.setVisibility(View.VISIBLE);   contact_text.setVisibility(View.VISIBLE);
                    contact_number_layout.setVisibility(View.VISIBLE); contact_number.setVisibility(View.VISIBLE);        details_layout.setVisibility(View.VISIBLE);   detail_text.setVisibility(View.VISIBLE);
                    codePicker.setVisibility(View.VISIBLE);            save.setVisibility(View.VISIBLE);                  land_size_unit_text.setVisibility(View.VISIBLE);

                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                        cityResponse.add("නගරය තෝරන්න");
                    }else {
                        cityResponse.add("Select City");
                    }
                    district.setAdapter(districts);
                    city.setAdapter(cities);

                    land_type.setItems(landTypeArrayList, new MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] booleans) {
                            for(int i=0; i<booleans.length; i++) {
                                if(booleans[i]) {
                                    Log.i("TAG", i + " : "+ landTypeArrayList.get(i));
                                }
                            }
                        }
                    });
                    price_unit.setItems(priceUnitArraylist, new MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] booleans) {
                            for(int i=0; i<booleans.length; i++) {
                                if(booleans[i]) {
                                    Log.i("TAG", i + " : "+ priceUnitArraylist.get(i));
                                }
                            }
                        }
                    });
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!land_size.getText().toString().isEmpty() && !rent.getText().toString().isEmpty() && !contact_number.getText().toString().isEmpty()){
                                if (!arrayList.isEmpty()){
                                    /*uploadLandRentProperty("Land","Land For Rent Or Lease",
                                            land_size.getText().toString(),land_size_unit.getSelectedItem()
                                                    .toString(),address.getText().toString());*/
                                    dataUpload();
                                }else {
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                            alert.setTitle(R.string.app_name);
                                            alert.setIcon(R.mipmap.ic_property_logo);
                                            alert.setMessage("කරුණාකර තෝරාගත් ඡායාරූප උඩුගත කරන්න !");
                                            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.create().show();
                                        }else {
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                            alert.setTitle(R.string.app_name);
                                            alert.setIcon(R.mipmap.ic_property_logo);
                                            alert.setMessage("Please upload the selected photos !");
                                            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.create().show();
                                        }
                                    }
                                }
                            }else {
                                if (land_size.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("ඉඩමේ විශාලත්වය හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("The size of the land can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }else if (price.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("මිල හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("Price can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }else if (contact_number.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("දුරකථන අංකය හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("The phone number can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }
                            }
                        }
                    });
                }else if (position == 4){
                    isHouseForRent = true;
                    ad_type.setVisibility(View.GONE);scrollView.setVisibility(View.VISIBLE);
                    type.setVisibility(View.GONE);
                    district_layout.setVisibility(View.VISIBLE);       district_text.setVisibility(View.VISIBLE);         image_layout.setVisibility(View.VISIBLE);
                    district.setVisibility(View.VISIBLE);
                    city_layout.setVisibility(View.VISIBLE);           image_details.setVisibility(View.VISIBLE); land_size_layout.setVisibility(View.VISIBLE);
                    city.setVisibility(View.VISIBLE);                  city_text.setVisibility(View.VISIBLE);     land_size.setVisibility(View.VISIBLE);
                    land_size_text.setVisibility(View.VISIBLE);        land_size_unit_layout.setVisibility(View.VISIBLE); land_size_unit.setVisibility(View.VISIBLE); address_layout.setVisibility(View.VISIBLE);
                    address_text.setVisibility(View.VISIBLE);          address.setVisibility(View.VISIBLE);               title_layout.setVisibility(View.VISIBLE);   title_text.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);                 description_layout.setVisibility(View.VISIBLE);    description_text.setVisibility(View.VISIBLE); description.setVisibility(View.VISIBLE);
                    rent_layout.setVisibility(View.VISIBLE);          rent_text.setVisibility(View.VISIBLE);            rent.setVisibility(View.VISIBLE);
                    contact_layout.setVisibility(View.VISIBLE);   contact_text.setVisibility(View.VISIBLE);
                    contact_number_layout.setVisibility(View.VISIBLE); contact_number.setVisibility(View.VISIBLE);        details_layout.setVisibility(View.VISIBLE);   detail_text.setVisibility(View.VISIBLE);
                    codePicker.setVisibility(View.VISIBLE);            save.setVisibility(View.VISIBLE);                  land_size_unit_text.setVisibility(View.VISIBLE); house_size_layout.setVisibility(View.VISIBLE);
                    house_size.setVisibility(View.VISIBLE);           house_size_text.setVisibility(View.VISIBLE);        bed_layout.setVisibility(View.VISIBLE);          bed.setVisibility(View.VISIBLE);
                    bed_text.setVisibility(View.VISIBLE);             bath_layout.setVisibility(View.VISIBLE);            bath.setVisibility(View.VISIBLE);               bath_text.setVisibility(View.VISIBLE);

                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                        cityResponse.add("නගරය තෝරන්න");
                    }else {
                        cityResponse.add("Select City");
                    }
                    district.setAdapter(districts);
                    city.setAdapter(cities);

                    rent_text.setText("Rent (/Month)(Rs)");
                    rent.setHint("Rent (/Month)(Rs)");
                    bed.setAdapter(beds);
                    bath.setAdapter(baths);

                    land_type.setItems(landTypeArrayList, new MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] booleans) {
                            for(int i=0; i<booleans.length; i++) {
                                if(booleans[i]) {
                                    Log.i("TAG", i + " : "+ landTypeArrayList.get(i));
                                }
                            }
                        }
                    });
                    price_unit.setItems(priceUnitArraylist, new MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] booleans) {
                            for(int i=0; i<booleans.length; i++) {
                                if(booleans[i]) {
                                    Log.i("TAG", i + " : "+ priceUnitArraylist.get(i));
                                }
                            }
                        }
                    });
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!land_size.getText().toString().isEmpty() && !rent.getText().toString().isEmpty()
                                    && !contact_number.getText().toString().isEmpty() && !house_size.getText().toString().isEmpty()){
                                if (!arrayList.isEmpty()){
                                    /*uploadHouseRentProperty("House",bed.getSelectedItem().toString(),bath.getSelectedItem()
                                            .toString(),house_size.getText().toString(),"House For Rent Or Lease",address.getText()
                                            .toString(),land_size.getText().toString(),land_size_unit.getSelectedItem().toString());*/
                                    dataUpload();
                                }else {
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                            alert.setTitle(R.string.app_name);
                                            alert.setIcon(R.mipmap.ic_property_logo);
                                            alert.setMessage("කරුණාකර තෝරාගත් ඡායාරූප උඩුගත කරන්න !");
                                            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.create().show();
                                        }else {
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                            alert.setTitle(R.string.app_name);
                                            alert.setIcon(R.mipmap.ic_property_logo);
                                            alert.setMessage("Please upload the selected photos !");
                                            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.create().show();
                                        }
                                    }
                                }
                            }else {

                                if (land_size.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("ඉඩමේ විශාලත්වය හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("The size of the land can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }else if (price.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("මිල හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("Price can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }else if (contact_number.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("දුරකථන අංකය හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("The phone number can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }else if (house_size.getText().toString().isEmpty()){
                                    if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("නිවසේ විශාලත්වය හිස් විය නොහැක !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }else {
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                                        alert.setTitle(R.string.app_name);
                                        alert.setIcon(R.mipmap.ic_property_logo);
                                        alert.setMessage("The size of the house can not be empty !");
                                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.create().show();
                                    }
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        arrayList = new ArrayList<>();

        select_1.setVisibility(View.GONE);
        select_2.setVisibility(View.GONE);
        select_3.setVisibility(View.GONE);
        select_4.setVisibility(View.GONE);
        select_5.setVisibility(View.GONE);

        upload_1.setVisibility(View.GONE);
        upload_2.setVisibility(View.GONE);
        upload_3.setVisibility(View.GONE);
        upload_4.setVisibility(View.GONE);
        upload_5.setVisibility(View.GONE);
    }
    private void getDistricts(){
        JsonArrayRequest request = new JsonArrayRequest(Constant.BASE_URL_get_district,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<DistrictResponse> items = new Gson().fromJson(response.toString(), new TypeToken<List<DistrictResponse>>() {
                        }.getType());

                        // adding contacts to contacts list
                        districtResponses.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            districtResponses.add("ප්\u200Dරදේශය තෝරන්න");
                        }else{
                            districtResponses.add("Select District");
                        }
                        for (int i = 0; i < items.size();i++){
                            districtResponses.add(items.get(i).getName_en());
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
        deleteCache(EnglishAddPostActivity.this);
    }
    private void getCities(){
        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position = parent.getSelectedItemPosition();
                cityResponse.clear();

                if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                    cityResponse.add("නගරය තෝරන්න");
                }else {
                    cityResponse.add("Select City");
                }
                if (position > 0){
                    if (position == 1){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Akkarepattu");cities.add("Kalmunai");cities.add("Ampara");cities.add("Sainthamaruthu");
                    }else if (position == 2){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Anuradhapura");cities.add("Kakirawa");cities.add("Thabuttegama");cities.add("Medawachchiya");cities.add("Eppawala");
                        cities.add("Mihintale");cities.add("Nochchiyagama");cities.add("Talawa");cities.add("Galnewa");cities.add("Galenbindunuwewa");
                        cities.add("Habarana");
                    }else if (position == 3){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Badulla");cities.add("Bandarawela");cities.add("Welimada");cities.add("Mahiyanganaya");cities.add("Hali Ela");
                        cities.add("Passara");cities.add("Ella");cities.add("Diyatalawa");cities.add("Haputale");
                    }else if (position == 4){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Batticaloa");

                    }else if (position == 5){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Nugegoda");cities.add("Dehiwala");cities.add("Colombo 6");cities.add("Piliyandala");
                        cities.add("Kottawa");cities.add("Maharagama");cities.add("Rajagiriya");cities.add("Homagama");cities.add("Boralesgamuwa");
                        cities.add("Malabe");cities.add("Moratuwa");cities.add("Colombo 4");cities.add("Colombo 3");cities.add("Battaramulla");cities.add("Colombo 5");
                        cities.add("Colombo 10");cities.add("Athurugiriya");cities.add("Kaduwela");cities.add("Mount Lavinia");
                        cities.add("Colombo 8");cities.add("Colombo 11");cities.add("Colombo 9");cities.add("Ratmalana");cities.add("Pannipitiya");
                        cities.add("Kohuwala");cities.add("Nawala");cities.add("Kotte");cities.add("Colombo 2");cities.add("Wellampitiya");
                        cities.add("Talawatugoda");cities.add("Angoda");cities.add("Colombo 15");cities.add("Colombo 13");cities.add("Kolonnawa");
                        cities.add("Colombo 14");cities.add("Padukka");cities.add("Colombo 12");cities.add("Kesbewa");cities.add("Hanwella");
                        cities.add("Avissawella");cities.add("Colombo 7");cities.add("Colombo 1");
                    }else if (position == 6){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Galle");cities.add("Ambalangoda");cities.add("Hikkaduwa");cities.add("Baddegama");
                        cities.add("Karapitiya");cities.add("Bentota");cities.add("Ahangama");cities.add("Batapola");
                        cities.add("Elpitiya");
                    }else if (position == 7){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Gampaha");cities.add("Negombo");cities.add("Kelaniya");cities.add("Kadawatha");cities.add("Kiribathgoda");
                        cities.add("Ja-Ela");cities.add("Wattala");cities.add("Nittambuwa");
                        cities.add("Minuwangoda");cities.add("Katunayake");cities.add("Kandana");cities.add("Ragama");cities.add("Delgoda");
                        cities.add("Divulapitiya");cities.add("Veyangoda");cities.add("Mirigama");cities.add("Ganemulla");
                    }else if (position == 8){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Tangalla");cities.add("Beliatta");cities.add("Ambalantota");cities.add("Tissamaharama");
                        cities.add("Hambantota");
                    }else if (position == 9){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Jaffna");cities.add("Nallur");cities.add("Chavakachcheri");
                    }else if (position == 10){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Horana");cities.add("Kalutara");cities.add("Panadura");cities.add("Bandaragama");
                        cities.add("Matugama");cities.add("Wadduwa");cities.add("Aluthgama");cities.add("Beruwala ");
                        cities.add("Ingiriya");
                    }else if (position == 11){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Kandy");cities.add("Katugastota");cities.add("Peradeniya");
                        cities.add("Gampola");cities.add("Kundasale");cities.add("Akurana");
                        cities.add("Pilimatalawa");cities.add("Digana");cities.add("Gelioya");
                        cities.add("Nawalapitiya");cities.add("Kadugannawa");cities.add("Wattegama");
                        cities.add("Galagedara");cities.add("Madawala Bazaar");cities.add("Ampitiya");

                    }else if (position == 12){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Kegalle");cities.add("Mawanella");cities.add("Warakapola ");cities.add("Rambukkana");cities.add("Ruwanwella");cities.add("Galigamuwa");
                        cities.add("Dehiowita");cities.add("Yatiyantota");cities.add("Deraniyagala");cities.add("Kithulgala");
                    }else if (position == 13){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Kilinochchi");
                    }else if (position == 14){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Kurunegala");cities.add("Kuliyapitiya");cities.add("Pannala");cities.add("Narammala");
                        cities.add("Wariyapola");cities.add("Mawathagama");cities.add("Polgahawela");cities.add("Ibbagamuwa");
                        cities.add("Alawwa");cities.add("Giriulla");cities.add("Hettipola");cities.add("Nikaweratiya");
                        cities.add("Bingiriya");cities.add("Galgamuwa");
                    }else if (position == 15){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Mannar");
                    }else if (position == 16){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Matale");cities.add("Dambulla");cities.add("Galewela");cities.add("Ukuwela");
                        cities.add("Sigiriya");cities.add("Rattota");cities.add("Palapathwela");cities.add("Yatawatta");

                    }else if (position == 17){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Matara");cities.add("Akuressa");cities.add("Weligama");
                        cities.add("Hakmana");cities.add("Dikwella");cities.add("Kamburupitiya");
                        cities.add("Deniyaya");cities.add("Kamburugamuwa");cities.add("Kekanadurra");

                    }else if (position == 18){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Moneragala");cities.add("Bibile");cities.add("Wellawaya");cities.add("Buttala");cities.add("Kataragama");

                    }else if (position == 19){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Mullativu");

                    }else if (position == 20){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Nuwara Eliya");cities.add("Hatton");cities.add("Ginigathena");cities.add("Madulla");

                    }else if (position == 21){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Polonnaruwa");cities.add("Hingurakgoda");cities.add("Kaduruwela");cities.add("Medirigiriya");

                    }else if (position == 22){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Chilaw");cities.add("Wennappuwa");cities.add("Puttalan");
                        cities.add("Nattandiya");cities.add("Marawila");cities.add("Dankotuwa");

                    }else if (position == 23){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Ratnapura");cities.add("Embilipitiya");cities.add("Balangoda");
                        cities.add("Pelmadulla");cities.add("Eheliyagoda");cities.add("Kuruwita");

                    }else if (position == 24){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Trincomalee");
                        cities.add("Kinniya");

                    }else if (position == 25){
                        cities.clear();
                        if (getIntent().getStringExtra("language").equalsIgnoreCase("Sinhala")){
                            cities.add("නගරය තෝරන්න");
                        }else {
                            cities.add("Select City");
                        }
                        cities.add("Vavuniya");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.err.println("Request : " + requestCode);
        if (requestCode == PICK_IMAGE_REQUEST_1 && resultCode == RESULT_OK && data != null && data.getData() != null) {// If the file selection was successful
            final Uri uri = data.getData();
            System.err.println("Uri : " + uri);

            Log.i(TAG, "Uri = " + Objects.requireNonNull(uri).toString());
            try {
                // Get the file path from the URI
                final String path = FileUtils.getPath(this, uri);
                Log.d("Single File Selected", path);

                arrayList.add(uri);
                Glide.with(EnglishAddPostActivity.this)
                        .asBitmap()
                        .load(uri)
                        .into(select_image_1);
            } catch (Exception e) {
                Log.e(TAG, "File select error", e);
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST_2 && resultCode == RESULT_OK && data != null && data.getData() != null) {// If the file selection was successful
            //do something with the image (save it to some directory or whatever you need to do with it here)
            final Uri uri = data.getData();
            Log.i(TAG, "Uri = " + uri.toString());
            try {
                // Get the file path from the URI
                final String path = FileUtils.getPath(this, uri);
                Log.d("Single File Selected", path);

                arrayList.add(uri);
                Glide.with(EnglishAddPostActivity.this)
                        .asBitmap()
                        .load(uri)
                        .into(select_image_2);
            } catch (Exception e) {
                Log.e(TAG, "File select error", e);
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST_3 && resultCode == RESULT_OK && data != null && data.getData() != null) {// If the file selection was successful
            //do something with the image (save it to some directory or whatever you need to do with it here)
            final Uri uri = data.getData();
            Log.i(TAG, "Uri = " + uri.toString());
            try {
                // Get the file path from the URI
                final String path = FileUtils.getPath(this, uri);
                Log.d("Single File Selected", path);

                arrayList.add(uri);
                Glide.with(EnglishAddPostActivity.this)
                        .asBitmap()
                        .load(uri)
                        .into(select_image_3);
            } catch (Exception e) {
                Log.e(TAG, "File select error", e);
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST_4 && resultCode == RESULT_OK && data != null && data.getData() != null) {// If the file selection was successful
            //do something with the image (save it to some directory or whatever you need to do with it here)
            final Uri uri = data.getData();
            Log.i(TAG, "Uri = " + uri.toString());
            try {
                // Get the file path from the URI
                final String path = FileUtils.getPath(this, uri);
                Log.d("Single File Selected", path);

                arrayList.add(uri);
                Glide.with(EnglishAddPostActivity.this)
                        .asBitmap()
                        .load(uri)
                        .into(select_image_4);
            } catch (Exception e) {
                Log.e(TAG, "File select error", e);
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST_5 && resultCode == RESULT_OK && data != null && data.getData() != null) {// If the file selection was successful
            //do something with the image (save it to some directory or whatever you need to do with it here)
            final Uri uri = data.getData();
            Log.i(TAG, "Uri = " + uri.toString());
            try {
                // Get the file path from the URI
                final String path = FileUtils.getPath(this, uri);
                Log.d("Single File Selected", path);

                arrayList.add(uri);
                Glide.with(EnglishAddPostActivity.this)
                        .asBitmap()
                        .load(uri)
                        .into(select_image_5);
            } catch (Exception e) {
                Log.e(TAG, "File select error", e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private boolean askForPermission_1() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            int hasCallPermission = ContextCompat.checkSelfPermission(EnglishAddPostActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                // need to request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(EnglishAddPostActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // explain
                    showMessageOKCancel(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(EnglishAddPostActivity.this,
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            Constant.REQUEST_CODE_ASK_PERMISSIONS_1);
                                }
                            });
                    // if denied then working here
                } else {
                    // Request for permission
                    ActivityCompat.requestPermissions(EnglishAddPostActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.REQUEST_CODE_ASK_PERMISSIONS_1);
                }

                return false;
            } else {
                // permission granted and calling function working
                return true;
            }
        } else {
            return true;
        }
    }
    private boolean askForPermission_2() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            int hasCallPermission = ContextCompat.checkSelfPermission(EnglishAddPostActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                // need to request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(EnglishAddPostActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // explain
                    showMessageOKCancel(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(EnglishAddPostActivity.this,
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            Constant.REQUEST_CODE_ASK_PERMISSIONS_2);
                                }
                            });
                    // if denied then working here
                } else {
                    // Request for permission
                    ActivityCompat.requestPermissions(EnglishAddPostActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.REQUEST_CODE_ASK_PERMISSIONS_2);
                }

                return false;
            } else {
                // permission granted and calling function working
                return true;
            }
        } else {
            return true;
        }
    }
    private boolean askForPermission_3() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            int hasCallPermission = ContextCompat.checkSelfPermission(EnglishAddPostActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                // need to request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(EnglishAddPostActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // explain
                    showMessageOKCancel(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(EnglishAddPostActivity.this,
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            Constant.REQUEST_CODE_ASK_PERMISSIONS_3);
                                }
                            });
                    // if denied then working here
                } else {
                    // Request for permission
                    ActivityCompat.requestPermissions(EnglishAddPostActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.REQUEST_CODE_ASK_PERMISSIONS_3);
                }

                return false;
            } else {
                // permission granted and calling function working
                return true;
            }
        } else {
            return true;
        }
    }
    private boolean askForPermission_4() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            int hasCallPermission = ContextCompat.checkSelfPermission(EnglishAddPostActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                // need to request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(EnglishAddPostActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // explain
                    showMessageOKCancel(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(EnglishAddPostActivity.this,
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            Constant.REQUEST_CODE_ASK_PERMISSIONS_4);
                                }
                            });
                    // if denied then working here
                } else {
                    // Request for permission
                    ActivityCompat.requestPermissions(EnglishAddPostActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.REQUEST_CODE_ASK_PERMISSIONS_4);
                }

                return false;
            } else {
                // permission granted and calling function working
                return true;
            }
        } else {
            return true;
        }
    }
    private boolean askForPermission_5() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            int hasCallPermission = ContextCompat.checkSelfPermission(EnglishAddPostActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                // need to request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(EnglishAddPostActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // explain
                    showMessageOKCancel(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(EnglishAddPostActivity.this,
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            Constant.REQUEST_CODE_ASK_PERMISSIONS_5);
                                }
                            });
                    // if denied then working here
                } else {
                    // Request for permission
                    ActivityCompat.requestPermissions(EnglishAddPostActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.REQUEST_CODE_ASK_PERMISSIONS_5);
                }

                return false;
            } else {
                // permission granted and calling function working
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                showChooser_1();
            } else {
                // Permission Denied
                Toast.makeText(EnglishAddPostActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                showChooser_1();
            } else {
                // Permission Denied
                Toast.makeText(EnglishAddPostActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                showChooser_1();
            } else {
                // Permission Denied
                Toast.makeText(EnglishAddPostActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_4) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                showChooser_1();
            } else {
                // Permission Denied
                Toast.makeText(EnglishAddPostActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_5) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                showChooser_1();
            } else {
                // Permission Denied
                Toast.makeText(EnglishAddPostActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showChooser_1() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, PICK_IMAGE_REQUEST_1);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }
    private void showChooser_2() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, PICK_IMAGE_REQUEST_2);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }
    private void showChooser_3() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, PICK_IMAGE_REQUEST_3);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }
    private void showChooser_4() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, PICK_IMAGE_REQUEST_4);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }
    private void showChooser_5() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, PICK_IMAGE_REQUEST_5);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EnglishAddPostActivity.this);
        final android.support.v7.app.AlertDialog dialog = builder.setMessage("You need to grant access to Read External Storage")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(
                        ContextCompat.getColor(EnglishAddPostActivity.this, android.R.color.holo_blue_light));
                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        ContextCompat.getColor(EnglishAddPostActivity.this, android.R.color.holo_red_light));
            }
        });

        dialog.show();

    }
    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(Objects.requireNonNull(getContentResolver().getType(fileUri))),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EnglishAddPostActivity.this, MainActivity.class);
        intent.putExtra("language",getIntent().getStringExtra("language"));
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            Intent intent = new Intent(EnglishAddPostActivity.this,MainActivity.class);
            intent.putExtra("language",getIntent().getStringExtra("language"));
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void dataUpload(){
        if (InternetConnection.checkConnection(EnglishAddPostActivity.this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://depalamobileapp.com/Ryan/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            //showProgress();

            // create list of file parts (photo, video, ...)
            final List<MultipartBody.Part> parts = new ArrayList<>();

            // create upload service client
            ApiService service = retrofit.create(ApiService.class);

            if (arrayList != null) {
                // create part for file (photo, video, ...)
                for (int i = 0; i < arrayList.size(); i++) {
                    parts.add(prepareFilePart("image"+i, arrayList.get(i)));
                }
            }

            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
            String date = mdformat.format(calendar.getTime());

            // create a map of data to pass along
            RequestBody descriptions = createPartFromString("www.depalamobileapp.com.com");
            RequestBody size = createPartFromString(""+parts.size());
            RequestBody status = createPartFromString("TRUE");
            RequestBody title_s = null;
            RequestBody districts = createPartFromString(district.getSelectedItem().toString());
            RequestBody city_s = createPartFromString(city.getSelectedItem().toString());
            RequestBody address_s = createPartFromString(address.getText().toString());
            RequestBody category_s = null;
            RequestBody property_type  = null;
            RequestBody land_type_s = null;
            RequestBody land_size_s = null;
            RequestBody house_size_s = null;
            RequestBody bed_s = null;
            RequestBody bath_s = null;
            RequestBody description_s = createPartFromString(description.getText().toString());
            RequestBody prices = null;
            RequestBody contacts = createPartFromString("+"+codePicker.getSelectedCountryCode() + contact_number.getText().toString());
            RequestBody UID = createPartFromString(android_id);
            RequestBody post = createPartFromString(date);
            if (isLandForSale){
                title_s = createPartFromString(title.getText().toString().trim());
                category_s = createPartFromString("Land");
                property_type = createPartFromString("Land For Sale");
                land_type_s = createPartFromString(land_type.getSelectedItem().toString());
                land_size_s = createPartFromString(land_size.getText().toString() + " " + land_size_unit.getSelectedItem().toString());
                house_size_s = createPartFromString("NULL");
                bed_s = createPartFromString("NULL");
                bath_s = createPartFromString("NULL");
                prices = createPartFromString(price.getText().toString()  + "/" + price_unit.getSelectedItem().toString());
            }
            if (isHouseForSale){
                title_s = createPartFromString(title.getText().toString().trim());
                category_s = createPartFromString("House");
                property_type = createPartFromString("House For Sale");
                land_size_s = createPartFromString(land_size.getText().toString() + " " + land_size_unit.getSelectedItem().toString());
                land_type_s = createPartFromString("NULL");
                house_size_s = createPartFromString(house_size.getText().toString()+ "sqft");
                bed_s = createPartFromString(bed.getSelectedItem().toString());
                bath_s = createPartFromString(bath.getSelectedItem().toString());
                prices = createPartFromString(price.getText().toString());
            }
            if (isLandForRent){
                title_s = createPartFromString(title.getText().toString().trim());
                category_s = createPartFromString("Land");
                property_type = createPartFromString("Land For Rent Or Lease");
                land_type_s = createPartFromString(land_type.getSelectedItem().toString());
                land_size_s = createPartFromString(land_size.getText().toString() + " " + land_size_unit.getSelectedItem().toString());
                house_size_s = createPartFromString("NULL");
                bed_s = createPartFromString("NULL");
                bath_s = createPartFromString("NULL");
                prices = createPartFromString(price.getText().toString() + "/ year");
            }
            if (isHouseForRent){
                title_s = createPartFromString(title.getText().toString().trim());
                category_s = createPartFromString("House");
                property_type = createPartFromString("House For Rent Or Lease");
                land_type_s = createPartFromString("NULL");
                land_size_s = createPartFromString(land_size.getText().toString() + " " + land_size_unit.getSelectedItem().toString());
                house_size_s = createPartFromString(house_size.getText().toString() + "sqft");
                bed_s = createPartFromString(bed.getSelectedItem().toString());
                bath_s = createPartFromString(bath.getSelectedItem().toString());
                prices = createPartFromString(price.getText().toString() + "/ month");
            }
            // finally, execute the request
            Call<PropertyResponse> call = service.uploadMultiple(descriptions, size,status,title_s,districts,city_s,address_s,category_s,property_type,land_type_s,land_size_s,house_size_s,bed_s,bath_s,description_s,prices,contacts,UID,post,parts);
            final ProgressDialog progress = new ProgressDialog(EnglishAddPostActivity.this);
            progress.setTitle(getString(R.string.app_name));
            progress.setIcon(R.mipmap.ic_property_logo);
            progress.setMessage("Data is Uploading !");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            call.enqueue(new Callback<PropertyResponse>() {
                @Override
                public void onResponse(@NonNull Call<PropertyResponse> call, @NonNull retrofit2.Response<PropertyResponse> response) {
                    //hideProgress();
                    if(response.isSuccessful()) {
                        /*Toast.makeText(EnglishNewPostAdsActivity.this,
                                "Images successfully uploaded!", Toast.LENGTH_SHORT).show();*/
                        progress.dismiss();
                        alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                        alert.setIcon(R.mipmap.ic_property_logo);
                        alert.setTitle(getString(R.string.app_name));
                        alert.setMessage("Delivering a notice is a success. Once the Admin Board approved, your ad will be published in the app. Thank you !.");
                        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(EnglishAddPostActivity.this,MainActivity.class);
                                intent.putExtra("language",getIntent().getStringExtra("language"));
                                startActivity(intent);
                                finish();
                            }
                        });
                        alert.create().show();
                        arrayList.clear();

                        description.setText("");
                        price.setText("");
                        title.setText("");
                    } else {
                        Snackbar.make(parentView, R.string.string_some_thing_wrong, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PropertyResponse> call, @NonNull Throwable t) {
                    //hideProgress();
                    Snackbar.make(parentView, t.getMessage(), Snackbar.LENGTH_LONG).show();
                    alert = new AlertDialog.Builder(EnglishAddPostActivity.this);
                    alert.setTitle("වාහන");
                    alert.setMessage("Upload Failed Try again !.");
                    alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.create().show();
                }
            });

        } else {
            //hideProgress();
            Toast.makeText(EnglishAddPostActivity.this,
                    R.string.string_internet_connection_not_available, Toast.LENGTH_SHORT).show();
        }
    }
}
