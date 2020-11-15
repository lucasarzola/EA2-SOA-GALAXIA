package com.example.galaxia.servicios;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServicioHTTP extends IntentService {

    private String token="";
    public ServicioHTTP() {
        super("ServicioHTTP");
    }
    int marca = 0;
    String result = "";
    String tokenValidacion="";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SERVIDOR", "Servicio onCreate()");
    }

    protected void onHandleIntent(Intent intent){
        try {
            String uri = intent.getExtras().getString("uri");
            if(uri.equals("http://so-unlam.net.ar/api/api/event")) {
                token = intent.getExtras().getString( "token" );

            }
            JSONObject datosJson = new JSONObject(intent.getExtras().getString("datosJson"));
            servidorPost(uri,datosJson);

        } catch (JSONException e) {
            Log.e("SERVIDOR","ERROR"+ e.toString());
        }

    }

    private void servidorPost(String uri, JSONObject datosJson) throws JSONException {

         result  = post (uri,datosJson);
        if (result.equals("NO_OK")){
            Log.e("SERVIDOR","Se recibio una respuesta NO_OK");
            Log.i("Respuesta:","La respuesta es:"+result);
            return;
        }
        if (result == null){
            Log.e("SERVIDOR","Error en POST");
            return;
        }
        datosJson  = new JSONObject(result);
        Intent i =new Intent("android.intent.action.MAIN");
        if(uri.equals("http://so-unlam.net.ar/api/api/login")) {
            tokenValidacion = datosJson.getString("token");
            getSharedPreferences( "tokenDeSesion" ,MODE_PRIVATE).edit().putString("token", tokenValidacion).apply();
        }
        else {
            tokenValidacion =  getSharedPreferences( "tokenDeSesion" ,MODE_PRIVATE).getString( "token","" );

        }
        i.putExtra("datosJson", result);
        i.putExtra("token",tokenValidacion );
        i.putExtra( "uri",uri );
        sendBroadcast(i);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private String post(String uri, JSONObject datosJson) {
        HttpURLConnection conexionHttp=null;
        String result="";

        try {
            URL mUrl=new URL(uri);
            conexionHttp = (HttpURLConnection) mUrl.openConnection();
            conexionHttp.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            if(uri.equals("http://so-unlam.net.ar/api/api/event")) {
                conexionHttp.setRequestProperty("Authorization", "Bearer " + token );
                Log.i( "Token;",token );
            }

            conexionHttp.setDoOutput(true);
            conexionHttp.setDoInput(true);
            conexionHttp.setConnectTimeout(5000);
            conexionHttp.setRequestMethod("POST");
            DataOutputStream wr =new DataOutputStream(conexionHttp.getOutputStream());
            wr.write(datosJson.toString().getBytes("UTF-8"));
            Log.i("SERVIDOR", "Se envia al server"+datosJson.toString());
            wr.flush();
            wr.close();

            conexionHttp.connect();
            int responseCode= conexionHttp.getResponseCode();
            if((responseCode == conexionHttp.HTTP_OK) || (responseCode == conexionHttp.HTTP_CREATED)) {
                result = convertInputStreamToString(new InputStreamReader(conexionHttp.getInputStream()));

            }
            else{
                result = "NO_OK";
            }


        }catch (Exception e) {
            return null;
        }
        Log.i("resultado:",result);
        return result;
    }

    private String convertInputStreamToString(InputStreamReader input) throws IOException {
        BufferedReader streamReader = new BufferedReader(input);
        StringBuilder respondStreamBuild = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            respondStreamBuild.append(inputStr);

        return respondStreamBuild.toString();
    }

}