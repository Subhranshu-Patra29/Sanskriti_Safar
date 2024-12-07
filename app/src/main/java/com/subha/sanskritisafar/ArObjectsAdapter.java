package com.subha.sanskritisafar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ArObjectsAdapter extends RecyclerView.Adapter<ArObjectsAdapter.ArObjectViewHolder> {

    private final List<String> arObjects;
    private final OnItemClickListener clickListener;

    public ArObjectsAdapter(List<String> arObjects, OnItemClickListener clickListener) {
        this.arObjects = arObjects;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ArObjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ArObjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArObjectViewHolder holder, int position) {
        String arObject = arObjects.get(position);
        holder.textView.setText(arObject);

        // Handle click event
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(arObject));
    }

    @Override
    public int getItemCount() {
        return arObjects.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String arObject);
    }

    public static class ArObjectViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ArObjectViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}

