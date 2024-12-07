package com.subha.sanskritisafar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArObjectsFragment extends Fragment {
    private ArrayList<String> insideObjects;
    private ArrayList<String> outsideObjects;

    public ArObjectsFragment() {
    }

    public static ArObjectsFragment newInstance(ArrayList<String> insideObjects, ArrayList<String> outsideObjects) {
        ArObjectsFragment fragment = new ArObjectsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("insideObjects", insideObjects);
        args.putStringArrayList("outsideObjects", outsideObjects);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            insideObjects = getArguments().getStringArrayList("insideObjects");
            outsideObjects = getArguments().getStringArrayList("outsideObjects");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ar_objects, container, false);

        RecyclerView insideRecyclerView = view.findViewById(R.id.inside_objects_list);
        RecyclerView outsideRecyclerView = view.findViewById(R.id.outside_objects_list);

        insideRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        outsideRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> insideObjects1 = insideObjects;
        List<String> outsideObjects1 = outsideObjects;

        insideRecyclerView.setAdapter(new ObjectsAdapter(insideObjects1, getContext()));
        outsideRecyclerView.setAdapter(new ObjectsAdapter(outsideObjects1, getContext()));
        return view;
    }
}