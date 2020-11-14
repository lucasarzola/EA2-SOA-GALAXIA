package com.example.galaxia.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galaxia.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.example.galaxia.servicios.ServicioHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class    LoginActivity extends AppCompatActivity {
    private Button btnLogin ;
    private EditText txtEmail;
    private EditText txtPassword;
    public TextView txtResp;
    public String token="";
    public IntentFilter filtro;

    private BroadcastReceiver receiver = new ReceptorOperacionLogin() ;
    private static String TAG  = LoginActivity.class.getName();

    private static final String URI_LOGIN = "http://so-unlam.net.ar/api/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_login);

        //FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( token -> Log.e(TAG,token.getToken()));

        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPass);
        txtResp = (TextView) findViewById( R.id.textrespuesta );
        btnLogin.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v1) {
                if (esValido()) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("email", txtEmail.getText().toString());
                        obj.put("password", txtPassword.getText().toString());
                            Intent i = new Intent(LoginActivity.this, ServicioHTTP.class );
                        i.putExtra("uri", URI_LOGIN);
                        i.putExtra("datosJson", obj.toString());
                        startService(i);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    configurarBroadcastReceiverLogin();

                }
            }

        });


    }


    class ReceptorOperacionLogin extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String datosJsonString = intent.getStringExtra( "datosJson" );
                token = intent.getStringExtra( "token" );
                Log.i("Token to",token);
                JSONObject datosJson = new JSONObject( datosJsonString );
                if (datosJson.toString() == null) return;
                if (token != "" && intent.getStringExtra("uri").equals( URI_LOGIN )) {
                    Intent i = new Intent( LoginActivity.this, GameActivity.class );
                    i.putExtra( "token", token );
                    startActivity( i );
                    Handler handler = new Handler();
                    handler.postDelayed( new Runnable() {
                        public void run() {
                            txtResp.setText( datosJsonString);
                        }
                    }, 2000 );
                }
                else {
                    txtResp.setText( "ATENCION! Fallo la conexion con el servidor. Puede que alguno de los campos ingresados no sea valido" );
                    Handler handler = new Handler();
                    handler.postDelayed( new Runnable() {

                        public void run() {
                            txtResp.setText( "" );
                        }
                    }, 2000 );
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private void configurarBroadcastReceiverLogin() {
        filtro=new IntentFilter("android.intent.action.MAIN");
        filtro.addCategory("android.intent.category.LAUNCHER");
        registerReceiver( receiver , filtro);
    }

    public void accederARegistro(View view) {

        startActivity( new Intent(LoginActivity.this, RegistroActivity.class));
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean esValido() {

        String campEmail = txtEmail.getText().toString();
        String campPass = txtPassword.getText().toString();
        boolean valido = true;

        if(campEmail.isEmpty()){
            txtEmail.setError("Debe ingresar su E-mal para registrarse");
            valido = false;
        }
        if(campPass.isEmpty() || campPass.length()<8){
            txtPassword.setError("Campo password incorrecto, recuerde ingresar 8 caracteres o mas");
            valido = false;
        }

        return valido;
    }
}
