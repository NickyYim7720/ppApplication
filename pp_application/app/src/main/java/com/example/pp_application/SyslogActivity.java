package com.example.pp_application;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
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

import static android.widget.Toast.LENGTH_SHORT;

public class SyslogActivity extends AppCompatActivity {

    private static String TAG = "LoginActivity===>";
    String login_code;
    private FingerprintManager fpM;
    private KeyguardManager kygM;
    private CancellationSignal cancellationSignal;
    EditText server, txtuname, txtPasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syslog);

        Log.d(TAG, "onCreate()");

        kygM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fpM = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        init(Functions.getPref("IS_LOGIN", getApplicationContext()));

    }

    public void init(String loginCode) {
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
            login();
        }

    };


    public void login() {
        Log.d(TAG, "login()");
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            Log.d(TAG, "login()->network OK");
            String _uname = txtuname.getText().toString();
            String _passwd = txtPasswd.getText().toString();
            Functions.setPref("SERVER", server.getText().toString(), getApplicationContext());
            String _address = Functions.getPref("SERVER", getApplicationContext());
            String _server = "http://" + _address + "/php/login.php";
            Log.d(TAG, "server path = " + _server);

            try {
                new loginAST(getApplicationContext()).execute(_server, _uname, _passwd);

            } catch (SecurityException e) {
                e.printStackTrace();
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
                    String _message = jsonO.optString("MESSAGE");

                    //Update Shared Preferences
                    Functions.setPref("LOGIN_CODE", _status, getApplicationContext());
                    Log.d(TAG, "loginAST->login_code = " + Functions.getPref("LOGIN_CODE", getApplicationContext()));
                    Functions.setPref("UNAME", _name, getApplicationContext());
                    Log.d(TAG, "loginAST->user_name = " + Functions.getPref("UNAME", getApplicationContext()));
                    Functions.setPref("UID", _uid, getApplicationContext());
                    Log.d(TAG, "loginAST->user_id = " + Functions.getPref("UID", getApplicationContext()));

                    //Popup message for user
                    Toast.makeText(context.getApplicationContext(), _message, Toast.LENGTH_SHORT).show();
                    init(Functions.getPref("LOGIN_CODE", context.getApplicationContext()));

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