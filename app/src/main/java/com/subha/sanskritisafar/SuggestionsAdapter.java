package com.subha.sanskritisafar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder> {

    private ArrayList<String> suggestionsList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String suggestion);
    }

    public SuggestionsAdapter(ArrayList<String> suggestionsList, OnItemClickListener onItemClickListener) {
        this.suggestionsList = suggestionsList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        String suggestion = suggestionsList.get(position);
        holder.suggestionText.setText(suggestion);
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(suggestion));
    }

    @Override
    public int getItemCount() {
        return suggestionsList.size();
    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView suggestionText;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            suggestionText = itemView.findViewById(R.id.suggestion_text);
        }
    }
}

