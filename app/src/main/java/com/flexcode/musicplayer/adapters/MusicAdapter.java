package com.flexcode.musicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flexcode.musicplayer.R;
import com.flexcode.musicplayer.activities.PlayerActivity;
import com.flexcode.musicplayer.models.MusicFiles;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> mFiles;

    //constructor
    public MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mFiles = mFiles;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image = getAlbumArt(mFiles.get(position).getPath());

        if (image != null) {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        } else {
            Glide.with(mContext).load(R.drawable.felix)
                    .into(holder.album_art);
        }

        //player activity when clicked on song item in recyclerview
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        //menu more options
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.more_menu_song, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((item ) ->{
                    switch (item.getItemId()){
                        case R.id.delete:
                            Toast.makeText(mContext,"",Toast.LENGTH_SHORT).show();
                            delete(position, v);
                            break;
                    }
                    return true;
                });

            }
        });
    }

    //delete method
    private void delete(int position, View v) {
        mFiles.remove(position);

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView file_name;
        ImageView album_art,menuMore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            file_name = itemView.findViewById(R.id.tvMusicFileName);
            album_art = itemView.findViewById(R.id.ivMusic);
            menuMore = itemView.findViewById(R.id.ivMoreMenu);
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
