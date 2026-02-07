package com.npdep.npps4;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.RemoteException;
import android.util.Log;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StatusFragment extends Fragment {
    private boolean stopChoreographerLoop = false;
    private int lastState = NPPS4Service.STATE_STOPPED;

    private final IStateCallbackResult stateCallbackResult = new IStateCallbackResult.Stub() {
        @Override
        public void onStateCallbackResult(int status) {
            lastState = status;
        }
    };

    public StatusFragment() {
        // Required empty public constructor
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
        Bridge bridge = Bridge.getInstance();
        super.onViewCreated(view, savedInstanceState);

        view.<Button>findViewById(R.id.button).setOnClickListener(view1 -> {
            if (bridge.binder == null) {
                return;
            }

            try {
                switch (bridge.binder.getStatus()) {
                    case NPPS4Service.STATE_STOPPED:
                        Intent intent = new Intent(bridge.activity, NPPS4Service.class);
                        ContextCompat.startForegroundService(bridge.activity, intent);
                        break;
                    case NPPS4Service.STATE_RUNNING:
                        bridge.binder.shutdown();
                        break;
                }
            } catch (RemoteException e) {
                Log.e("StatusFragment", "ohno", e);
            }
        });

        stopChoreographerLoop = false;
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long l) {
                if (stopChoreographerLoop) {
                    return;
                }

                Bridge bridge = Bridge.getInstance();
                if (bridge.binder == null) {
                    lastState = NPPS4Service.STATE_STOPPED;
                }

                TextView status = view.findViewById(R.id.textView);
                switch (lastState) {
                    case NPPS4Service.STATE_STOPPED:
                        status.setText("Server Stopped");
                        break;
                    case NPPS4Service.STATE_STARTING:
                        status.setText("Server Starting");
                        break;
                    case NPPS4Service.STATE_RUNNING:
                        status.setText("Server is Running");
                        break;
                    case NPPS4Service.STATE_STOPPING:
                        status.setText("Server Stopping");
                        break;
                }

                if (bridge.binder != null) {
                    try {
                        bridge.binder.getStatusAsync(stateCallbackResult);
                    } catch (RemoteException ignored) {
                    }
                }
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }
}
