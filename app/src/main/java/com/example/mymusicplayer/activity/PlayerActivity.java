package com.example.mymusicplayer.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.data.model.Song;
import com.example.mymusicplayer.service.MusicService;

public class PlayerActivity extends AppCompatActivity {

    private ImageView ivPlay, ivPrevious, ivNext, ivAlbumCover;
    private TextView tvSongTitle, tvArtist;
    private SeekBar seekBar;
    private Handler handler = new Handler();

    private MusicService musicService;
    private boolean isBound = false;

    private BroadcastReceiver playbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_PLAYBACK_COMPLETE".equals(intent.getAction())) {
                Log.d("PlaybackReceiver-Player", "接收到本地广播 <=== 播放结束");
//                ivPlay.setImageResource(R.drawable.ic_play);
                // 更新歌曲信息
                loadMusicInfo();
            } else if ("ACTION_PLAYBACK_OVER".equals(intent.getAction())) {
                Log.d("PlaybackReceiver-Player", "接收到本地广播 <=== 播放列表完毕");
                ivPlay.setImageResource(R.drawable.ic_play);
//                Toast.makeText(context, "播放完毕", Toast.LENGTH_LONG).show();
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

            // 初始化音乐播放控件
            initMediaPlayer();
            // 更新播放进度条
            updateSeekBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // 建立服务
        Intent indent = new Intent(this, MusicService.class);
        ComponentName ms = startService(indent); // 启动服务
        bindService(indent, serviceConnection, BIND_AUTO_CREATE);

        // 注册广播接收器
        if (!isReceiverRegistered) {
            registerPlaybackReceiver();
            isReceiverRegistered = true;
        }

        // 初始化返回按扭
        View back = findViewById(R.id.iv_back);
        back.setOnClickListener(v -> {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
            finish(); // 关闭
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (musicService != null) {
            initMediaPlayer();
        }
    }

    private void registerPlaybackReceiver() {
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_PLAYBACK_COMPLETE");
        filter.addAction("ACTION_PLAYBACK_OVER");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(playbackReceiver, filter);
        Log.d("LocalBroadcast", "注册本地广播接收器");
    }

    public void initMediaPlayer() {
        // 初始化控件
        ivPlay = findViewById(R.id.btn_play);
        ivPrevious = findViewById(R.id.btn_previous);
        ivNext = findViewById(R.id.btn_next);
        ivAlbumCover = findViewById(R.id.iv_album_cover);
        seekBar = findViewById(R.id.seekBar);
        tvSongTitle = findViewById(R.id.tv_song_title);
        tvArtist = findViewById(R.id.tv_artist);

        // 设置歌曲信息
//        tvSongTitle.setText("稻香");
//        tvArtist.setText("Jay Chou");
        loadMusicInfo();


        // 初始化 MediaPlayer
//        mediaPlayer = MediaPlayer.create(this, R.raw.sample_music); // 替换为你的音乐文件
//        seekBar.setMax(musicService.getDuration());

        // 按钮图片初始化
        if (musicService.isPlaying()) {
            ivPlay.setImageResource(R.drawable.ic_pause); // 设置暂停按钮的图片
        } else {
            ivPlay.setImageResource(R.drawable.ic_play); // 设置播放按钮图片
        }
        // 播放按钮点击事件
        ivPlay.setOnClickListener(v -> {
            if (musicService != null) {
                if (musicService.isPlaying()) {
                    musicService.pause();
                    ivPlay.setImageResource(R.drawable.ic_play);
                } else {
                    musicService.resume();
                    ivPlay.setImageResource(R.drawable.ic_pause);
                    updateSeekBar();
                }
            }
        });

        // 下一曲按钮点击事件
        ivNext.setOnClickListener(v -> {
            musicService.nextSong();
            initMediaPlayer();
//            loadMusicInfo();
        });

        //上一曲按钮点击事件
        ivPrevious.setOnClickListener(v -> {
            musicService.previousSong();
            // 更新歌曲信息
            initMediaPlayer();
//            loadMusicInfo();
            if (musicService.isPlaying()) {
                ivPlay.setImageResource(R.drawable.ic_pause); // 设置暂停按钮的图片
            } else {
                musicService.resume();
            }
        });

        // 进度条监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

//        // 播放完毕监听
//        musicPlayer.setOnCompletionListener(mp -> {
//            ivPlay.setImageResource(R.drawable.ic_play);
//        });

    }

    private void loadMusicInfo() {
        Song song = musicService.getCurrentSong();
        if (song == null) {
            return;
        }
        tvSongTitle.setText(song.getSongTitle());
        tvArtist.setText(song.getArtist());
        seekBar.setMax(musicService.getDuration());
        seekBar.setProgress(musicService.getCurrentPosition());
        ivAlbumCover.setImageResource(song.getCoverId());
    }

    // 更新进度条
    private void updateSeekBar() {
        if (isBound && musicService.isPlaying()) {
            seekBar.setProgress(musicService.getCurrentPosition());
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注册广播接收器
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(playbackReceiver);
            isReceiverRegistered = false;
        }

        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        stopService(new Intent(this, MusicService.class));
    }
}