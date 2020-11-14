package com.example.galaxia.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.galaxia.R;
import static com.example.galaxia.model.PantallaJuego.ENEMY_DESTROYED;
import static com.example.galaxia.model.PantallaJuego.SCORE;
import java.util.Random;

public class Enemigo {

        private Bitmap mBitmap;
        private int mX;
        private int mY;
        private Rect mCollision;
        private int mScreenSizeX;
        private int mScreenSizeY;
        private int mMaxX;
        private int mMaxY;
        private int mHP;
        private int mVelocidad;
        private boolean doblaDerecha;

        public Enemigo(Context context, int screenSizeX, int screenSizeY) {
            mScreenSizeX = screenSizeX;
            mScreenSizeY = screenSizeY;

            mHP = 5;

            Random random = new Random();
            mBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.enemigo1);
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth() * 3/5, mBitmap.getHeight() * 3/5, false);

            mVelocidad = random.nextInt(3) + 1;

            mMaxX = screenSizeX - mBitmap.getWidth();
            mMaxY = screenSizeY - mBitmap.getHeight();

            mX = random.nextInt(mMaxX);
            mY = 0 - mBitmap.getHeight();

            if (mX<mMaxX){
                doblaDerecha = true;
            }else{
                doblaDerecha = false;
            }

            mCollision = new Rect(mX, mY, mX + mBitmap.getWidth(), mY + mBitmap.getHeight());
        }

        public void actualizar(){
            mY += 7 * mVelocidad;

            if (mX<=0){
                doblaDerecha = true;
            }else if (mX>=mScreenSizeX-mBitmap.getWidth()){
                doblaDerecha = false;
            }

            if (doblaDerecha){
                mX += 7 * mVelocidad;
            }else{
                mX -= 7 * mVelocidad;
            }

            mCollision.left = mX;
            mCollision.top = mY;
            mCollision.right = mX + mBitmap.getWidth();
            mCollision.bottom = mY + mBitmap.getHeight();
        }

        public Rect getCollision() {
            return mCollision;
        }

        public void hit(){
            if (--mHP ==0){
                SCORE += 50;
                ENEMY_DESTROYED++;
                destroy();
            }
        }

        public void destroy(){
            mY = mScreenSizeY + 1;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public int getX() {
            return mX;
        }

        public int getY() {
            return mY;
        }
    }
