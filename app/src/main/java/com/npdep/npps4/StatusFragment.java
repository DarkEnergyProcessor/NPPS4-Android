package com.npdep.npps4;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {

    private Bridge bridge;

    public StatusFragment() {
        // Required empty public constructor
    }

    public static StatusFragment newInstance(Bridge bridge) {
        StatusFragment fragment = new StatusFragment();
        fragment.bridge = bridge;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.<Button>findViewById(R.id.button).setOnClickListener(view1 -> {
            Intent intent = new Intent(bridge.activity, NPPS4Service.class);
            bridge.activity.startForegroundService(intent);
        });
    }
}