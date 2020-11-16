package com.example.galaxia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.galaxia.model.SharedPreferencesManager;
import com.example.galaxia.servicios.ServicioHTTP;

import org.json.JSONException;
import org.json.JSONObject;


public class ScoreActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mBack;
    private TextView mScore, mEnemigo, mPuntaje;
    private Button btnEvento;
    private LinearLayout mHighScoreContainer;
    private  String token ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.score);

        mBack = findViewById(R.id.back);
        mScore = findViewById(R.id.score);
        mEnemigo = findViewById(R.id.enemy);
        mPuntaje = findViewById(R.id.null_high_score);
        btnEvento = findViewById( R.id.btnEvento );
        mHighScoreContainer = findViewById(R.id.high_score_container);
        mBack.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        String token = extras.getString( "token" );
        cargarPuntajeMasAlto();
        btnEvento.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent( ScoreActivity.this, EventosActivity.class);
                informarEvento( "Cambio de Activity", "Se abrio pantalla de Eventos" );
                startActivity(i);

            }
        });

    }

    void cargarPuntajeMasAlto(){
        informarEvento( "PantalladeScore","Se encuentra en pantalla de Score" );
        SharedPreferencesManager spm = new SharedPreferencesManager(this);
        if (spm.getPuntajeAlto()!=-1){
            mPuntaje.setVisibility(TextView.GONE);
            mHighScoreContainer.setVisibility(LinearLayout.VISIBLE);
            mScore.setText(spm.getPuntajeAlto() + "");
            mEnemigo.setText(spm.getEnemigosDestrozados() + "");
        }else{
            mPuntaje.setVisibility(TextView.VISIBLE);
            mHighScoreContainer.setVisibility(LinearLayout.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    public void informarEvento(String tipoEvento, String descripcion){

        token = getSharedPreferences( "tokenDeSesion" ,MODE_PRIVATE).getString("token","").toString();

        JSONObject obj = new JSONObject();
        try {
            obj.put("env", "PROD");
            obj.put("type_events", tipoEvento);
            obj.put("description", descripcion);
            Intent i = new Intent( ScoreActivity.this, ServicioHTTP.class);
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