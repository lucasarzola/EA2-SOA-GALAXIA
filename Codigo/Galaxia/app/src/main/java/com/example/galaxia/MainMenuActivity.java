package com.example.galaxia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galaxia.GameActivity;
import com.example.galaxia.R;
import com.example.galaxia.model.Score;
import com.example.galaxia.servicios.ServicioHTTP;

import org.json.JSONException;
import org.json.JSONObject;


public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mJugar, mPuntaje, mSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main_menu );

        mJugar = findViewById( R.id.play);
        mPuntaje = findViewById(R.id.puntaje );
        mSalir = findViewById(R.id.exit);

        mJugar.setOnClickListener(this);
        mPuntaje.setOnClickListener(this);
        mSalir.setOnClickListener(this);
    }
    public void informarEvento(String tipoEvento, String descripcion){

        Bundle extras = getIntent().getExtras();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("tokenDeSesion", MODE_PRIVATE);
        JSONObject tokenJson = new JSONObject();

        String token ="";
        JSONObject obj = new JSONObject();
        try {
            obj.put("env", "PROD");
            obj.put("type_events", tipoEvento);
            obj.put("description", descripcion);
            Intent i = new Intent(MainMenuActivity.this, ServicioHTTP.class);
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                informarEvento( "sensorAcelerometro", "Se activo el sensor acelerometro" );
                informarEvento( "sensorProximidad", "Se activo el sensor de proximidad" );
                startActivity(new Intent(this, GameActivity.class));
                finish();
                break;
            case R.id.puntaje:
                startActivity(new Intent(this, Score.class));
                break;
            case R.id.exit:
                finish();
                break;
        }
    }
}
