package com.example.galaxia.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


public class GameActivity extends AppCompatActivity implements SensorEventListener {


    private PantallaJuego pantallaJuego;
    private float mXTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Log.d("X and Y size", "X = " + point.x + ", Y = " + point.y);

        pantallaJuego = new PantallaJuego(this, point.x, point.y);
        setContentView( pantallaJuego );

        //Sensor Accelerometer digunakan untuk menggerakan player ke kanan dan ke kiri
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}