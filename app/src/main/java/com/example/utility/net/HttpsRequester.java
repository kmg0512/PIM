package com.example.utility.net;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Hoon on 5/2/2016.
 */

// reference : http://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/

public class HttpsRequester extends AsyncTask<String, Void, HttpsRequester.HttpsRequestResult> {
    public class HttpsRequestResult {
        public String url = "<Error>";
        public boolean urlmalformed = true;

        public int responsecode = 0;
        public boolean connected = false;

        public boolean readsuccess = false;
        public String result = "";
    }

    public static abstract class HttpsRequestListener {
        public abstract void AfterExecute(HttpsRequestResult result);
    }

    HttpsRequestListener listener = null;
    public HttpsRequester(HttpsRequestListener listener) {
        this.listener = listener;
    }


    @Override
    protected HttpsRequestResult doInBackground(String... urlstr) {
        HttpsRequestResult result = new HttpsRequestResult();

        // check length
        if(urlstr.length != 1) {
            Log.d("HttpsRequester", "needs 1 parameter : url");
            return result;
        }
        result.url = urlstr[0];

        // log url
        String logurl = urlstr[0];
        if(logurl.length() > 20)
            logurl = logurl.substring(0, 17) + "...";
        Log.d("HttpsRequester", "connect to : " + logurl);

        // create url
        URL url = null;
        try {
            url = new URL(urlstr[0]);

            result.urlmalformed = false;
        } catch (MalformedURLException e) {
            Log.d("HttpsRequester", "url malformed : " + logurl);
            e.printStackTrace();

            return result;
        }

        // connect
        HttpsURLConnection con = null;
        try {
            // start connection
            con = (HttpsURLConnection) url.openConnection();

            // get response code
            int respcode = con.getResponseCode();
            result.responsecode = respcode;
            Log.d("HttpsRequester", "response code : " + respcode);
            if(respcode != HttpsURLConnection.HTTP_OK)
                return result;

            result.connected = true;
        } catch (IOException e) {
            Log.d("HttpsRequester", "cannot connect : " + logurl);
            e.printStackTrace();

            return result;
        }


        // read string
        try {
            // create buffer
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(con.getInputStream())
            );

            // read line
            String inputLine;
            StringBuffer buff = new StringBuffer();

            while ((inputLine = reader.readLine()) != null) {
                buff.append(inputLine);
            }

            // close handler
            con.disconnect();
            reader.close();

            result.readsuccess = true;
            result.result = buff.toString();
        } catch (IOException e) {
            Log.d("HttpsRequester", "cannot connect : " + logurl);
            e.printStackTrace();

            return result;
        }

        Log.d("HttpsRequester", "connection successed : " + logurl);

        return result;
    }

    @Override
    protected void onPostExecute(HttpsRequester.HttpsRequestResult result) {
        listener.AfterExecute(result);
    }
}
