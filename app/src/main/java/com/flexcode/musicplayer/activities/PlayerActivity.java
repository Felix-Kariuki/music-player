package com.flexcode.musicplayer.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flexcode.musicplayer.R;
import com.flexcode.musicplayer.boundedService.ActionPlaying;
import com.flexcode.musicplayer.boundedService.MusicService;
import com.flexcode.musicplayer.boundedService.NotificationReceiver;
import com.flexcode.musicplayer.models.MusicFiles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


import static com.flexcode.musicplayer.activities.MainActivity.musicFiles;
import static com.flexcode.musicplayer.activities.MainActivity.repeatBoolean;
import static com.flexcode.musicplayer.activities.MainActivity.shuffleBoolean;
import static com.flexcode.musicplayer.adapters.AlbumDetailsAdapter.albumFiles;
import static com.flexcode.musicplayer.adapters.MusicAdapter.mFiles;
import static com.flexcode.musicplayer.boundedService.ApplicationClass.ACTION_NEXT;
import static com.flexcode.musicplayer.boundedService.ApplicationClass.ACTION_PLAY;
import static com.flexcode.musicplayer.boundedService.ApplicationClass.ACTION_PREVIOUS;
import static com.flexcode.musicplayer.boundedService.ApplicationClass.CHANNEL_ID_1;
import static com.flexcode.musicplayer.boundedService.ApplicationClass.CHANNEL_ID_2;

