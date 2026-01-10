package com.npdep.npps4;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;

import com.chaquo.python.android.PyApplication;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.util.Objects;

public class App extends PyApplication {
    @Override
    public void onCreate() {
        try {
            String datadir = Objects.requireNonNull(getExternalFilesDir("NPPS4")).toString();
            Os.setenv("PYTHONDONTWRITEBYTECODE", "1", true);
            Os.setenv("NPPS4_CONFIG_MAIN_DATADIR", datadir, false);
            Os.setenv("NPPS4_CONFIG_DATABASE_URL", "sqlite+aiosqlite:///" + datadir + "/main.sqlite3", false);
        } catch (ErrnoException ignored) {}
        loadManifestEnvVars();

        super.onCreate();

        beginOutputRedirection("stdout", OsConstants.STDOUT_FILENO);
        beginOutputRedirection("stderr", OsConstants.STDERR_FILENO);
    }

    private void loadManifestEnvVars() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(
                    getPackageName(),
                    PackageManager.GET_META_DATA
            );

            try {
                Os.setenv("NPPS4_CONFIG_MAIN_DATADIR", Objects.requireNonNull(getExternalFilesDir("NPPS4")).toString(), false);
            } catch (ErrnoException e) {
                Log.e("App", "Unable to set NPPS4_CONFIG_MAIN_DATADIR");
            }

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

    public static void beginOutputRedirection(String tag, int fd) {
        new Thread(() -> {
            try {
                FileDescriptor[] pipe = Os.pipe();
                Os.dup2(pipe[1], fd);

                BufferedReader reader = new BufferedReader(new FileReader(pipe[0]));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(tag, line);
                }
            } catch (Exception e) {
                Log.e("App", "Failed to redirect " + tag, e);
            }
        }).start();
    }
}
