package com.example.mymusicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.data.model.Song;

import java.util.List;

public class SongAdapter extends BaseAdapter {

    private List<Song> songList;
    private LayoutInflater inflater;

    public SongAdapter(List<Song> songList) {
        this.songList = songList;
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.item_song, parent, false);
        }

        Song song = songList.get(position);

        // 获取歌名和歌手 TextView
        TextView songRank = convertView.findViewById(R.id.tvRank);
        TextView songTitle = convertView.findViewById(R.id.tvSongTitle);
        TextView artistName = convertView.findViewById(R.id.tvArtist);

        // 设置数据
        songRank.setText(String.valueOf(song.getRank()));
        songTitle.setText(song.getSongTitle());
        artistName.setText(song.getArtist());

        return convertView;
    }
}