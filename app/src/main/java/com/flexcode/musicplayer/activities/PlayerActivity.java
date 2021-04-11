package com.flexcode.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flexcode.musicplayer.R;
import com.flexcode.musicplayer.models.MusicFiles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.flexcode.musicplayer.activities.MainActivity.musicFiles;

public class PlayerActivity extends AppCompatActivity {

    TextView tvSongName,tvSongArtist,tvDurationPlayed,tvTotalDuration;
    SeekBar seekBar;
    ImageView ivBack,ivMenu,ivShuffle,ivPrevious,ivNext,ivRepeat,ivCoverArt;
    FloatingActionButton playPause;
    int position = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;

     Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();
        getIntentMethod();
        tvSongName.setText(listSongs.get(position).getTitle());
        tvSongArtist.setText(listSongs.get(position).getArtist());

        //seek bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    tvDurationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private String formattedTime(int mCurrentPosition) {

        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + seconds;

        if (seconds.length() == 1) {
            return totalNew;
        }else {
            return totalOut;
        }

    }

    //get extra from music adapter
    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        listSongs = musicFiles;
        if (listSongs != null) {
            playPause.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            metaData(uri);
        }

    }

    private void initViews() {

        tvSongName = findViewById(R.id.tvSongName);
        tvSongArtist = findViewById(R.id.tvSongArtist);
        tvDurationPlayed = findViewById(R.id.tvDurationPlayed);
        tvTotalDuration = findViewById(R.id.tvTotalDuration);
        ivCoverArt = findViewById(R.id.ivCoverArt);
        ivBack = findViewById(R.id.ivBack);
        ivMenu = findViewById(R.id.ivMenu);
        ivShuffle = findViewById(R.id.ivShuffle);
        ivPrevious = findViewById(R.id.ivPrevious);
        ivNext = findViewById(R.id.ivNext);
        ivRepeat = findViewById(R.id.ivRepeat);
        seekBar = findViewById(R.id.seekBar);
        playPause = findViewById(R.id.playPause);
    }

    //metadata method to retrieve song metadata to play activity at run time
    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        tvTotalDuration.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(ivCoverArt);
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.felix)
                    .into(ivCoverArt);
        }
    }
}