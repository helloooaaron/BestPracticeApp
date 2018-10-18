package com.iamaaronz.bestpracticeapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    public static final String ACTION_FORCE_LOGOUT = "com.iamaaronz.bestpracticeapp.logout";

    protected static class ActivityCollector {

        private static List<Activity> activityList = new ArrayList<>();

        static void add(Activity activity) {
            activityList.add(activity);
        }

        static void remove(Activity activity) {
            activityList.remove(activity);
        }

        static void finishAll() {
            for (Activity activity : activityList) {
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
    }

    IntentFilter mFilter = new IntentFilter(ACTION_FORCE_LOGOUT);
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("you have been logged out")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCollector.finishAll();
                            startActivity(new Intent(context, WelcomeActivity.class));
                        }
                    }).setCancelable(false).create().show();

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.add(this);
        Log.d(TAG, "onCreate activity " + this.getLocalClassName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.remove(this);
        Log.d(TAG, "onDestroy activity " + this.getLocalClassName());
    }
}
