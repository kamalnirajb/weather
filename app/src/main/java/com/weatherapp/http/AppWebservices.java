package com.weatherapp.http;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by NirajKumar on 6/9/17.
 */

public class AppWebservices extends AsyncTask<String, Integer, String> implements Response.Listener<JSONObject>, Response.ErrorListener{

    public interface AppWebServiceCallBack  {
        void onSuccess(JSONObject response, String key);
        void onError(String message);
    }

    private static final String TAG = "AppWebService";

    private AppWebServiceCallBack callback;
    private String key;
    private String baseurl;

    private Context context;
    private Activity activity;

    private AppLoader appLoader;

    public AppWebservices(Activity activity, Context context, final AppWebServiceCallBack callback, String key, String baseUrl) {
        this.context = context;
        this.callback = callback;
        this.activity = activity;
        this.key = key;
        this.baseurl = baseUrl;

        appLoader = new AppLoader(activity);
    }

    public boolean isNetworkConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }
    
    private JSONObject getURL(Context context, String key){
        JSONObject json = null;
        try {
            String jsonString = "";
            InputStream is = context.getAssets().open("webservice.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            while (true) {
                if (!(is.read(buffer) >= size)) break;
            }
            is.close();
            jsonString = new String(buffer, "UTF-8");
            JSONObject serviceJson = new JSONObject(jsonString);
            if(serviceJson.has(key)) {
                return serviceJson.getJSONObject(key);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        if (isNetworkConnected()) {
            try {
                this.key = key;
                final JSONObject urlParams = getURL(context, key);
                assert urlParams != null;
                final String loaderMessage = "".equalsIgnoreCase(urlParams.getString("loader")) ? activity.getString(R.string.loading) : urlParams.getString("loader");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        appLoader.showLoader(loaderMessage);
                    }
                });
                String url = baseurl + urlParams.getString("url");
                JSONObject payload = new JSONObject(params[0]);

                if (urlParams.getString("method").equals("GET") && params != null) {
                    for (Iterator<String> iter = payload.keys(); iter.hasNext(); ) {
                        String key = iter.next();
                        url = url.replace(key, payload.getString(key));
                    }
                }
                Log.i(TAG, "URL: " + url);
                JsonObjectRequest request = new JsonObjectRequest(
                        (urlParams.getString("method").equals("GET") ? Request.Method.GET : Request.Method.POST),
                        url,
                        (urlParams.getString("method").equals("GET")) ? null : payload,
                        AppWebservices.this, AppWebservices.this
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        try {
                            params.put("Content-Type", "application/json");

                            if (urlParams.has("headers")) {
                                JSONObject headers = urlParams.getJSONObject("headers");
                                if (headers != null && headers.length() > 0) {
                                    for (Iterator<String> iter = headers.keys(); iter.hasNext(); ) {
                                        String key = iter.next();
                                        params.put(key, "application/json");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return params;
                    }

                };
                Volley.newRequestQueue(context.getApplicationContext()).add(request);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }else {

        }
        return null;
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        appLoader.hideLoader();
        callback.onError(activity.getString(R.string.service_unavailable));
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            appLoader.hideLoader();
            if (response != null) {
                if ("login".equals(key) || "register".equals(key)) {
                    if (response.has("errors")) {
                        JSONArray errors = response.getJSONArray("errors");
                        if (errors.length() > 0) {
                            StringBuilder message = new StringBuilder();
                            for (int i=0;i<errors.length();i++) {
                                message.append(errors.getJSONObject(i).getString("message")).append("\n");
                            }
                            callback.onError(message.toString());
                        }else {
                            callback.onError(context.getString(R.string.service_unavailable));
                        }
                    } else {
                        callback.onSuccess(response, key);
                    }
                }else {
                    if (response.has("cod") && response.getInt("cod") == 200) {
                        callback.onSuccess(response, key);
                    }else {
                        callback.onError(context.getString(R.string.service_unavailable));
                    }
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            callback.onError(context.getString(R.string.service_unavailable));
        }
    }

}
