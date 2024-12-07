package com.subha.sanskritisafar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TrendingLocationAdapter extends RecyclerView.Adapter<TrendingLocationAdapter.ViewHolder> {

    private Context context;
    private List<TrendingLocation> locationList;
    private final OnItemClickListener listener;

    // Interface for click events
    public interface OnItemClickListener {
        void onItemClick(TrendingLocation location);
    }

    public TrendingLocationAdapter(Context context, List<TrendingLocation> locationList, OnItemClickListener listener) {
        this.context = context;
        this.locationList = locationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trending_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrendingLocation location = locationList.get(position);
        holder.locationTitle.setText(location.getTitle());
        holder.locationDescription.setText(location.getDescription());

        // Load image with Glide
        Glide.with(context).load(location.getImageUrl()).into(holder.locationImage);

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(location));
    }


    @Override
    public int getItemCount() {
        return locationList.size();
    }

    // ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationTitle, locationDescription;
        ImageView locationImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTitle = itemView.findViewById(R.id.location_title);
            locationDescription = itemView.findViewById(R.id.location_description);
            locationImage = itemView.findViewById(R.id.location_image);
        }
    }
}

