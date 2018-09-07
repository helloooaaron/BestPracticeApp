package com.iamaaronz.bestpracticeapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    static class ActivityCollector {

        private static List<Activity> activityList = new ArrayList<>();

        static void add(Activity activity) {
            activityList.add(activity);
        }

        static void remove(Activity activity) {
            activityList.remove(activity);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.add(this);
        Log.d(TAG, "onCreate activity " + this.getLocalClassName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.remove(this);
        Log.d(TAG, "onDestroy activity " + this.getLocalClassName());
    }
}
