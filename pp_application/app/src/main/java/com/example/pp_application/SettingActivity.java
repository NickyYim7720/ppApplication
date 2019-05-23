package com.example.pp_application;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private TextView address_Txt;
    private String ser_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        address_Txt = (TextView) findViewById(R.id.address_Txt);

        ser_path = Functions.getPref("SERVER_PATH", getApplicationContext());
        if(!ser_path.matches("")){
            address_Txt.setText(ser_path);
        }else{
            address_Txt.setText("");
        }

    }

    public void btn_confirm_Click(View v){
        ser_path = address_Txt.getText().toString();
        if(ser_path.matches("")){
            ser_path = getText(R.string.def_server).toString();
        }
        Functions.setPref("SERVER_PATH", ser_path, getApplicationContext());
        Toast.makeText(this, "SERVER_PATH change to: " + ser_path, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        this.finish();
    }
}
