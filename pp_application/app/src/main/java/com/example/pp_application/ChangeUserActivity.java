package com.example.pp_application;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ChangeUserActivity extends AppCompatActivity {

    private String TAG = "ChangeUserActivity ===> ";
    private String login_code;
    private EditText userName, password;
    private ImageButton btn_login;
    private Button btn_logout;
    private FloatingActionButton floatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeuser);

        floatBtn = (FloatingActionButton) findViewById(R.id.floatBtn);
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        btn_login = (ImageButton) findViewById(R.id.btn_login);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        floatBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                closeAct();
            }
        });
        init();
    }

    private void closeAct(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void init(){
        login_code = Functions.getPref("LOGIN_CODE", getApplicationContext());
        if(login_code.matches("") || login_code.matches("0")){
            userName.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.VISIBLE);
            btn_logout.setVisibility(View.GONE);
        }else if (login_code.matches("1")){
            userName.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            btn_login.setVisibility(View.GONE);
            btn_logout.setVisibility(View.VISIBLE);
        }
    }

    public void btn_login_click(View v){
        Log.d(TAG, "btn_login_click()");
        login();
    }

    public void btn_logout_click(View v){
        Log.d(TAG, "btn_logout_click()");
        Functions.setPref("LOGIN_CODE", "0", getApplicationContext());
        Functions.setPref("UNAME", getText(R.string.def_username).toString(), getApplicationContext());
        Functions.setPref("UID", getText(R.string.def_uid).toString(), getApplicationContext());
        Functions.setPref("DEPART", getText(R.string.def_department).toString(), getApplicationContext());
        init();
    }

    public void login() {
        Log.d(TAG, "login()");
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            Log.d(TAG, "login()->network OK");
            String _uname = userName.getText().toString();
            String _passwd = password.getText().toString();
            String _address = Functions.getPref("SERVER_PATH", getApplicationContext());
            if(_address.matches(getText(R.string.def_server).toString())){
                Functions.showDialog(this, "Setting error", "You have to setup the server address first.");
            }else{
                String _server = "http://" + _address + "/php/pp/login.php";
                Log.d(TAG, "server path = " + _server);

                try {
                    new ChangeUserActivity.loginAST(getApplicationContext()).execute(_server, _uname, _passwd);

                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

        } else {
            Log.d(TAG, "no network");
            Functions.showDialog(this, getString(R.string.error_network_title), getString(R.string.error_network_disconnect));
        }

    }


    public class loginAST extends AsyncTask<String, Void, String> {

        //Properties
        Context context;

        //Constructor
        loginAST(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String _server = params[0];
            String _uname = params[1];
            String _passwd = params[2];
            int response_code;
            String response = "";

            Log.d(TAG, "loginAST->_server = " + _server + ", _uname = " + _uname + ", _passwd = " + _passwd);

            try {
                URL url = new URL(_server);
                HttpURLConnection httpURLC = (HttpURLConnection) url.openConnection();
                httpURLC.setRequestMethod("POST");
                httpURLC.setConnectTimeout(5000);
                httpURLC.setDoOutput(true);
                httpURLC.setDoInput(true);

                //set POST data
                String data = URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(_uname, "UTF-8");
                data += "&" + URLEncoder.encode("passwd", "UTF-8") + "=" + URLEncoder.encode(_passwd, "UTF-8");

                //send POST data
                OutputStream outputS = httpURLC.getOutputStream();
                BufferedWriter buffW = new BufferedWriter(new OutputStreamWriter(outputS, "UTF-8"));
                buffW.write(data);
                buffW.flush();
                buffW.close();
                outputS.close();

                //get webpage response code
                response_code = httpURLC.getResponseCode();
                if (response_code == 200) {
                    //recevice Server response
                    InputStream inputS = httpURLC.getInputStream();
                    BufferedReader buffR = new BufferedReader(new InputStreamReader(inputS, "iso-8859-1"));
                    response = "";
                    String line = "";
                    while ((line = buffR.readLine()) != null) {
                        response += line;
                    }
                    buffR.close();
                    inputS.close();
                    httpURLC.disconnect();
                }
                return response;

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            //JSON Parse
            result = Functions.parseJSON(result);
            try {
                JSONObject jsonO = new JSONObject(result);
                if ((String.valueOf(jsonO.optString("STATUS")).matches("1"))) {
                    String _status = jsonO.optString("STATUS");
                    String _name = jsonO.optString("NAME");
                    String _uid = jsonO.optString("UID");
                    String _depart = jsonO.optString("DEPART");
                    String _message = jsonO.optString("MESSAGE");

                    //Update Shared Preferences
                    Functions.setPref("LOGIN_CODE", _status, getApplicationContext());
                    Log.d(TAG, "loginAST->login_code = " + Functions.getPref("LOGIN_CODE", getApplicationContext()));
                    Functions.setPref("UNAME", _name, getApplicationContext());
                    Log.d(TAG, "loginAST->user_name = " + Functions.getPref("UNAME", getApplicationContext()));
                    Functions.setPref("UID", _uid, getApplicationContext());
                    Log.d(TAG, "loginAST->user_id = " + Functions.getPref("UID", getApplicationContext()));
                    Functions.setPref("DEPART", _depart, getApplicationContext());
                    Log.d(TAG, "loginAST->department = " + Functions.getPref("DEPART", getApplicationContext()));

                    //Popup message for user
                    Toast.makeText(context.getApplicationContext(), _message, Toast.LENGTH_SHORT).show();

                    init();

                } else {
                    String _message = jsonO.optString("MESSAGE");

                    //Update Shared Preferences
                    Functions.setPref("LOGIN_CODE", "0", context.getApplicationContext());

                    //Popup message for user
                    Toast.makeText(context.getApplicationContext(), _message, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } //JSON Parse

        }

    }
}
