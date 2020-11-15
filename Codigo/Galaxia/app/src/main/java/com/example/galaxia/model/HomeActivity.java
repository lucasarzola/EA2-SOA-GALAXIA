package com.example.galaxia.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.galaxia.R;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;


public class   HomeActivity extends AppCompatActivity {
    private TextView tvIngresar;
    private static String TAG  = HomeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home);
        tvIngresar = (TextView) findViewById( R.id.tvIngresar);
        File folderPath = new File(getFilesDir () + "/" + getResources (). getString (R.string.app_name));
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( token -> Log.e(TAG,token.getToken()));
        tvIngresar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentHome = new Intent(HomeActivity.this, LoginActivity.class );
                startActivity( intentHome );
            }
        } );
    }


    }