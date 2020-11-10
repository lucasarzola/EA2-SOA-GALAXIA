package com.example.galaxia.servicios;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

public class ServiceHTTP extends IntentService {
    private String token="";

    public ServiceHTTP() {
        super("ServicioHTTP");
    }

    public void onCreate() {
        super.onCreate();
        Log.i("SERVIDORHTPP", "Servicio onCreate()");
    }

    @Override
    protected void onHandleIntent( Intent intent) {
        try {
            String uri = intent.getExtras().getString("uri");
            if(uri.equals("http://so-unlam.net.ar/api/api/event")) {
                token = intent.getExtras().getString( "token" );

            }
            JSONObject datosJson = new JSONObject(intent.getExtras().getString("datosJson"));
            // servidorPost(uri,datosJson);

        } catch (JSONException e) {
            Log.e("SERVIDOR","ERROR"+ e.toString());
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
