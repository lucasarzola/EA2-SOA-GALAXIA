package com.example.galaxia.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.galaxia.LoginActivity;
import com.example.galaxia.R;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;


public class   HomeActivity extends AppCompatActivity {
    private TextView tvIngresar;
    private static String TAG  = HomeActivity.class.getName();
    private TextView lblBattery ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home);
        tvIngresar = (TextView) findViewById( R.id.tvIngresar);
        lblBattery = (TextView) findViewById( R.id.lblBattery );
        File folderPath = new File(getFilesDir () + "/" + getResources (). getString (R.string.app_name));
        this.registerReceiver(this.getBatteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( token -> Log.e(TAG,token.getToken()));
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText( getApplicationContext(),"Conexión a internet exitosa",Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText( getApplicationContext(),"NO está conectado a internet",Toast.LENGTH_SHORT ).show();
        }

        tvIngresar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentHome = new Intent(HomeActivity.this, LoginActivity.class );
                startActivity( intentHome );
            }
        } );
    }
    private BroadcastReceiver getBatteryLevelReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, 0);
            lblBattery.setText("Nivel de Batería actual: " + level + "%");
        }
    };



    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(getBatteryLevelReceiver);

    }
}