package com.example.galaxia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.galaxia.R;
import com.example.galaxia.servicios.ServicioHTTP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventosActivity extends AppCompatActivity{

    ListView listEventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_eventos);
        listEventos = (ListView) findViewById(R.id.listEventos);

        cargarPreferences();
    }

    private void cargarPreferences() {

        ArrayList<String> lista= new ArrayList<>();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("HistorialEventosPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        int cantEventos=pref.getInt("cantEventos", 0);
        for(int i=1;i<=cantEventos;i++){
            lista.add(pref.getString("key"+String.valueOf(i),null));
        }
        ArrayAdapter adaptador = new ArrayAdapter(this,android.R.layout.simple_list_item_1,lista);
        listEventos.setAdapter(adaptador);

    }

    public void borrarHistorial(View view) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("HistorialEventosPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        informarEvento( "Historial de eventos","se borró el historial de eventos" );
        editor.clear();
        editor.commit();
        ArrayList<String> lista= new ArrayList<>();

        ArrayAdapter adaptador = new ArrayAdapter(this,android.R.layout.simple_list_item_1,lista);
        adaptador.clear();
        listEventos.setAdapter(adaptador);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    public void informarEvento(String tipoEvento, String descripcion){
        String token = getSharedPreferences( "tokenDeSesion" ,MODE_PRIVATE).getString("token","").toString();
        JSONObject obj = new JSONObject();
        try {
            obj.put("env", "PROD");
            obj.put("type_events", tipoEvento);
            obj.put("description", descripcion);
            Intent i = new Intent(EventosActivity.this, ServicioHTTP.class);
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
