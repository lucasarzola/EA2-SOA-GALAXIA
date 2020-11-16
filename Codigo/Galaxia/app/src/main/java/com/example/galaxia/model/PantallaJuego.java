package com.example.galaxia.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.galaxia.MainMenuActivity;

import java.util.ArrayList;

public class PantallaJuego extends SurfaceView implements Runnable {

    private Thread mHiloJuego;
    private volatile boolean naveViva;
    private NaveEspacial naveEspacial;
    private Paint mPaint;
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private ArrayList<Laser> mLasers;
    private ArrayList<Enemigo> mEnemies;
    private int mScreenSizeX, mScreenSizeY;
    private int mCounter = 0;
    private SharedPreferencesManager mSP;
    public static int SCORE = 0;
    public static int ENEMIGOS_DESTROZADOS = 0;
    private volatile boolean finalizaJuego;
    private volatile boolean mPuntajeAlto;

    public PantallaJuego(Context context, int screenSizeX, int screenSizeY) {
        super(context);

        mScreenSizeX = screenSizeX;
        mScreenSizeY = screenSizeY;
        mSP = new SharedPreferencesManager(context);

        mPaint = new Paint();
        mSurfaceHolder = getHolder();

        reset();
    }

    void reset() {
        SCORE = 0;
        naveEspacial = new NaveEspacial( getContext(), mScreenSizeX, mScreenSizeY );
        mLasers = new ArrayList<>();
        mEnemies = new ArrayList<>();
        finalizaJuego = false;
        mPuntajeAlto = false;
    }

    @Override
    public void run() {
        while (naveViva) {
            if (!finalizaJuego) {
                actualizar();
                draw();
                control();
            }
        }
        Log.d("Juego", "Terminó el juego");
    }

    public void actualizar() {
        naveEspacial.actualizar();
        if (mCounter % 200 == 0) {
            naveEspacial.fire();
        }

        for (Enemigo e : mEnemies) {
            e.actualizar();
            if (Rect.intersects(e.getCollision(), naveEspacial.getCollision())) {
                e.destroy();
                finalizaJuego = true;
                if (SCORE>=mSP.getPuntajeAlto()){
                    mSP.saveHighScore(SCORE, ENEMIGOS_DESTROZADOS );
                }
            }

            for (Laser l : naveEspacial.getLasers()) {
                if (Rect.intersects(e.getCollision(), l.getCollision())) {
                    e.hit();
                    l.destroy();
                }
            }
        }
        boolean eliminar = true;
        while (eliminar) {
            if (mEnemies.size() != 0) {
                if (mEnemies.get(0).getY() > mScreenSizeY) {
                    mEnemies.remove(0);
                }
            }

            if (mEnemies.size() == 0 || mEnemies.get(0).getY() <= mScreenSizeY) {
                eliminar = false;
            }
        }
        if (mCounter % 2000 == 0) {
            mEnemies.add(new Enemigo(getContext(), mScreenSizeX, mScreenSizeY));
        }

    }

    public void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.BLACK);
            mCanvas.drawBitmap( naveEspacial.getBitmap(), naveEspacial.getX(), naveEspacial.getY(), mPaint);

            for (Laser l : naveEspacial.getLasers()) {
                mCanvas.drawBitmap(l.getBitmap(), l.getX(), l.getY(), mPaint);
            }
            for (Enemigo e : mEnemies) {
                mCanvas.drawBitmap(e.getBitmap(), e.getX(), e.getY(), mPaint);
            }
            dibujarPuntaje();
            if (finalizaJuego) {
                dibujarFinDeJuego();
            }
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    void dibujarPuntaje() {
        Paint score = new Paint();
        score.setTextSize(30);
        score.setColor(Color.WHITE);
        mCanvas.drawText("Score : " + SCORE, 100, 50, score);
    }

    void dibujarFinDeJuego() {
        Paint finJuego = new Paint();
        finJuego.setTextSize(100);
        finJuego.setTextAlign(Paint.Align.CENTER);
        finJuego.setColor(Color.WHITE);
        mCanvas.drawText("FIN DEL JUEGO", mScreenSizeX / 2, mScreenSizeY / 2, finJuego);
        Paint highScore = new Paint();
        highScore.setTextSize(50);
        highScore.setTextAlign(Paint.Align.CENTER);
        highScore.setColor(Color.WHITE);
        if (mPuntajeAlto){
            mCanvas.drawText("Nuevo Puntaje Alto: " + mSP.getPuntajeAlto(), mScreenSizeX / 2, (mScreenSizeY / 2) + 60, highScore);
            Paint enemigoDestrozado = new Paint();
            enemigoDestrozado.setTextSize(50);
            enemigoDestrozado.setTextAlign(Paint.Align.CENTER);
            enemigoDestrozado.setColor(Color.WHITE);
            mCanvas.drawText("Enemigo Destrozado : " + mSP.getEnemigosDestrozados(), mScreenSizeX / 2, (mScreenSizeY / 2) + 120, enemigoDestrozado);

        }

    }

    public void steerLeft(float speed) {
        naveEspacial.steerLeft(speed);
    }

    public void steerRight(float speed) {
        naveEspacial.steerRight(speed);
    }

    public void stay() {
        naveEspacial.stay();
    }

    public void control() {
        try {
            if (mCounter == 10000) {
                mCounter = 0;
            }
            mHiloJuego.sleep(20);
            mCounter += 20;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        Log.d("HiloJuego", "Pausa");
        naveViva = false;
        try {
            mHiloJuego.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        naveViva = true;
        mHiloJuego = new Thread(this);
        mHiloJuego.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (finalizaJuego){
                    ((Activity) getContext()).finish();
                    getContext().startActivity(new Intent(getContext(), MainMenuActivity.class));
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}