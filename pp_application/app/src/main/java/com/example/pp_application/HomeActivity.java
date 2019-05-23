package com.example.pp_application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {

    private String TAG = "HomeActivity ===> ";
    private FragmentTransaction ft;
    private Context context;
    private String fragCode = "acc";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_syslog:
                    callSyslogAct();
                    return false;
                case R.id.navigation_account:
                    fragCode = "acc";
                    callAccountFrag();
                    return true;
                case R.id.navigation_info:
                    fragCode = "info";
                    callInfoFrag();
                    return true;
                case R.id.navigation_changeuser:
                    callChangeUserAct();
                    return false;
                case R.id.navigation_setting:
                    callSettingAct();
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

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume()");
        if (fragCode.matches("acc")){
            callAccountFrag();
        }else if(fragCode.matches("info")){
            callInfoFrag();
        }
    }
    private void init(){
        Log.d(TAG, "init()");

        String server = Functions.getPref("SERVER_PATH", getApplicationContext());
        if(server.matches("")){
            Functions.setPref("SERVER_PATH", getString(R.string.def_server), getApplicationContext());
        }
        String login_code = Functions.getPref("LOGIN_CODE", getApplicationContext());
        if (login_code.matches("") || login_code.matches("0")){
            Log.d(TAG, "LOGIN_CODE = " + login_code);
            Functions.setPref("IS_LOGIN", "0", getApplicationContext());
            Functions.setPref("UNAME", getString(R.string.def_username), getApplicationContext());
            Functions.setPref("UID", getString(R.string.def_uid), getApplicationContext());
            Functions.setPref("DEPART", getString(R.string.def_department), getApplicationContext());
        }
        callAccountFrag();
    }

    private void callAccountFrag(){
        Log.d(TAG, "callAccountFrag()");
        String username = Functions.getPref("UNAME", getApplicationContext());
        String userdepartment = Functions.getPref("DEPART", getApplicationContext());
        String uid = Functions .getPref("UID", getApplicationContext());
        Log.d(TAG, username + " " + userdepartment + " " + uid);
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
        String ser_path = Functions.getPref("SERVER_PATH", getApplicationContext());
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new InfoFragment(app_version, app_name, app_author, app_publisher, ser_path));
        ft.commit();

    }

    private void callSettingAct(){
        Log.d(TAG, "callSettingAct()");
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void callChangeUserAct(){
        Log.d(TAG, "callChangeUserAct()");
        Intent intent = new Intent(this, ChangeUserActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void callSyslogAct(){
        Log.d(TAG, "callSyslogAct()");
        Intent intent = new Intent(this, SyslogActivity.class);
        startActivity(intent);
        this.finish();
    }

}
