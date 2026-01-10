package com.npdep.npps4;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StatusFragment extends Fragment {

    private Bridge bridge;
    private boolean stopChoreographerLoop = false;

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
    public void onDestroyView() {
        stopChoreographerLoop = true;
        super.onDestroyView();
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
            ContextCompat.startForegroundService(bridge.activity, intent);
        });

        TextView[] textStatus = new TextView[]{
                view.findViewById(R.id.textView),
                view.findViewById(R.id.textView2)
        };

        stopChoreographerLoop = false;
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long l) {
                if (stopChoreographerLoop) {
                    return;
                }

                int serverActive = bridge.binder != null ? 1 : 0;
                if (textStatus[serverActive].getVisibility() == View.GONE) {
                    textStatus[serverActive].setVisibility(View.VISIBLE);
                    textStatus[1 - serverActive].setVisibility(View.GONE);
                }

                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }
}
