package com.waki.ezpzmath;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MusicService extends Service  implements MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;
    private int length = 0;

    public MusicService() {
    }

    public class ServiceBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent){     //onUnbid stop playing music
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            length = mPlayer.getCurrentPosition();
        }
        return true;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = MediaPlayer.create(this, R.raw.background);
        mPlayer.setOnErrorListener(this);

        if (mPlayer != null) {   //prepare the Media player
            mPlayer.setLooping(true);
            mPlayer.setVolume(100, 100);
        }


        mPlayer.setOnErrorListener(new OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int
                    extra) {

                onError(mPlayer, what, extra);
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { //execute when we call startService()
        mPlayer.start();
        return START_STICKY;
    }

    public void pauseMusic() {
        if (mPlayer.isPlaying()) {    //stop music and save current length
            mPlayer.pause();
            length = mPlayer.getCurrentPosition();

        }
    }
    public boolean isPlaying(){
        if(mPlayer.isPlaying()){
            return true;
        }
        else
            return false;

    }

    public void resumeMusic() {
        if (mPlayer.isPlaying() == false) {
            mPlayer.seekTo(length);        //start music from the previous saved length
            mPlayer.start();
        }
    }

    public void stopMusic() {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
                stopSelf();
            } finally {
                mPlayer = null;
            }
        }
    }
    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
        return false;
    }
}