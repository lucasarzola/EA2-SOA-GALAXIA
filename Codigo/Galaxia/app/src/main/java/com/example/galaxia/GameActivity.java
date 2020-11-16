package com.example.galaxia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.galaxia.model.PantallaJuego;
import com.example.galaxia.servicios.ServicioHTTP;

import org.json.JSONException;
import org.json.JSONObject;


public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private PantallaJuego pantallaJuego;
    private float mXTemp;
    private Canvas mCanvas;
    private Boolean play = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Log.d("X and Y size", "X = " + point.x + ", Y = " + point.y);

        pantallaJuego = new PantallaJuego(this, point.x, point.y);
        setContentView( pantallaJuego );

        //sensor acelerometro
        SensorManager managerAcc = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor acelerometro = managerAcc.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        managerAcc.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
        //sensor Proximidad
        SensorManager managerProx = (SensorManager) getSystemService( Context.SENSOR_SERVICE );
        Sensor proximidad = managerProx.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        managerProx.registerListener( this ,proximidad,SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onResume() {
        super.onResume();
        pantallaJuego.resume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        pantallaJuego.pause();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        mXTemp = event.values[0];

        if (event.values[0] > 1){
            pantallaJuego.steerLeft(event.values[0]);
        }
        else if (event.values[0] < -1){
            pantallaJuego.steerRight(event.values[0]);
        }else{
            pantallaJuego.stay();
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            float valProx = Float.valueOf(event.values[0]);
            Log.i("Valor Sensor Prox",String.valueOf(valProx));
            if(valProx <= 0){
                String descripcion = "";
                if(play) {
                    play = false;
                    descripcion = "JUEGO PAUSADO";
                    onPause();
                    Toast.makeText( getApplicationContext(),"Juego En Pausa",Toast.LENGTH_SHORT ).show();
                } else {
                    play = true;
                    descripcion = "JUEGO REANUDADO";
                    onResume();
                    Toast.makeText( getApplicationContext(),"Juego Reanudado",Toast.LENGTH_SHORT ).show();

                }
                informarEvento("Estado de juego", descripcion);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void informarEvento(String tipoEvento, String descripcion){

        String token = getSharedPreferences( "tokenDeSesion" ,MODE_PRIVATE).getString("token","").toString();

        JSONObject obj = new JSONObject();
        try {
            obj.put("env", "PROD");
            obj.put("type_events", tipoEvento);
            obj.put("description", descripcion);
            Intent i = new Intent(GameActivity.this, ServicioHTTP.class);
            i.putExtra("uri", "http://so-unlam.net.ar/api/api/event");
            i.putExtra("token", token);
            i.putExtra("datosJson", obj.toString());

            startService(i);
            guardarEnPreferences(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void guardarEnPreferences(String datosEvento){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("HistorialEventosPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        int cantEventos=pref.getInt("cantEventos", 0);
        cantEventos++;

        String datos="IdEvento "+String.valueOf(cantEventos) + datosEvento;
        editor.putInt("cantEventos", cantEventos);
        editor.putString("key"+String.valueOf(cantEventos), datos);
        editor.commit();

    }
}