package com.example.ju_group.health_assist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;



/*To check if internet connection is available*/

class CheckConnection extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "checkConnection";
    private Context mContext;

    /*for callback*/
    interface testConnection{

        void connectResult(boolean result);
    }
    private testConnection mTestConnection;

    /*Context and callback class object*/
    public CheckConnection(Context context, testConnection mTestConnection) {
        mContext = context;
        this.mTestConnection = mTestConnection;
    }

    /*Check connection by response code*/
    @Override
    protected Boolean doInBackground(String... params) {

        Log.d(TAG, "doInBackground: checking connection");
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(TAG, "No network available!");
            return false;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {

        /*callback with result*/
        mTestConnection.connectResult(result);
    }

    /*Connected to any network?, doesn't imply internet available*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)  mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
