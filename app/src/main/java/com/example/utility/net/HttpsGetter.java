package com.example.utility.net;

/**
 * Created by Hoon on 5/2/2016.
 */


public class HttpsGetter {
    public class HttpsGetResult {
        public boolean success = false;
        public String result = "";
    }

    public static abstract class HttpsGetListener {
        public abstract void OnGet(HttpsGetResult result);
    }




    // lock this requester while getting.
    private boolean lock = false;
    private HttpsGetListener listener = null;

    public void Get(String url, final HttpsGetListener listener)
    {
        if(lock)
            return;
        lock = true;

        this.listener = listener;

        // send request
        HttpsRequester.HttpsRequestListener reqlistener = new HttpsRequester.HttpsRequestListener(){
            @Override
            public void AfterExecute(HttpsRequester.HttpsRequestResult result) {
                HttpsGetResult input = new HttpsGetResult();
                input.success = result.readsuccess;
                input.result = result.result;

                listener.OnGet(input);
            }
        };
        HttpsRequester req = new HttpsRequester(reqlistener);
        req.execute(url);
    }



}
