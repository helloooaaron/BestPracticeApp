package com.iamaaronz.bestpracticeapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.iamaaronz.bestpracticeapp.download.MyService;
import com.iamaaronz.bestpracticeapp.message.MessageFragment;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String INTENT_KEY_ACCOUNT = "account";

    private static final int NOTIFICATION_ID = 1;

    private String mAccount;

    DrawerLayout mDrawerLayout;

    FloatingActionButton mFab;

    private MyService.MyServiceBinder mBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder = (MyService.MyServiceBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        mAccount = intent.getStringExtra(INTENT_KEY_ACCOUNT);

        // toolbar
        //
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_white);
            actionBar.setTitle("Demos");
        }

        // fab
        //
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NestedScrollView nestedScrollView = findViewById(R.id.scroll_view_home);
                if (nestedScrollView != null) {
                    nestedScrollView.scrollTo(0, 0);
                }
            }
        });

        // Drawer
        //
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_menu_home);
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                TextView navHeaderAccount = findViewById(R.id.nav_header_account);
                navHeaderAccount.setText("Hello " + mAccount);
                break;
            case R.id.menu_logout:
                Intent intent = new Intent(ACTION_FORCE_LOGOUT);
                sendBroadcast(intent);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_content);
        switch (item.getItemId()) {
            case R.id.nav_menu_home:
                if (fragment instanceof HomeFragment) {
                    Toast.makeText(this, "you already on home", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_content, new HomeFragment());
//                            transaction.addToBackStack(null);
                    transaction.commit();
                    mFab.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.nav_menu_msg:
                if (fragment instanceof MessageFragment) {
                    Toast.makeText(this, "you already on Message Demo", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_content, new MessageFragment());
//                            transaction.addToBackStack(null);
                    transaction.commit();
                    mFab.setVisibility(View.GONE);
                }
                break;
            case R.id.nav_menu_download:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

                // Bind Download service
                //
                if (mBinder == null) {
                    Intent downloadIntent = new Intent(this, MyService.class);
                    bindService(downloadIntent, mConnection, BIND_AUTO_CREATE);
                }

                final NumberPicker picker = new NumberPicker(this);
                final String[] selections = getResources().getStringArray(R.array.download_selections);
                final String[] urls = getResources().getStringArray(R.array.download_urls);
                picker.setMinValue(0);
                picker.setMaxValue(selections.length - 1);
                picker.setDisplayedValues(selections);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Download").setCancelable(true)
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int selection = picker.getValue();
                                mBinder.startDownload(urls[selection]);
                            }
                        })
                        .setView(picker)
                        .create().show();
                break;
        }

        mDrawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                // request external storage access permission
                //
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission is denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * this must be called otherwise "has leaked ServiceConnection" will be complained
         */
        unbindService(mConnection);
    }
}
