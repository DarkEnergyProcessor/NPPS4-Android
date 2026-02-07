package com.npdep.npps4;

import android.content.ServiceConnection;

public class Bridge {
    public INPPS4 binder;
    public MainActivity activity;
    public ServiceConnection serviceConnection;

    private static Bridge bridge = null;
    public static Bridge getInstance() {
        if (bridge == null) {
            bridge = new Bridge();
        }

        return bridge;
    }

    private Bridge() {}
}
