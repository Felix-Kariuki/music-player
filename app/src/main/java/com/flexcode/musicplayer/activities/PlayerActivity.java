package com.flexcode.musicplayer.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
     private Thread playThread,prevThread,nextThread;

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

    //resume method


    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    //prev thread
    private void prevThreadBtn() {
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();
                ivPrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    //on clicking the prev button
    private void prevBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            //decrement the song position;
            position = ((position - 1 ) < 0 ? (listSongs.size() -1 ): (position - 1));
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            //setting title and artist name
            tvSongName.setText(listSongs.get(position).getTitle());
            tvSongArtist.setText(listSongs.get(position).getArtist());
            //seek bar position
            seekBar.setMax(mediaPlayer.getDuration() / 1000 );
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            //setting play pause to pause
            playPause.setImageResource(R.drawable.ic_pause);
            //starting the song
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            //decrement the song position
            position = ((position - 1 ) < 0 ? (listSongs.size() -1 ): (position - 1));
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            //setting title and artist name
            tvSongName.setText(listSongs.get(position).getTitle());
            tvSongArtist.setText(listSongs.get(position).getArtist());
            //seek bar position
            seekBar.setMax(mediaPlayer.getDuration() / 1000 );
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            //setting play pause to play
            playPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                ivNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    //when next btn is clicked
    private void nextBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            //increment the song position
            //position = ((position + 1) % listSongs.size());
            position = ((position - 1 ) < 0 ? (listSongs.size() -1 ): (position - 1)); //so as to traverse with no error when at the last to first song
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            //setting title and artist name
            tvSongName.setText(listSongs.get(position).getTitle());
            tvSongArtist.setText(listSongs.get(position).getArtist());
            //Seek Bar position
            seekBar.setMax(mediaPlayer.getDuration() / 1000 );
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            playPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }else {
            mediaPlayer.stop();
            mediaPlayer.release();
            //increment the song position
            position = ((position - 1 ) < 0 ? (listSongs.size() -1 ): (position - 1));
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            tvSongName.setText(listSongs.get(position).getTitle());
            tvSongArtist.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000 );
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            playPause.setImageResource(R.drawable.ic_play);
        }
    }

    //play thread
    private void playThreadBtn() {
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                playPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    //clicking the play pause button
    private void playPauseBtnClicked() {
        //if playing
        if (mediaPlayer.isPlaying()) {
            playPause.setImageResource(R.drawable.ic_play);
            // when pause
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000 );
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            playPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
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
        //using bitmap to change the bg and its gradient
        Bitmap bitmap;
        //setting image of the song
        if (art != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(ivCoverArt);
            //bg gradient change
            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    //song image gradient
                    if (swatch != null) {
                        ImageView ivGradient = findViewById(R.id.ivGradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        ivGradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.player_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.
                                Orientation.BOTTOM_TOP, new int[] {
                                        swatch.getRgb(), 0x00000000
                        });
                        ivGradient.setBackground(gradientDrawable);

                        //bg of entire player activity
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.
                                Orientation.BOTTOM_TOP, new int[] {
                                swatch.getRgb(), swatch.getRgb()
                        });
                        mContainer.setBackground(gradientDrawableBg);

                        //color of song and artist
                        tvSongName.setTextColor(swatch.getTitleTextColor());
                        tvSongArtist.setTextColor(swatch.getBodyTextColor());
                    }else {
                        ImageView ivGradient = findViewById(R.id.ivGradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        ivGradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.player_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.
                                Orientation.BOTTOM_TOP, new int[] {
                                0xff000000, 0x00000000
                        });
                        ivGradient.setBackground(gradientDrawable);

                        //bg of entire player activity
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.
                                Orientation.BOTTOM_TOP, new int[] {
                                0xff000000, 0xff000000
                        });
                        mContainer.setBackground(gradientDrawableBg);

                        //color of song and artist
                        tvSongName.setTextColor(Color.WHITE);
                        tvSongArtist.setTextColor(Color.DKGRAY);
                    }
                }
            });
        } else {
            //if image is null the use default image
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.felix)
                    .into(ivCoverArt);
            ImageView ivGradient = findViewById(R.id.ivGradient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            ivGradient.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.player_bg);
            //color of song and artist
            tvSongName.setTextColor(Color.WHITE);
            tvSongArtist.setTextColor(Color.DKGRAY);
        }
    }
}