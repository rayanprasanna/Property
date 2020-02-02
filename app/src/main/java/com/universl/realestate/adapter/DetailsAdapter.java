package com.universl.realestate.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.universl.realestate.R;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    private List<String> imageUrl;
    private Context mContext;

    public DetailsAdapter(Context mContext,List<String> imageUrl){
        this.mContext = mContext;
        this.imageUrl = imageUrl;
    }
    @NonNull
    @Override
    public DetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_folder,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsAdapter.ViewHolder viewHolder, int i) {
        GlideApp.with(mContext)
                .asBitmap()
                .load(imageUrl.get(i))
                .into(viewHolder.photo);

    }

    @Override
    public int getItemCount() {
        return imageUrl.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;

        ImageView image;
        PhotoView photo;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            photo = mView.findViewById(R.id.photoView);
        }
    }
}
