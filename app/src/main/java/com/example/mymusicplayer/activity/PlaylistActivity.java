package com.example.mymusicplayer.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.adapter.SongAdapter;
import com.example.mymusicplayer.data.model.Song;
import com.example.mymusicplayer.service.MusicService;

import java.util.List;

public class PlaylistActivity extends Activity {

    private List<Song> playList;
    private ListView listViewPlaylist;
    private SongAdapter songAdapter;

    private MusicService musicService;
    private boolean isBound = false;

    // ServiceConnection 来接收服务的回调
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
            // 初始化列表
            initSongList();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // 获取返回按钮并设置点击事件
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(view -> {
            finish();
        });
    }

    // 初始化歌曲列表数据
    private void initSongList() {
        playList = musicService.getPlayList();
        // 初始化 ListView 适配器
        songAdapter = new SongAdapter(playList);
        listViewPlaylist = findViewById(R.id.listview_playlist);
        listViewPlaylist.setAdapter(songAdapter);

        // 设置 ListView 的点击事件，点击列表项时播放音乐
        listViewPlaylist.setOnItemClickListener((parent, view, position, id) -> {
            Song clickedSong = playList.get(position);
            Toast.makeText(this, "开始播放：" + clickedSong.getSongInfo(), Toast.LENGTH_SHORT).show();
            musicService.changeSongFromPlaylist(clickedSong.getRank() - 1);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 建立服务
        Intent indent = new Intent(this, MusicService.class);
        ComponentName ms = startService(indent); // 启动服务
        bindService(indent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (musicService != null) {
            initSongList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        stopService(new Intent(this, MusicService.class)); // 停止服务
    }
}