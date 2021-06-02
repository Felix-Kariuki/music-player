package com.flexcode.musicplayer.fragments;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flexcode.musicplayer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.flexcode.musicplayer.activities.MainActivity.PATH_TO_FRAG;
import static com.flexcode.musicplayer.activities.MainActivity.SHOW_MINI_PLAYER;


public class NowPlayingBottomFragment extends Fragment {

    ImageView btnNext,musicImage;
    TextView artistName,songName;
    FloatingActionButton btnPlayPause;
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_now_playing_bottom, container, false);
        artistName = view.findViewById(R.id.tvSongArtistMini);
        songName = view.findViewById(R.id.tvSongNameMini);
        btnNext = view.findViewById(R.id.ivSkipNextBottom);
        btnPlayPause = view.findViewById(R.id.btnPlayPauseMini);
        musicImage = view.findViewById(R.id.ivBottomAlbumArt);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SHOW_MINI_PLAYER){
            if (PATH_TO_FRAG != null) {
                byte[] art = getAlbumArt(PATH_TO_FRAG);
                Glide.with(getContext()).load(art)
                        .into(musicImage);
                songName.setText(PATH_TO_FRAG);
            }
        }
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}