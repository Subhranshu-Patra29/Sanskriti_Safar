package com.subha.sanskritisafar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class DescriptionFragment extends Fragment {

    private final String long_description;

    public DescriptionFragment(String long_description)
    {
        this.long_description = long_description;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);

        // Placeholder for the description content
        TextView description = view.findViewById(R.id.description_text);
        description.setText(long_description);

        return view;
    }
}

