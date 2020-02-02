package com.universl.realestate.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.universl.realestate.DisplayActivity;
import com.universl.realestate.R;
import com.universl.realestate.response.PropertyResponse;
import com.universl.realestate.utils.AppController;
import com.universl.realestate.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainAdsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<PropertyResponse> propertyResponses;
    private List<PropertyResponse> image_propertyResponses;
    private ArrayList<String> image_pathList;
    private ArrayList<PropertyResponse> propertyResponseArrayList;
    private String language;
    //private String androidId;
    private DatabaseReference databaseQuotes;

    private ArrayList<String> getImage_pathList() {
        return image_pathList;
    }

    public MainAdsAdapter() {
    }

    @SuppressLint("HardwareIds")
    public MainAdsAdapter(Context context, List<PropertyResponse> propertyResponses, List<PropertyResponse> image_propertyResponses, ArrayList<String> image_pathList, String language) {
        this.context = context;
        this.propertyResponses = propertyResponses;
        this.image_pathList = image_pathList;
        this.image_propertyResponses = image_propertyResponses;
        this.propertyResponseArrayList = new ArrayList<>();
        propertyResponseArrayList.addAll(propertyResponses);
        layoutInflater = LayoutInflater.from(context);
        this.language = language;
        /*androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
    }

    public class ViewHolder{
        TextView title,publish_date,count_of_favorite;
        ImageView image,favorite;
        Boolean isClickFavoriteButton;
    }

    @Override
    public int getCount() {
        return propertyResponses.size();
    }

    @Override
    public Object getItem(int position) {
        return propertyResponses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.ads_list, null);
            // Locate the TextViews in listView_item.xml

            databaseQuotes = FirebaseDatabase.getInstance().getReference("favorite_property");
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.publish_date = convertView.findViewById(R.id.date);
            viewHolder.image = convertView.findViewById(R.id.quotes_image);
            viewHolder.count_of_favorite = convertView.findViewById(R.id.count_of_favorite);
            viewHolder.favorite = convertView.findViewById(R.id.favorite);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.count_of_favorite.setText(String.valueOf(Collections.frequency(getImage_pathList(),propertyResponses.get(position).image_path_1)));

        viewHolder.isClickFavoriteButton = false;
        // Set the results into TextViews
        viewHolder.title.setText("# " + propertyResponses.get(position).title);
        GlideApp.with(context.getApplicationContext()).load(propertyResponses.get(position).image_path_1).fitCenter().into(viewHolder.image);
        viewHolder.publish_date.setText(propertyResponses.get(position).post);
        viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewHolder.isClickFavoriteButton){
                    viewHolder.isClickFavoriteButton = true;
                    viewHolder.favorite.setBackgroundResource(R.drawable.ic_favorite_y);
                    uploadFavorite(propertyResponses.get(position).image_path_1);
                    viewHolder.count_of_favorite.setText(String.valueOf(Integer.parseInt(viewHolder.count_of_favorite.getText().toString()) + 1));
                }else {
                    viewHolder.isClickFavoriteButton = false;
                    viewHolder.favorite.setBackgroundResource(R.drawable.ic_favorite_n);
                }
            }
        });
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayActivity.class);
                intent.putExtra("ads_image",propertyResponses.get(position).image_path_1);
                intent.putExtra("language",language);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        return convertView;
    }
    private void uploadFavorite(String image_path){
        String id = databaseQuotes.push().getKey();
        PropertyResponse response =
                new PropertyResponse(image_path,id);
        assert id != null;
        databaseQuotes.child(id).setValue(response);
    }
}
