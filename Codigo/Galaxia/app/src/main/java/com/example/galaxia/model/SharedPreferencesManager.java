package com.example.galaxia.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private String mName = "Galaxia";
    private Context mContext;

    public SharedPreferencesManager(Context context) {
        mContext = context;
    }

    public void saveHighScore(int score, int enemyDestroyed){
        SharedPreferences sp = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putInt("puntaje", score);
        e.putInt("enemigos", enemyDestroyed);
        e.commit();
    }

    public int getPuntajeAlto(){
        SharedPreferences sp = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
        return sp.getInt("puntaje", 0);
    }

    public int getEnemigosDestrozados(){
        SharedPreferences sp = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
        return sp.getInt("enemigos", 0);
    }
}