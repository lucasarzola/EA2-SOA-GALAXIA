package com.example.galaxia.model;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.galaxia.R;


public class Score  extends AppCompatActivity implements View.OnClickListener {

    private ImageView mBack;
    private TextView mScore, mEnemigo, mPuntaje;
    private LinearLayout mHighScoreContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.score);

        mBack = findViewById(R.id.back);
        mScore = findViewById(R.id.score);
        mEnemigo = findViewById(R.id.enemy);
        mPuntaje = findViewById(R.id.null_high_score);
        mHighScoreContainer = findViewById(R.id.high_score_container);
        mBack.setOnClickListener(this);

        loadHighScore();
    }

    void loadHighScore(){
        SharedPreferencesManager spm = new SharedPreferencesManager(this);
        if (spm.getPuntajeAlto()!=-1){
            mPuntaje.setVisibility(TextView.GONE);
            mHighScoreContainer.setVisibility(LinearLayout.VISIBLE);
            mScore.setText(spm.getPuntajeAlto() + "");
            mEnemigo.setText(spm.getEnemyDestroyed() + "");
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