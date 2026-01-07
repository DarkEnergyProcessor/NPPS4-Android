package com.npdep.npps4;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import com.chaquo.python.android.PyApplication;

public class App extends PyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        loadManifestEnvVars();
    }

    private void loadManifestEnvVars() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(
                    getPackageName(),
                    PackageManager.GET_META_DATA
            );

            Bundle bundle = ai.metaData;
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    // Load keys prefixed with "env." to avoid
                    if (key.startsWith("env.")) {
                        String envKey = key.substring(4);
                        String envValue = bundle.getString(key);

                        if (envValue != null) {
                            try {
                                Os.setenv(envKey, envValue, false);
                                Log.d("App", "Set env var: " + envKey + "=" + envValue);
                            } catch (ErrnoException e) {
                                Log.e("App", "Failed to set env var: " + envKey, e);
                            }
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("App", "Failed to load meta-data", e);
        }
    }
}
