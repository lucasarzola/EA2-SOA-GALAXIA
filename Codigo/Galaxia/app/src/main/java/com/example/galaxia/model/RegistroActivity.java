package com.example.galaxia.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galaxia.R;
import com.example.galaxia.servicios.ServicioHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistroActivity extends AppCompatActivity {

    private EditText txtNombre;
    private EditText txtApellido;
    private EditText txtDni;
    private EditText txtEmail;
    private EditText txtPassword;
    private Spinner txtEnt;
    private EditText txtComision;
    private Button btnRegistrar;
    public TextView txtResp;
    public String token="";
    public IntentFilter filtro;
    private BroadcastReceiver receiverRegistro = new ReceptorOperacionRegistro() ;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            onNetworkChange(ni, context);
        }

        private void onNetworkChange(NetworkInfo networkInfo, Context context) {
            if (networkInfo != null && networkInfo.isConnected() ) {
                Log.d("MenuActivity", "CONNECTED");
            }else{
                Log.d("MenuActivity", "DISCONNECTED");
                Toast.makeText(context.getApplicationContext(), "ATENCION! No hay acceso a internet", Toast.LENGTH_LONG).show();
            }
        }
    };


    private static final String URI_REGISTRO = "http://so-unlam.net.ar/api/api/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_registro);


        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtApellido = (EditText) findViewById(R.id.txtApellido);
        txtDni = (EditText) findViewById(R.id.txtNumDni);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPass);
        txtComision = (EditText) findViewById(R.id.numComision);
        txtEnt = (Spinner) findViewById( R.id.spnEnv );
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        txtResp = (TextView) findViewById( R.id.textrespuesta );

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View v) {

                if (esValido()) {

                    JSONObject obj = new JSONObject();

                    try {
                        obj.put("env",txtEnt.getSelectedItem().toString());
                        obj.put("name", txtNombre.getText().toString());
                        obj.put("lastname", txtApellido.getText().toString());
                        obj.put("dni", Integer.parseInt(txtDni.getText().toString()));
                        obj.put("email", txtEmail.getText().toString());
                        obj.put("password", txtPassword.getText().toString());
                        obj.put("commission",Integer.parseInt( txtComision.getText().toString()));

                        Intent i = new Intent(RegistroActivity.this, ServicioHTTP.class);

                        i.putExtra("uri", URI_REGISTRO);
                        i.putExtra("datosJson", obj.toString());

                        startService(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                configurarBroadcastReceiverRegistro();

            }
        });

    }
    private void configurarBroadcastReceiverRegistro() {
        filtro=new IntentFilter("android.intent.action.MAIN");
        filtro.addCategory("android.intent.category.LAUNCHER");
        registerReceiver( receiverRegistro, filtro);
    }
    class ReceptorOperacionRegistro extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String datosJsonString = intent.getStringExtra( "datosJson" );
                token = intent.getStringExtra( "token" );
                JSONObject datosJson = new JSONObject( datosJsonString );
                if (datosJson.toString() == null) return;
                if (token != "" && intent.getStringExtra("uri").equals( URI_REGISTRO )) {
                    Intent i = new Intent( RegistroActivity.this, LoginActivity.class );
                    i.putExtra( "token", token );
                    startActivity( i );
                    Handler handler = new Handler();
                    handler.postDelayed( new Runnable() {
                        public void run() {
                            txtResp.setText( token );
                        }
                    }, 3000 );

                } else {
                    Handler handler = new Handler();
                    handler.postDelayed( new Runnable() {
                        public void run() {
                            txtResp.setText( "ATENCION! Fallo la conexion con el servidor. Puede que alguno de los campos ingresados no sea valido" );
                        }
                    }, 2000 );
                }

                txtResp.setText( datosJsonString );

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void enviarIntent() {
        Log.i( "TOKEN ES : ",token );

        if(token!="") {
            Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
            i.putExtra("token", token);
            startActivity(i);
        }
        else {
            txtResp = (TextView) findViewById(R.id.textrespuesta);
            txtResp.setText("ATENCION! Fallo la conexion con el servidor en Login. ");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    txtResp.setText("");
                }
            }, 2000);
        }
    }


    public boolean esValido() {

        String campNombre = txtNombre.getText().toString();
        String campApellido = txtApellido.getText().toString();
        String campDni = txtDni.getText().toString();
        String campEmail = txtEmail.getText().toString();
        String campPass = txtPassword.getText().toString();
        String campComi = txtComision.getText().toString();
        String campEnt= txtEnt.getSelectedItem().toString();
        boolean valido = true;
        if(campNombre.isEmpty()){

            txtNombre.setError("Debe ingresar su nombre para registrarse");
            valido = false;
        }
        if(campApellido.isEmpty()){

            txtApellido.setError("Debe ingresar su apellido para registrarse");
            valido = false;
        }

        if(campDni.isEmpty() || campDni.length()>8){

            txtDni.setError("Numero de dni incorrecto,recuerde que no debe superar los 8 caracteres");
            valido = false;
        }

        if(campEmail.isEmpty()){
            txtEmail.setError("Debe ingresar su E-mal para registrarse");
            valido = false;
        }
        if(campPass.isEmpty() || campPass.length()<8){
            txtPassword.setError("Campo password incorrecto, recuerde ingresar 8 caracteres o mas");
            valido = false;
        }

        if(campComi.isEmpty()){
            txtComision.setError("Debe ingresar la comision para registrarse");
            valido = false;
        }
        if(campEnt.isEmpty()){
            txtComision.setError("Debe ingresar Un entorno correcto para registrarse");
            valido = false;
        }
        return valido;
    }


}