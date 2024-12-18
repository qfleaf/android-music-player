package com.example.mymusicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.data.model.SongList;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    private List<SongList> songLists;

    public SongListAdapter(List<SongList> songLists) {
        this.songLists = songLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongList songList = songLists.get(position);
        holder.tvSongName.setText(songList.getName());
        holder.tvPlayCount.setText(songList.getPlayCount() + "次播放");
        holder.ivSongCover.setImageResource(songList.getCoverResourceId());
    }

    @Override
    public int getItemCount() {
        return songLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSongCover;
        TextView tvSongName, tvPlayCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSongCover = itemView.findViewById(R.id.iv_song_cover);
            tvSongName = itemView.findViewById(R.id.tv_song_name);
            tvPlayCount = itemView.findViewById(R.id.tv_play_count);
        }
    }
}