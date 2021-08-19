package com.hustlebuddy.controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class DataComSingleton {

    private static DataComSingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private DataComSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();


    }

    public static synchronized DataComSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new DataComSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
