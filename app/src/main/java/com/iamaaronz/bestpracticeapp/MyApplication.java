package com.iamaaronz.bestpracticeapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    public static class Account {
        String username;

        Account(String username) { this.username = username; }
    }

    private static Context sContext;

    public static Account sAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    protected static synchronized void signIn (String username) {
        if (sAccount != null) return;
        sAccount = new Account(username);
    }

    protected static synchronized void signOut() {
        sAccount = null;
    }

    protected static Account getAccount() {
        return sAccount;
    }
}
