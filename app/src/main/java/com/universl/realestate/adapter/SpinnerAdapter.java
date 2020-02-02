package com.universl.realestate.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.universl.realestate.R;
import com.universl.realestate.utils.ItemData;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<ItemData> {
    private int group_id;
    Activity context;
    private ArrayList<ItemData> list;
    private LayoutInflater layoutInflater;

    public SpinnerAdapter(Activity context,int group_id,int id,ArrayList<ItemData>list){
        super(context,id,list);
        this.list = list;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.group_id = group_id;
    }
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        @SuppressLint("ViewHolder") View itemView = layoutInflater.inflate(group_id,parent,false);
        ImageView imageView = itemView.findViewById(R.id.image_id);
        imageView.setImageResource(list.get(position).getImage_id());
        TextView textView = itemView.findViewById(R.id.body_type_id);
        textView.setText(list.get(position).getText());
        return itemView;
    }
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent){
        return getView(position,convertView,parent);
    }

    public ItemData getList(int position) {
        return list.get(position);
    }
}
