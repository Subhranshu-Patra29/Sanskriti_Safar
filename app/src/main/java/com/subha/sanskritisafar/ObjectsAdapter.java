package com.subha.sanskritisafar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ObjectsAdapter extends RecyclerView.Adapter<ObjectsAdapter.ViewHolder> {

    private final List<String> objectsList;
    private final Context context;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public ObjectsAdapter(List<String> objectsList, Context context) {
        this.objectsList = objectsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String objectName = objectsList.get(position);
        holder.textView.setText(objectName);

        // Update background color based on selection
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        // Set OnClickListener for each item
        holder.itemView.setOnClickListener(v -> {
            // Update selected position
            int previousPosition = selectedPosition;
            selectedPosition = position;

            // Notify changes to update background colors
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            Toast.makeText(context, "Selected: " + objectName, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return objectsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}