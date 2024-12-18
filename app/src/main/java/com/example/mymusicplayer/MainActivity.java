package com.example.mymusicplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mymusicplayer.activity.LoginActivity;
import com.example.mymusicplayer.activity.PlayerActivity;
import com.example.mymusicplayer.activity.PlaylistActivity;
import com.example.mymusicplayer.common.UserSessionManager;
import com.example.mymusicplayer.data.model.Song;
import com.example.mymusicplayer.fragment.NewMusicFragment;
import com.example.mymusicplayer.fragment.RankFragment;
import com.example.mymusicplayer.fragment.SongListFragment;
import com.example.mymusicplayer.service.MusicService;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ImageView ivPlayPause, ivSongCover, ivNext, ivPlayback, ivPlaylist;
    private TextView tvSongInfo;
    private TabLayout tlTabMenu;

    private MusicService musicService;
    private boolean isBound = false;
    private UserSessionManager sessionManager;

    private BroadcastReceiver playbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_PLAYBACK_COMPLETE".equals(intent.getAction())) {
                Log.d("PlaybackReceiver-Main", "接收到本地广播 <=== 播放结束");
//                ivPlayPause.setImageResource(R.drawable.ic_play);
                // 更新歌曲信息。
                loadMusicInfo();
//                seekBar.setMax(musicService.getDuration());
            } else if ("ACTION_PLAYBACK_OVER".equals(intent.getAction())) {
                Log.d("PlaybackReceiver-Main", "接收到本地广播 <=== 播放列表结束");
                ivPlayPause.setImageResource(R.drawable.ic_play);
                Toast.makeText(context, "播放列表完成", Toast.LENGTH_LONG).show();
            }
        }
    };
    private boolean isReceiverRegistered = false;

    // ServiceConnection 来接收服务的回调
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;

            // 初始化视图组件
            initViews();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 建立服务
        Intent indent = new Intent(this, MusicService.class);
        ComponentName ms = startService(indent); // 启动服务
        bindService(indent, serviceConnection, BIND_AUTO_CREATE);

        // 注册广播接收器
        if (!isReceiverRegistered) {
            registerPlaybackReceiver();
            isReceiverRegistered = true;
        }

        // 注册用户会话管理器
        sessionManager = new UserSessionManager(getApplicationContext());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (musicService != null) {
            musicService.updatePlaylist();
            initBottomPlayer();
        }
    }

    public void initViews() {
        // todo toolbar
        ImageView profile = findViewById(R.id.toolbar_title_profile);

        // 登陆页的跳转
        profile.setOnClickListener(v -> {
            if (sessionManager.getUsername() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "当前用户：" + sessionManager.getUsername(), Toast.LENGTH_LONG).show();
            }
        });

        profile.setOnLongClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                sessionManager.logout();
                Toast.makeText(this, "已退出登陆", Toast.LENGTH_LONG).show();
                recreate();
            }
            return true;
        });

        // tab栏
        tlTabMenu = findViewById(R.id.tab_layout);
        // 初始化tab栏
        initTabMenu();

        // 歌曲信息
        ivSongCover = findViewById(R.id.iv_song_cover);
        tvSongInfo = findViewById(R.id.tv_song_info);
        ivPlayback = findViewById(R.id.iv_playback_method);
        ivPlaylist = findViewById(R.id.iv_playlist);
        loadMusicInfo();

        // 初始化底部播放器
        initBottomPlayer();
    }

    public void initTabMenu() {

        // 初始化时直接加载新曲区
        NewMusicFragment newMusicFragment = NewMusicFragment.newInstance();
        loadFragmentContainer(newMusicFragment);

        // 添加点击事件(修改帧布局)
        tlTabMenu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 根据 Tab 的位置加载不同的 Fragment
                switch (tab.getPosition()) {
                    // 新曲
                    case 0:
                        NewMusicFragment newMusicFragment = NewMusicFragment.newInstance();
                        loadFragmentContainer(newMusicFragment);
                        break;
                    case 1:
                        SongListFragment songListFragment = SongListFragment.newInstance();
                        loadFragmentContainer(songListFragment);
                        break;
                    //排行榜
                    case 2:
                        RankFragment rankFragment = RankFragment.newInstance(musicService);
                        loadFragmentContainer(rankFragment);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void loadFragmentContainer(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void registerPlaybackReceiver() {
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_PLAYBACK_COMPLETE");
        filter.addAction("ACTION_PLAYBACK_OVER");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(playbackReceiver, filter);
        Log.d("LocalBroadcast", "注册本地广播接收器");
    }


    // 初始化底部播放器
    private void initBottomPlayer() {
        // 歌曲信息获取
        loadMusicInfo();

        // 播放方式改变(默认循环播放)
        if (musicService.isLoop()) {
            ivPlayback.setImageResource(R.drawable.ic_loop_playback);
        } else {
            ivPlayback.setImageResource(R.drawable.ic_sequence_playback);
        }
        ivPlayback.setOnClickListener(v -> {
            musicService.switchLoopType();
            if (musicService.isLoop()) {
                ivPlayback.setImageResource(R.drawable.ic_loop_playback);
            } else {
                ivPlayback.setImageResource(R.drawable.ic_sequence_playback);
            }
        });

        // 播放列表
        ivPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlaylistActivity.class);
            startActivity(intent);
        });

        // 底部播放器的监听（点击封面图跳转）
        ivSongCover.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerActivity.class);
            startActivity(intent);
//            finish(); // 关闭 MainActivity
        });

        // 初始化播放按钮
        ivPlayPause = findViewById(R.id.iv_play_pause);
        // 按钮图片初始化
        if (musicService != null && musicService.isPlaying()) {
            ivPlayPause.setImageResource(R.drawable.ic_pause); // 设置暂停按钮的图片
        } else {
            ivPlayPause.setImageResource(R.drawable.ic_play); // 设置播放按钮图片
        }
        // 监听点击事件
        ivPlayPause.setOnClickListener(v -> {
            if (musicService != null) {
                if (musicService.isPlaying()) {
                    musicService.pause();
                    ivPlayPause.setImageResource(R.drawable.ic_play);
                } else {
                    musicService.resume();
                    ivPlayPause.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        ivNext = findViewById(R.id.iv_next);
        ivNext.setOnClickListener(v -> {
            if (musicService != null) {
                musicService.nextSong();
                // 更新歌曲信息。
                loadMusicInfo();
            }
        });
    }

    public void loadMusicInfo() {
        Song song = musicService.getCurrentSong();
        if (song == null) {
            return;
        }
        ivSongCover.setImageResource(song.getCoverId());
        tvSongInfo.setText(song.getSongInfo());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(playbackReceiver);
            isReceiverRegistered = false;
        }
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        stopService(new Intent(this, MusicService.class)); // 停止服务
    }
}