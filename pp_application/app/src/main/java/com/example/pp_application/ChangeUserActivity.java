package com.example.pp_application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

public class ChangeUserActivity extends AppCompatActivity {

    private String TAG = "ChangeUserActivity ===> ";
    private EditText userName, password;
    private ImageButton btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeuser);

        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        btn_login = (ImageButton) findViewById(R.id.btn_login);

    }

    public void btn_login_click(){
        Log.d(TAG, "btn_login_click()");
        Functions.showDialog(this, "button test", "login button avaliable." + userName.getText() + " " + password.getText());
    }
}
