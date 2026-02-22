package com.npdep.npps4;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.chaquo.python.android.PyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class App extends PyApplication {
    @Override
    public void onCreate() {
        String datadir = Objects.requireNonNull(getExternalFilesDir("NPPS4")).toString();

        try {
            Os.setenv("PYTHONDONTWRITEBYTECODE", "1", true);
            Os.setenv("NPPS4_CONFIG_MAIN_DATADIR", datadir, false);
            Os.setenv("NPPS4_CONFIG_DATABASE_URL", "sqlite+aiosqlite:///" + datadir + "/main.sqlite3", false);
            Os.setenv("NPPS4_CONFIG_MAIN_SERVERDATA", datadir + "/server_data.json", false);
        } catch (ErrnoException ignored) {}
        loadManifestEnvVars();

        super.onCreate();
        ((AndroidPlatform) Python.getPlatform()).redirectStdioToLogcat();

        // Copy server_data.json to the data directory
        Path targetFile = Paths.get(datadir + "/server_data.json");
        if (!Files.exists(targetFile)) {
            Python py = Python.getInstance();
            PyObject npps4 = py.getModule("npps4");
            Path npps4file = Paths.get(Objects.requireNonNull(npps4.get("__file__")).toString());
            Path sourceFile = npps4file.getParent().resolve("server_data.json");

            try {
                Files.copy(sourceFile, targetFile);
            } catch (IOException ignored) {}
        }
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
}
