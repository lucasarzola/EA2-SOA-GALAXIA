package com.example.galaxia.model;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galaxia.R;

public class NotificationActivity extends  AppCompatActivity{

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_notification );

            String title = getIntent().getStringExtra(  "title") ;
            String body = getIntent().getStringExtra(  "body") ;

            TextView textViewTitle = findViewById( R.id.notification_title );
            TextView textViewBody = findViewById( R.id.notification_body );

            textViewTitle.setText(title);
            textViewBody.setText(body);

        }
}
