package com.iamaaronz.bestpracticeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.w3c.dom.Text;

public class SigninActivity extends BaseActivity implements View.OnClickListener {

    public static final int ACTION_SIGNIN = 0;

    public static final int ACTION_REGISTER = 1;

    private static final String ACTION_KEY_NAME = "action";

    private static final String PREF_REMEMBER_PASSWORD = "remember_password";

    private static final String PREF_ACCOUNT = "account";

    private static final String PREF_PASSWORD = "password";

    private SharedPreferences mPref;

    private EditText mEditAccount;

    private EditText mEditPassword;

    private EditText mEditConfirmPwd;

    private CheckBox mCheckBoxRememberPwd;

    public static void actionStart(Context context, int action) {
        Intent intent = new Intent(context, SigninActivity.class);
        intent.putExtra(ACTION_KEY_NAME, action);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Intent intent = getIntent();
        int action = intent.getIntExtra(ACTION_KEY_NAME, ACTION_SIGNIN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(action == ACTION_SIGNIN ? "Sign In" : "Register");
        }

        mPref = getPreferences(MODE_PRIVATE);

        mEditAccount = findViewById(R.id.edit_text_account);
        mEditPassword = findViewById(R.id.edit_text_password);
        mEditConfirmPwd = findViewById(R.id.edit_text_confirm_password);
        mCheckBoxRememberPwd = findViewById(R.id.checkbox_remember_pwd);

        Button buttonSignin = findViewById(R.id.button_signin);
        Button buttonCreate = findViewById(R.id.button_create);
        switch(action) {
            case ACTION_SIGNIN:
                LinearLayout layout = findViewById(R.id.layout_confirm_password);
                layout.setVisibility(View.GONE);
                buttonCreate.setVisibility(View.GONE);
                buttonSignin.setOnClickListener(this);
                if (mPref.getBoolean(PREF_REMEMBER_PASSWORD, false)) {
                    String account = mPref.getString(PREF_ACCOUNT, "");
                    String password = mPref.getString(PREF_PASSWORD, "");
                    mEditAccount.setText(account);
                    mEditPassword.setText(password);
                    mCheckBoxRememberPwd.setChecked(true);
                }
                break;
            case ACTION_REGISTER:
                LinearLayout layoutCheckBox = findViewById(R.id.layout_remeber_pwd);
                layoutCheckBox.setVisibility(View.GONE);
                buttonSignin.setVisibility(View.GONE);
                buttonCreate.setOnClickListener(this);
                break;
            default:
        }
    }

    @Override
    public void onClick(View view) {
        String account = mEditAccount.getText().toString();
        String password = mEditPassword.getText().toString();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            Snackbar.make(view, "Account and Password can't be empty", Snackbar.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor prefEditor = mPref.edit();
        switch (view.getId()) {
            case R.id.button_signin:
                String savedAccount = mPref.getString(PREF_ACCOUNT, "");
                String savedPassword = mPref.getString(PREF_PASSWORD, "");
                if (TextUtils.equals(account, savedAccount) &&
                        TextUtils.equals(password, savedPassword)) {
                    // success
                    if (mCheckBoxRememberPwd.isChecked()) {
                        prefEditor.putString(PREF_ACCOUNT, account);
                        prefEditor.putString(PREF_PASSWORD, password);
                        prefEditor.putBoolean(PREF_REMEMBER_PASSWORD, true);
                        prefEditor.apply();
                    }
                    finish();
                } else {
                    // failure
                    Snackbar.make(view, "Account or Password doesn't match.", Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;

            case R.id.button_create:
                String password2 = mEditConfirmPwd.getText().toString();
                if (!TextUtils.equals(password, password2)) {
                    Snackbar.make(view, "Passwords not match", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                prefEditor.putString(PREF_ACCOUNT, account);
                prefEditor.putString(PREF_PASSWORD, password);
                prefEditor.apply();
                finish();
                break;
            default:
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }
}
