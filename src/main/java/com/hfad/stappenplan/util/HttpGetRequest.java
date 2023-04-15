package com.hfad.stappenplan.util;

import android.os.AsyncTask;

import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class HttpGetRequest extends AsyncTask<URL, Void, String> {
    private HttpGetRequestResponse delegate;

    public HttpGetRequest(HttpGetRequestResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(URL... urls) {
        String json = null;

        URL url = urls[0];

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            json = response.body().string();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return json;
    }

    @Override
    protected void onPostExecute(String json){
        super.onPostExecute(json);
        delegate.processFinish(json);
    }
}
