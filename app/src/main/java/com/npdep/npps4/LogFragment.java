package com.npdep.npps4;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LogFragment extends Fragment {

    private Bridge bridge;

    public LogFragment() {
        // Required empty public constructor
    }

    public static LogFragment newInstance(Bridge bridge) {
        LogFragment fragment = new LogFragment();
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
        return inflater.inflate(R.layout.fragment_log, container, false);
    }
}