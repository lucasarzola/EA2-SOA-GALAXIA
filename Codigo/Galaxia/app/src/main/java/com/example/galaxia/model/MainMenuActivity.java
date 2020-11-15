package com.example.galaxia.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.galaxia.R;


public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mJugar, mPuntaje, mSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mJugar = findViewById( R.id.play);
        mPuntaje = findViewById(R.id.high_score);
        mSalir = findViewById(R.id.exit);

        mJugar.setOnClickListener(this);
        mPuntaje.setOnClickListener(this);
        mSalir.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                startActivity(new Intent(this, GameActivity.class));
                finish();
                break;
            case R.id.high_score:
                startActivity(new Intent(this, Score.class));
                break;
            case R.id.exit:
                finish();
                break;
        }
    }
}
