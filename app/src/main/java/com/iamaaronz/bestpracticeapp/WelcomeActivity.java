package com.iamaaronz.bestpracticeapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = WelcomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button buttonSignin = findViewById(R.id.button_signin);
        Button buttonRegister = findViewById(R.id.button_register);
        buttonSignin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_signin:
                SigninActivity.actionStart(this, SigninActivity.ACTION_SIGNIN);
                break;
            case R.id.button_register:
                SigninActivity.actionStart(this, SigninActivity.ACTION_REGISTER);
                break;
            default:
        }
    }
}
