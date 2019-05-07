package com.example.pp_application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Functions {

    private static String TAG = "Model.java===>";
    public static String url;

    // ==== SharedPreferences ====//
    public static void setPref(String key, String value, Context context) {
        Log.d(TAG, "setPref(key=" + key + ",value=" + value + ",Context=" + context + ")");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPref(String key, Context context) {
        Log.d(TAG, "getPref(key=" + key + ",Context=" + context + ")");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }

    //==== parse JSON ====//
    public static String parseJSON(String json) {
        String out = json;
        Log.d(TAG, "parseJson(in)->" + json);
        Log.d(TAG, "parseJson(out)->" + out);
        return out;
    }

    public static String getUrl(){
        Log.d(TAG, "getUrl()");
        return Functions.url;
    }

    public static void showDialog(Context context, String title, String content){
        Log.d(TAG, "showDialog()");
        //Show a dialog for user check the details
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(content);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

}