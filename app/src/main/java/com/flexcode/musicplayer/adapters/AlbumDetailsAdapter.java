package com.flexcode.musicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flexcode.musicplayer.R;
import com.flexcode.musicplayer.activities.AlbumDetails;
import com.flexcode.musicplayer.activities.PlayerActivity;
import com.flexcode.musicplayer.models.MusicFiles;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {

    private Context mContext;
    public static ArrayList<MusicFiles> albumFiles;
    View view; //object

    //constructor
    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        AlbumDetailsAdapter.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent,
                false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.albumName.setText(albumFiles.get(position).getTitle());
        byte[] image = getAlbumArt(albumFiles.get(position).getPath());

        if (image != null) {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.albumImage);
        } else {
            Glide.with(mContext).load(R.drawable.felix)
                    .into(holder.albumImage);
        }

        //playing songs in album activity
        //starting player activity on click of a song in album details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("sender","albumDetails");
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView albumImage;
        TextView albumName;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.ivMusic);
            albumName = itemView.findViewById(R.id.tvMusicFileName);
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
