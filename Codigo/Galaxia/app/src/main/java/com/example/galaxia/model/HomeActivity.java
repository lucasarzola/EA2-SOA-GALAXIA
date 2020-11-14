package com.example.galaxia.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.galaxia.R;

import java.io.File;


public class    HomeActivity extends AppCompatActivity {
    private TextView tvIngresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home);
        tvIngresar = (TextView) findViewById( R.id.tvIngresar);
        File folderPath = new File(getFilesDir () + "/" + getResources (). getString (R.string.app_name));

        tvIngresar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentHome = new Intent(HomeActivity.this, LoginActivity.class );
                startActivity( intentHome );
            }
        } );
    }


    }