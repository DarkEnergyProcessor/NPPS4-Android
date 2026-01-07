package com.npdep.npps4;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NPPS4Service extends Service {
    private Runnable serverRunnable = null;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final INPPS4 binder = new INPPS4.Stub() {
        @Override
        public void shutdown() {
            if (serverRunnable == null) {
                return;
            }

            Python py = Python.getInstance();
            PyObject androidMain = py.getModule("android_main");
            androidMain.callAttr("stop_server");
        }

        @Override
        public ConsoleText pollConsole() {
            synchronized (queue) {
                return queue.poll();
            }
        }
    };
    private final Queue<ConsoleText> queue = new LinkedList<>();

    public NPPS4Service() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Runnable runnable = () -> {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            Notification notification = new NotificationCompat.Builder(this, NPPS4Service.class.getName())
                    .setContentTitle("NPPS4 Server")
                    .setContentText("Server is running")
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Make sure this icon exists
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();

            Python py = Python.getInstance();
            PyObject androidMain = py.getModule("android_main");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(startId, notification, FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                startForeground(startId, notification);
            }

            androidMain.callAttr("start_server");

            serverRunnable = null;
            stopSelf(startId);
        };
        executor.submit(runnable);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder.asBinder();
    }

    @Override
    public void onDestroy() {
        executor.shutdown();
    }
}