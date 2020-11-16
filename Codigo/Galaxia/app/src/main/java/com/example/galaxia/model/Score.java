package com.example.galaxia.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.galaxia.EventosActivity;
import com.example.galaxia.R;


public class Score  extends AppCompatActivity implements View.OnClickListener {

    private ImageView mBack;
    private TextView mScore, mEnemigo, mPuntaje;
    private Button btnEvento;
    private LinearLayout mHighScoreContainer;

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

        cargarPuntajeMasAlto();
        btnEvento.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Score.this, EventosActivity.class);
                startActivity(i);
            }
        });

    }

    void cargarPuntajeMasAlto(){
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
}