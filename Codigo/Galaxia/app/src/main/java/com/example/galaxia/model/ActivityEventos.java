package com.example.galaxia.model;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.galaxia.R;

import java.util.ArrayList;

public class ActivityEventos extends AppCompatActivity{

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
}
