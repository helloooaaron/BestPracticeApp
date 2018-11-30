package com.iamaaronz.bestpracticeapp.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.iamaaronz.bestpracticeapp.HomeActivity;
import com.iamaaronz.bestpracticeapp.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyService extends Service implements IDownloadListener {

    private static final String TAG = MyService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 1;

    private MyServiceBinder mBinder = new MyServiceBinder();

    private NotificationManager mNotificationManager;

    private DownloadTask mTask;

    public static final String ACTION_PAUSE_DOWNLOAD = "download_action_pause";

    public static final String ACTION_CANCEL_DOWNLOAD = "download_action_cancel";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (ACTION_PAUSE_DOWNLOAD.equals(action)) {
                    if (mTask != null) {
                        mTask.pause();
                    }
                } else if (ACTION_CANCEL_DOWNLOAD.equals(action)) {
                    if (mTask != null) {
                        mTask.cancel();
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d(TAG, "service is created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service is started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "service is binded");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "service is destroyed.");
    }

    @Override
    public void onProgress(int progress) {
        mNotificationManager.notify(NOTIFICATION_ID, createNotification("Downloading...", progress));
    }

    @Override
    public void onSuccess() {
        mTask = null;
        stopForeground(true);
        mNotificationManager.notify(NOTIFICATION_ID, createNotification("Download Success", -1));
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onFailure() {
        mTask = null;
        stopForeground(true);
        mNotificationManager.notify(NOTIFICATION_ID, createNotification("Download Fail", -1));
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onPause() {
        mTask = null;
        Log.d(TAG, "onPause is called");
        stopForeground(true);
        mNotificationManager.notify(NOTIFICATION_ID, createNotification("Download is paused", -1));
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onCancel() {
        mTask = null;
        Log.d(TAG, "onCancel is called");
        stopForeground(true);
        mNotificationManager.notify(NOTIFICATION_ID, createNotification("Download is canceled", -1));
        unregisterReceiver(mBroadcastReceiver);
    }

    private Notification createNotification(String title, int progress) {
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "download_ch");
        builder.setSmallIcon(R.mipmap.ic_download)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_download))
                .setContentTitle(title)
                .setContentIntent(pi);

        if (progress > 0) {
            PendingIntent pausePi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_PAUSE_DOWNLOAD), 0);
            PendingIntent cancelPi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_CANCEL_DOWNLOAD), 0);
            builder.setContentText(progress + "%")
                    .setProgress(100, progress, false)
                    .addAction(R.mipmap.ic_menu, "Pause", pausePi)
                    .addAction(R.mipmap.ic_menu, "Cancel", cancelPi);
        } else {
            builder.setAutoCancel(true);
        }
        return builder.build();
    }

    public class MyServiceBinder extends Binder {

        public void startDownload(String url) {
            if (mTask == null) { // avoid dup download
                mTask = new DownloadTask(MyService.this);
                mTask.execute(url);
                startForeground(NOTIFICATION_ID, createNotification("Downloading...", 0));
                Toast.makeText(MyService.this, "start downloading " + url, Toast.LENGTH_SHORT).show();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION_PAUSE_DOWNLOAD);
                intentFilter.addAction(ACTION_CANCEL_DOWNLOAD);
                registerReceiver(mBroadcastReceiver, intentFilter);
            }
        }
    }
}


class DownloadTask extends AsyncTask<String, Integer, Integer> {

    private static final String TAG = DownloadTask.class.getSimpleName();

    private static final int RESULT_SUCCESS = 0;

    private static final int RESULT_FAILURE = 1;

    private static final int RESULT_PAUSE = 2;

    private static final int RESULT_CANCEL = 3;

    // this is very important to avoid busy updating progress bar
    private int mLastProgress = 0;

    private volatile boolean mPaused = false;

    private volatile boolean mCanceled = false;

    IDownloadListener mListener;

    OkHttpClient mClient;

    DownloadTask(IDownloadListener listener) {
        mListener = listener;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > mLastProgress) {
            mLastProgress = progress;
            mListener.onProgress(progress);
        }
    }

    @Override
    protected Integer doInBackground(String... urls) {
        String url = urls[0];
        String filename = url.substring(url.lastIndexOf('/'));
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File file = new File(dir, filename);

        // find downloaded length
        //
        long downloadedLength = 0;
        if (file.exists()) {
            downloadedLength = file.length();
        }

        InputStream is = null;
        RandomAccessFile randomAccessFile = null;
        Response response = null;
        try {
            mClient = new OkHttpClient();

            // get length
            //
            Request request = new Request.Builder().url(url).build();
            response = mClient.newCall(request).execute();
            if (response == null || !response.isSuccessful()) {
                return RESULT_FAILURE;
            }
            long contentLength = response.body().contentLength();
            response.close();
            if (contentLength == 0) {
                return RESULT_FAILURE;
            } else if (contentLength == downloadedLength) {
                return RESULT_SUCCESS;
            }

            // request download from breakpoint
            //
            request = new Request.Builder().url(url)
                    .addHeader("Range", String.format("bytes=%d-%d", downloadedLength, contentLength))
                    .build();
            response = mClient.newCall(request).execute();
            if (response == null || !response.isSuccessful()) {
                return RESULT_FAILURE;
            }

            // write to file
            //
            is = response.body().byteStream();
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(downloadedLength);
            byte[] buffer = new byte[4096];
            int len = 0, total = 0;
            while ((len = is.read(buffer)) != -1) {
                if (mPaused) return RESULT_PAUSE;
                if (mCanceled) {
                    file.delete();
                    return RESULT_CANCEL;
                }
                total += len;
                randomAccessFile.write(buffer, 0, len);
                int progress = (int)((total + downloadedLength) * 100 / contentLength);
                publishProgress(progress);
            }
        } catch (IOException e) {
            return RESULT_FAILURE;
        } finally {
            try {
                if (is != null) is.close();
                if (randomAccessFile != null) randomAccessFile.close();
                if (response != null) response.close();
            } catch (IOException e) { }

        }

        return RESULT_SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch(integer) {
            case RESULT_SUCCESS:
                mListener.onSuccess();
                break;
            case RESULT_FAILURE:
                mListener.onFailure();
                break;
            case RESULT_PAUSE:
                mListener.onPause();
                break;
            case RESULT_CANCEL:
                mListener.onCancel();
                break;
        }
    }

    public void pause() {
        mPaused = true;
    }

    public void cancel() {
        mCanceled = true;
    }
}