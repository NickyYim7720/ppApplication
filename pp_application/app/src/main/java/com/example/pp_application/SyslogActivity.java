package com.example.pp_application;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
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

public class SyslogActivity extends AppCompatActivity {

    private static String TAG = "LoginActivity===>";
    String syslog_code, login_code;
    private FingerprintManager fpM;
    private KeyguardManager kygM;
    private CancellationSignal cancellationSignal;
    private ImageView fingerImg;
    private Button btn_cancel, btn_scan;
    private TextView errorTxt, syslogTxt;
    private Space errorSpace;
    private FloatingActionButton floatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syslog);

        Log.d(TAG, "onCreate()");

        kygM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fpM = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        errorSpace = (Space) findViewById(R.id.errorSpace);
        errorTxt = (TextView) findViewById(R.id.errorTxt);
        syslogTxt = (TextView) findViewById(R.id.syslogTxt);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        fingerImg = (ImageView) findViewById(R.id.fingerImg);
        floatBtn = (FloatingActionButton) findViewById(R.id.floatBtn);

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

    public void init(){
        syslog_code = Functions.getPref("SYSLOG_CODE", getApplicationContext());
        login_code = Functions.getPref("LOGIN_CODE", getApplicationContext());
        if (login_code.matches("") || login_code.matches("0")){
            errorSpace.setVisibility(View.VISIBLE);
            errorTxt.setVisibility(View.VISIBLE);
            fingerImg.setVisibility(View.GONE);
            syslogTxt.setVisibility(View.GONE);
            btn_scan.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.GONE);
        }else{
            errorSpace.setVisibility(View.GONE);
            errorTxt.setVisibility(View.GONE);
            fingerImg.setVisibility(View.VISIBLE);
            syslogTxt.setVisibility(View.VISIBLE);
            if (syslog_code.matches("") || syslog_code.matches("0")){
                btn_scan.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.GONE);
            }else if(syslog_code.matches("1")){
                btn_scan.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.VISIBLE);
            }else if(syslog_code.matches("2")){
                btn_scan.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.GONE);
                Functions.setPref("SYSLOG_CODE", "0", getApplicationContext());
                syslog_code = Functions.getPref("SYSLOG_CODE", getApplicationContext());
            }
        }
    }
    public void btn_scan_Click(View v){
        fpScan(login_code);
        btn_scan.setVisibility(View.GONE);
    }

    public void btn_cancel_Click(View v){
        cancelLog();
    }

    public void fpScan(String loginCode) {
        if (loginCode.matches("0") || loginCode.matches("")) {
            Log.d(TAG, "loginCode = 0");
            Functions.showDialog(this, getString(R.string.error_login_title), getString(R.string.error_not_login_yet));

        } else {
            if (!kygM.isKeyguardSecure()) {
                //check the fingerprint screen lock able
                Log.d(TAG, "!isKeyguardSecure()");
                Functions.showDialog(this, getString(R.string.error_fp_title), getString(R.string.error_fp_lock_disable));
                return;
            }

            if (checkSelfPermission(Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                if (!fpM.isHardwareDetected()) {
                    //check hardware contant the fingerprint hardware
                    Log.d(TAG, "!isHardwareDetected()");
                    Functions.showDialog(this, getString(R.string.error_fp_title), getString(R.string.error_fp_hardware_disable));
                    return;
                }

                if (!fpM.hasEnrolledFingerprints()) {
                    //check device at least has one fingerprint record
                    Log.d(TAG, "!hasEnrolledFingerprints()");
                    Functions.showDialog(this, getString(R.string.error_fp_title), getString(R.string.error_fp_record_null));
                    return;
                }
            }
            startFingerprintListening();
        }
    }


    private void startFingerprintListening() {
        cancellationSignal = new CancellationSignal();
        if (checkSelfPermission(Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
            fpM.authenticate(null, //wrapper class of crypto objects, it makes authentication more safty.
                    cancellationSignal, //for cancel authenticate object
                    0, //optional flags; should be 0
                    authenticationCallback, //callback for receive the authenticate success or fail
                    null); //optional value, if it able to use, fingerprintmanager will though it for sending message.
        }
    }

    FingerprintManager.AuthenticationCallback authenticationCallback = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            Log.d(TAG, "onAuthenticationError");
        }

        @Override
        public void onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed");
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Log.d(TAG, "onAuthenticationSucceeded");
            syslog();
        }

    };


    public void syslog() {
        Log.d(TAG, "syslog()");
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            Log.d(TAG, "syslog()->network OK");
            String _uname = Functions.getPref("UNAME", getApplicationContext());
            String _uid = Functions.getPref("UID", getApplicationContext());
            String _address = Functions.getPref("SERVER_PATH", getApplicationContext());
            String _server = "http://" + _address + "/php/pp/syslog.php";
            Log.d(TAG, "server path = " + _server);

            if(_uname.matches("") || _uname.matches(getText(R.string.def_username).toString()) ||
               _uid.matches("") || _uid.matches(getText(R.string.def_uid).toString())){
                Functions.showDialog(this, getText(R.string.error_login_title).toString(), getText(R.string.error_not_login_yet).toString());
                Intent intent = new Intent(this, ChangeUserActivity.class);
                startActivity(intent);
            }else{
                try {
                    new syslogAST(getApplicationContext()).execute(_server, _uname, _uid);

                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

        } else {
            Log.d(TAG, "no network");
            Functions.showDialog(this, getString(R.string.error_network_title), getString(R.string.error_network_disconnect));
        }
    }

    public void cancelLog() {
        Log.d(TAG, "cancelLog()");
        Log.d(TAG, "syslog()");
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            Log.d(TAG, "syslog()->network OK");
            String _uname = Functions.getPref("UNAME", getApplicationContext());
            String _uid = Functions.getPref("UID", getApplicationContext());
            String _address = Functions.getPref("SERVER_PATH", getApplicationContext());
            String _server = "http://" + _address + "/php/pp/cancellog.php";
            Log.d(TAG, "server path = " + _server);
            try {
                new syslogAST(getApplicationContext()).execute(_server, _uname, _uid);

            } catch (SecurityException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "no network");
            Functions.showDialog(this, getString(R.string.error_network_title), getString(R.string.error_network_disconnect));
        }
    }

    public class syslogAST extends AsyncTask<String, Void, String> {

        //Properties
        Context context;

        //Constructor
        syslogAST(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String _server = params[0];
            String _uname = params[1];
            String _uid = params[2];
            int response_code;
            String response = "";

            Log.d(TAG, "syslogAST->_server = " + _server + ", _uname = " + _uname + ", _uid = " + _uid);

            try {
                URL url = new URL(_server);
                HttpURLConnection httpURLC = (HttpURLConnection) url.openConnection();
                httpURLC.setRequestMethod("POST");
                httpURLC.setConnectTimeout(5000);
                httpURLC.setDoOutput(true);
                httpURLC.setDoInput(true);

                //set POST data
                String data = URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(_uname, "UTF-8");
                data += "&" + URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(_uid, "UTF-8");

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
                if ((String.valueOf(jsonO.optString("STATUS")).matches("1")) || (String.valueOf(jsonO.optString("STATUS")).matches("2"))) {
                    String _status = jsonO.optString("STATUS");
                    String _message = jsonO.optString("MESSAGE");

                    //Update Shared Preferences
                    Functions.setPref("SYSLOG_CODE", _status, getApplicationContext());
                    Log.d(TAG, "syslogAST->syslog_code = " + Functions.getPref("SYSLOG_CODE", getApplicationContext()));

                    //Popup message for user
                    Toast.makeText(context.getApplicationContext(), _message, Toast.LENGTH_SHORT).show();

                    //review the page setting
                    init();

                } else {
                    String _message = jsonO.optString("MESSAGE");

                    //Update Shared Preferences
                    Functions.setPref("SYSLOG_CODE", "0", context.getApplicationContext());

                    //Popup message for user
                    Toast.makeText(context.getApplicationContext(), _message, Toast.LENGTH_SHORT).show();

                    //review the page setting
                    init();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } //JSON Parse

        }

    }
}