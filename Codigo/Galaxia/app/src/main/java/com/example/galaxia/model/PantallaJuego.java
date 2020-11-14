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

import java.util.ArrayList;

public class PantallaJuego extends SurfaceView implements Runnable {

    private Thread mGameThread;
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
    public static int METEOR_DESTROYED = 0;
    public static int ENEMY_DESTROYED = 0;
    private volatile boolean finalizaJuego;
    private volatile boolean mNewHighScore;

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
        mNewHighScore = false;
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
        Log.d("GameThread", "Run stopped");
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
                if (SCORE>=mSP.getHighScore()){
                    mSP.saveHighScore(SCORE, METEOR_DESTROYED, ENEMY_DESTROYED);
                }
            }

            for (Laser l : naveEspacial.getLasers()) {
                if (Rect.intersects(e.getCollision(), l.getCollision())) {
                    e.hit();
                    l.destroy();
                }
            }
        }
        boolean deleting = true;
        while (deleting) {
            if (mEnemies.size() != 0) {
                if (mEnemies.get(0).getY() > mScreenSizeY) {
                    mEnemies.remove(0);
                }
            }

            if (mEnemies.size() == 0 || mEnemies.get(0).getY() <= mScreenSizeY) {
                deleting = false;
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
            drawScore();
            if (finalizaJuego) {
                drawGameOver();
            }
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    void drawScore() {
        Paint score = new Paint();
        score.setTextSize(30);
        score.setColor(Color.WHITE);
        mCanvas.drawText("Score : " + SCORE, 100, 50, score);
    }

    void drawGameOver() {
        Paint gameOver = new Paint();
        gameOver.setTextSize(100);
        gameOver.setTextAlign(Paint.Align.CENTER);
        gameOver.setColor(Color.WHITE);
        mCanvas.drawText("GAME OVER", mScreenSizeX / 2, mScreenSizeY / 2, gameOver);
        Paint highScore = new Paint();
        highScore.setTextSize(50);
        highScore.setTextAlign(Paint.Align.CENTER);
        highScore.setColor(Color.WHITE);
        if (mNewHighScore){
            mCanvas.drawText("New High Score : " + mSP.getHighScore(), mScreenSizeX / 2, (mScreenSizeY / 2) + 60, highScore);
            Paint enemyDestroyed = new Paint();
            enemyDestroyed.setTextSize(50);
            enemyDestroyed.setTextAlign(Paint.Align.CENTER);
            enemyDestroyed.setColor(Color.WHITE);
            mCanvas.drawText("Enemy Destroyed : " + mSP.getEnemyDestroyed(), mScreenSizeX / 2, (mScreenSizeY / 2) + 120, enemyDestroyed);
            Paint meteorDestroyed = new Paint();
            meteorDestroyed.setTextSize(50);
            meteorDestroyed.setTextAlign(Paint.Align.CENTER);
            meteorDestroyed.setColor(Color.WHITE);
            mCanvas.drawText("Meteor Destroyed : " + mSP.getMeteorDestroyed(), mScreenSizeX / 2, (mScreenSizeY / 2) + 180, meteorDestroyed);
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
            mGameThread.sleep(20);
            mCounter += 20;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        Log.d("GameThread", "Main");
        naveViva = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        naveViva = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (finalizaJuego){
                    ((Activity) getContext()).finish();
                    getContext().startActivity(new Intent(getContext(), HomeActivity.class));
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}