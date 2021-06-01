package com.flexcode.musicplayer.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flexcode.musicplayer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


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
        return view;
    }
}