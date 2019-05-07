package com.example.pp_application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {

    private String TAG = "HomeActivity ===> ";
    private FragmentTransaction ft;
    private Context context;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_syslog:

                    return false;
                case R.id.navigation_account:
                    callAccountFrag();
                    return true;
                case R.id.navigation_info:
                    callInfoFrag();
                    return true;
                case R.id.navigation_changeuser:

                    return false;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate()");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        init();

    }

    private void init(){
        Log.d(TAG, "init()");

        String is_login = Functions.getPref("IS_LOGIN", getApplicationContext());
        if (is_login.matches("") || is_login.matches("0")){
            Functions.setPref("IS_LOGIN", "0", getApplicationContext());
            Functions.setPref("USERNAME", getString(R.string.def_username), getApplicationContext());
            Functions.setPref("UID", getString(R.string.def_uid), getApplicationContext());
            Functions.setPref("DEPARTMENT", getString(R.string.def_department), getApplicationContext());
        }else{
            //Don't do anything.
        }
        callAccountFrag();
    }

    private void callAccountFrag(){
        Log.d(TAG, "callAccountFrag()");
        String username = Functions.getPref("USERNAME", getApplicationContext());
        String userdepartment = Functions.getPref("DEPARTMENT", getApplicationContext());
        String uid = Functions .getPref("UID", getApplicationContext());
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new AccountFragment(username, uid, userdepartment));
        ft.commit();
    }

    private void callInfoFrag(){
        Log.d(TAG, "callInfoFrag()");
        String app_version = getString(R.string.def_version_value);
        String app_name = getString(R.string.app_name);
        String app_author = getString(R.string.app_author);
        String app_publisher = getString(R.string.app_publisher);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new InfoFragment(app_version, app_name, app_author, app_publisher));
        ft.commit();

    }


}
