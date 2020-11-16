package com.example.galaxia.servicios.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.galaxia.R;
import com.example.galaxia.NotificationActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MensajeriaService extends FirebaseMessagingService {
    private static String TAG = MensajeriaService.class.getName();
    private static String CHANNEL_ID = "firebaseChannel";

    public void onMessageReceived (RemoteMessage remoteMessage){
        super.onMessageReceived( remoteMessage );
        Log.e( TAG, remoteMessage.getNotification().getTitle() );
        Log.e( TAG, remoteMessage.getNotification().getBody() );

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel( CHANNEL_ID,
                    remoteMessage.getNotification().getTitle(),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription( remoteMessage.getNotification().getBody() );
            NotificationManager manager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
            manager.createNotificationChannel( notificationChannel );
        }

        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        notificationIntent.putExtra( "title",remoteMessage.getNotification().getTitle() );
        notificationIntent.putExtra( "body",remoteMessage.getNotification().getBody() );
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        PendingIntent pendingIntent  = PendingIntent.getActivity( this,0,notificationIntent,0 );

        NotificationCompat.Builder builder = new NotificationCompat.Builder( getApplication(),CHANNEL_ID );
        builder.setSmallIcon( R.drawable.icon_noti )
                .setContentTitle( remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody() )
                .setAutoCancel( true )
                .setPriority( NotificationCompat.PRIORITY_DEFAULT );
        builder.setContentIntent( pendingIntent );

        NotificationManagerCompat managerCompat =  NotificationManagerCompat.from( getApplicationContext() );
        managerCompat.notify(0,builder.build());

    }
}
