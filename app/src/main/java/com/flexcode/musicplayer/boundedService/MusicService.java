package com.flexcode.musicplayer.boundedService;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.flexcode.musicplayer.models.MusicFiles;

import java.util.ArrayList;

import static com.flexcode.musicplayer.activities.PlayerActivity.listSongs;


public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    MyBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position =-1;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "method");
        return mBinder;
    }



    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition",-1);
        if (myPosition != -1){
            playMedia(myPosition);
        }
        return START_STICKY;
    }

    private void playMedia(int StartPosition) {
        musicFiles = listSongs;
        position = StartPosition;
        if (mediaPlayer != null ){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (musicFiles != null){
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }else {
          createMediaPlayer(position);
          mediaPlayer.start();
        }
    }

    //start media player
    public void start(){
        mediaPlayer.start();
    }
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
    public void stop() {
        mediaPlayer.stop();
    }
    public void release() {
        mediaPlayer.release();
    }
    public int getDuration() {
        return mediaPlayer.getDuration();
    }
    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    public void createMediaPlayer(int position) {
        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }
    public void pause() {
        mediaPlayer.pause();
    }
    public void OnCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
