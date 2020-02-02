package com.universl.realestate.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.universl.realestate.R;
import com.universl.realestate.response.HitAdsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HitAdsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<HitAdsResponse> hitAdsResponses;


    public HitAdsAdapter(Context context, List<HitAdsResponse> hitAdsResponses) {
        this.context = context;
        this.hitAdsResponses = hitAdsResponses;
        layoutInflater = LayoutInflater.from(context);
    }

    public class ViewHolder{
        TextView title,description,contact;
        LinearLayout linearLayout;
    }
    @Override
    public int getCount() {
        return hitAdsResponses.size();
    }

    @Override
    public Object getItem(int position) {
        return hitAdsResponses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.hit_ads_list, null);
            // Locate the TextViews in listView_item.xml
            viewHolder.title = view.findViewById(R.id.title);
            viewHolder.description = view.findViewById(R.id.description);
            viewHolder.contact = view.findViewById(R.id.contact);
            viewHolder.linearLayout = view.findViewById(R.id.call_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.title.setText(hitAdsResponses.get(position).title);
        viewHolder.description.setText(hitAdsResponses.get(position).description);
        viewHolder.contact.setText(hitAdsResponses.get(position).contact);
        viewHolder.contact.setPaintFlags(viewHolder.contact.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+hitAdsResponses.get(position).contact));
                ((Activity)context).startActivity(intent);
            }
        });
        return view;
    }
}
