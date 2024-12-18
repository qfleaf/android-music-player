package com.example.mymusicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.data.model.Song;
import com.example.mymusicplayer.service.MusicService;

import java.util.List;

public class RankAdapter extends BaseAdapter {

    private List<Song> songList;
    private Context context;
    private MusicService musicService;

    public RankAdapter(Context context, List<Song> songList, MusicService musicService) {
        this.context = context;
        this.songList = songList;
        this.musicService = musicService;
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
        // 获取歌曲对象
        Song song = songList.get(position);

        // 使用LayoutInflater将布局文件加载到视图中
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        }

        // 获取各个控件并绑定数据
        TextView tvRank = convertView.findViewById(R.id.tvRank);
        TextView tvSongTitle = convertView.findViewById(R.id.tvSongTitle);
        TextView tvArtist = convertView.findViewById(R.id.tvArtist);

        tvRank.setText(String.valueOf(song.getRank()));  // 设置排名
        tvSongTitle.setText(song.getSongTitle());  // 设置歌曲名称
        tvArtist.setText(song.getArtist());  // 设置歌手名称

        convertView.setOnClickListener(v -> {
            if (song.getResourceId() != -1) {
                musicService.addSongToPlaylist(song);
                Toast.makeText(context, "已添加到播放列表", Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }
}
