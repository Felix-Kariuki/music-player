package com.flexcode.musicplayer.adapters;

import android.content.Context;
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
import com.flexcode.musicplayer.models.MusicFiles;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> albumFiles;
    View view; //object

    //constructor
    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumDetailsAdapter.MyHolder holder, int position) {
        holder.tvAlbumName.setText(albumFiles.get(position).getTitle());
        byte[] image = getAlbumArt(albumFiles.get(position).getPath());

        if (image != null) {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.ivAlbumImage);
        } else {
            Glide.with(mContext).load(R.drawable.felix)
                    .into(holder.ivAlbumImage);
        }
        //play when clicked on album item in recyclerview
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AlbumDetails.class);
                intent.putExtra("albumName", albumFiles.get(position).getAlbum());
                mContext.startActivity(intent);
            }
        });**/
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView ivAlbumImage;
        TextView tvAlbumName;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbumImage = itemView.findViewById(R.id.ivMusic);
            tvAlbumName = itemView.findViewById(R.id.tvMusicFileName);
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