public class PlayerActivity extends AppCompatActivity
        implements ActionPlaying, ServiceConnection {

    TextView tvSongName, tvSongArtist, tvDurationPlayed, tvTotalDuration;
    SeekBar seekBar;
    ImageView ivBack, ivMenu, ivShuffle, ivPrevious, ivNext, ivRepeat, ivCoverArt;
    FloatingActionButton playPause;
    int position = -1;
    public static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    public static Uri uri;
    //public static MediaPlayer mediaPlayer;

    Handler handler = new Handler();
    MusicService musicService;
    MediaSessionCompat mediaSessionCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");
        initViews();
        getIntentMethod();
        //seek bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress * 1000);
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
                if (musicService != null) {
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    tvDurationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        //shuffle button
        ivShuffle.setOnClickListener(v -> {
            if (shuffleBoolean) {
                shuffleBoolean = false;
                ivShuffle.setImageResource(R.drawable.ic_shuffle_off);
            } else {
                shuffleBoolean = true;
                ivShuffle.setImageResource(R.drawable.ic_shuffle_on);
            }
        });
        //repeat button repeat songs
        ivRepeat.setOnClickListener(v -> {
            if (repeatBoolean) {
                repeatBoolean = false;
                ivRepeat.setImageResource(R.drawable.ic_repeat_off);
            } else {
                repeatBoolean = true;
                ivRepeat.setImageResource(R.drawable.ic_repeat_on);
            }
        });
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //resume method
    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    //prev thread
    private void prevThreadBtn() {
        Thread prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                ivPrevious.setOnClickListener(v -> prevBtnClicked());
            }
        };
        prevThread.start();
    }

    //on clicking the prev button
    public void prevBtnClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            //decrement the song position;
            //if shuffle on ad repeat off
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }//else the repeat is on no changing position of song

            //position = ((position - 1 ) < 0 ? (listSongs.size() -1 ): (position - 1));
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);//mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            //setting title and artist name
            tvSongName.setText(listSongs.get(position).getTitle());
            tvSongArtist.setText(listSongs.get(position).getArtist());
            //seek bar position
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            //setting play pause to pause
            showNotification(R.drawable.ic_pause);
            playPause.setImageResource(R.drawable.ic_pause);
            //starting the song
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();
            //decrement the song position
            //if shuffle on ad repeat off
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }//else the repeat is on no changing position of song

            //position = ((position - 1 ) < 0 ? (listSongs.size() -1 ): (position - 1));
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);//mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            //setting title and artist name
            tvSongName.setText(listSongs.get(position).getTitle());
            tvSongArtist.setText(listSongs.get(position).getArtist());
            //seek bar position
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_play);
            //setting play pause to play
            playPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void nextThreadBtn() {
        Thread nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                ivNext.setOnClickListener(v -> nextBtnClicked());
            }
        };
        nextThread.start();
    }

    //when next btn is clicked
    public void nextBtnClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            //if shuffle on ad repeat off
            if (shuffleBoolean && !repeatBoolean) {

                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                //position = getRandom(listSongs.size() - 1);
                //position = ((position - 1 ) < 0 ? (listSongs.size() -1 ): (position - 1));
                position = ((position + 1) % listSongs.size());
            }//else the repeat is on no changing position of song

            //increment the song position
            //position = ((position + 1) % listSongs.size()); // use this the other one prev
            //position = ((position - 1 ) < 0 ? (listSongs.size() -1 ): (position - 1)); //so as to traverse with no error when at the last to first song
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);//mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            //setting title and artist name
            tvSongName.setText(listSongs.get(position).getTitle());
            tvSongArtist.setText(listSongs.get(position).getArtist());
            //Seek Bar position
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_pause);
            playPause.setImageResource(R.drawable.ic_pause);
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();
            //if shuffle on ad repeat off
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            }//else the repeat is on no changing position of song

            //increment the song position
            //position = ((position - 1 ) < 0 ? (listSongs.size() -1 ): (position - 1));
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);//mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            tvSongName.setText(listSongs.get(position).getTitle());
            tvSongArtist.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_play);
            playPause.setImageResource(R.drawable.ic_play);
        }
    }

    //random song method
    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    //play thread
    private void playThreadBtn() {
        Thread playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPause.setOnClickListener(v -> playPauseBtnClicked());
            }
        };
        playThread.start();
    }

    //clicking the play pause button
    public void playPauseBtnClicked() {
        //if playing
        if (musicService.isPlaying()) {
            playPause.setImageResource(R.drawable.ic_play);
            showNotification(R.drawable.ic_play);
            // when pause
            musicService.pause();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            showNotification(R.drawable.ic_pause);
            playPause.setImageResource(R.drawable.ic_pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }


    private String formattedTime(int mCurrentPosition) {

        String totalOut;
        String totalNew;
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + seconds;

        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }

    }

    //get extra from music adapter & album details adapter
    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");

        //check if sender is from album details
        if (sender != null && sender.equals("albumDetails")) {
            listSongs = albumFiles;
        } else {
            //change music files to updated list mFiles
            listSongs = mFiles;
        }

        //listSongs = mFiles;
        if (listSongs != null) {
            playPause.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        showNotification(R.drawable.ic_pause);
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);


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
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            Palette.from(bitmap).generate(palette -> {
                assert palette != null;
                Palette.Swatch swatch = palette.getDominantSwatch();
                //song image gradient
                ImageView ivGradient = findViewById(R.id.ivGradient);
                RelativeLayout mContainer = findViewById(R.id.mContainer);
                ivGradient.setBackgroundResource(R.drawable.gradient_bg);
                mContainer.setBackgroundResource(R.drawable.player_bg);
                GradientDrawable gradientDrawable;
                if (swatch != null) {
                    gradientDrawable = new GradientDrawable(GradientDrawable.
                            Orientation.BOTTOM_TOP, new int[]{
                            swatch.getRgb(), 0x00000000
                    });
                    ivGradient.setBackground(gradientDrawable);

                    //bg of entire player activity
                    GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.
                            Orientation.BOTTOM_TOP, new int[]{
                            swatch.getRgb(), swatch.getRgb()
                    });
                    mContainer.setBackground(gradientDrawableBg);

                    //color of song and artist
                    tvSongName.setTextColor(swatch.getTitleTextColor());
                    tvSongArtist.setTextColor(swatch.getBodyTextColor());
                } else {
                    gradientDrawable = new GradientDrawable(GradientDrawable.
                            Orientation.BOTTOM_TOP, new int[]{
                            0xff000000, 0x00000000
                    });
                    ivGradient.setBackground(gradientDrawable);

                    //bg of entire player activity
                    GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.
                            Orientation.BOTTOM_TOP, new int[]{
                            0xff000000, 0xff000000
                    });
                    mContainer.setBackground(gradientDrawableBg);

                    //color of song and artist
                    tvSongName.setTextColor(Color.WHITE);
                    tvSongArtist.setTextColor(Color.DKGRAY);
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

    //Image change  Animation
    /*public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap){
    }*/


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        Toast.makeText(this, "Connected  " + musicService, Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);
        tvSongName.setText(listSongs.get(position).getTitle());
        tvSongArtist.setText(listSongs.get(position).getArtist());
        musicService.OnCompleted();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

    void showNotification(int playPauseBtn) {
        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0,
                prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0,
                pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] picture = null;
        Bitmap thumb = null;
        picture = getAlbumArt(listSongs.get(position).getPath());
        if (picture != null) {
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        } else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.felix);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(listSongs.get(position).getTitle())
                .setContentText(listSongs.get(position).getArtist())
                .addAction(R.drawable.ic_skip_previous_24, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.ic_skip_next_24, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);

    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

}