package com.example.mymusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mymusicplayer.common.UserSessionManager;
import com.example.mymusicplayer.data.dao.PlaylistDAO;
import com.example.mymusicplayer.data.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private LocalBroadcastManager localBroadcastManager;
    private MediaPlayer mediaPlayer;
    private int currentPosition = 0;
    private boolean isPlaying = false;
    private boolean isLoop = true;

    private List<Song> playList = new ArrayList<>();
    private int currentSongIndex = 0;

    private PlaylistDAO playlistDAO; // 数据库访问对象

    private UserSessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化广播管理器
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        // 初始化用户会话管理器
        sessionManager = new UserSessionManager(getApplicationContext());
        // 初始化数据库访问对象
        playlistDAO = new PlaylistDAO(this);
        if (sessionManager.isLoggedIn()) {
            playList = playlistDAO.getAllSongs(sessionManager.getUserId());
        } else {
            playList = playlistDAO.getAllSongs(0);
        }
        // 从数据库加载播放列表
        if (playList.isEmpty()) {
            Log.d("MusicService", "播放列表为空，添加示例歌曲");
//            playlistDAO.addSong(new Song("稻香", "周杰伦", 1, R.raw.sample_music, R.drawable.ic_album_cover), 0);
//            playlistDAO.addSong(new Song("花海", "周杰伦", 2, R.raw.sample_music2, R.drawable.ic_album_cover2), 0);
//            playlistDAO.addSong(new Song("明明就", "周杰伦", 3, R.raw.sample_music3, R.drawable.ic_album_cover3), 0);
            playList = playlistDAO.getAllSongs(0);
        }
        // 初始化MediaPlayer
        if (!playList.isEmpty() && mediaPlayer == null) {
            initMediaPlayer(playList.get(currentSongIndex).getResourceId());
        }
    }

    private void initMediaPlayer(int resourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, resourceId);
        mediaPlayer.setOnCompletionListener(mp -> {
            // 音乐播放完成广播
            Intent completedBroadcastIntent = new Intent("ACTION_PLAYBACK_COMPLETE");
            localBroadcastManager.sendBroadcast(completedBroadcastIntent);
            nextSong();
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isPlaying && mediaPlayer != null) {
            mediaPlayer.seekTo(currentPosition); // 恢复播放进度
//            mediaPlayer.start();
//            isPlaying = true;
        }
        // 返回START_STICKY，表示即使Activity销毁，Service也继续运行
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            currentPosition = mediaPlayer.getCurrentPosition(); // 保存播放进度
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
//        return null;
        return new LocalBinder(); // 如果不需要绑定，可以返回null
    }

    public void updatePlaylist() {
        playList = playlistDAO.getAllSongs(sessionManager.getUserId());
    }

    // 提供一个本地绑定器来访问service
    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public int getDuration() {
        return mediaPlayer == null ? 0 : mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void switchLoopType() {
        isLoop = !isLoop;
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            currentPosition = mediaPlayer.getCurrentPosition();
            isPlaying = false;
        }
    }

    public void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    public void seekTo(int position) {
        currentPosition = position;
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    // 切换音乐源
    public void setMusicSource(int resourceId) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + resourceId));
                mediaPlayer.prepare();
                mediaPlayer.start();
                isPlaying = true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 上一曲
    public void previousSong() {
        if (currentSongIndex > 0) {
            seekTo(0);
            currentSongIndex--;
        } else if (isLoop) {
            currentSongIndex = playList.size() - 1;
        }
        playCurrentSong();
    }

    // 下一曲
    public void nextSong() {
        if (currentSongIndex < playList.size() - 1) {
            seekTo(0); // 重置进度条位置
            currentSongIndex++;
        } else if (isLoop) {
            currentSongIndex = 0;
        } else {
            // 发送广播通知结束播放列表
            Intent overBroadcastIntent = new Intent("ACTION_PLAYBACK_OVER");
            localBroadcastManager.sendBroadcast(overBroadcastIntent);
            isPlaying = false;
            seekTo(0);
            return;
        }
        playCurrentSong();
    }

    // 播放当前歌曲
    private void playCurrentSong() {
        if (!playList.isEmpty()) {
            Song currentSong = playList.get(currentSongIndex);
            setMusicSource(currentSong.getResourceId());
        }
    }

    public Song getCurrentSong() {
        return playList.isEmpty() ? null : playList.get(currentSongIndex);
    }

    // 向播放列表添加歌曲
    public void addSongToPlaylist(Song song) {
        int currentResourceId = getCurrentSong().getResourceId();
        playlistDAO.deleteSongByResourceId(song.getResourceId(), sessionManager.getUserId());
        playlistDAO.addSong(song, sessionManager.getUserId());
        playList = playlistDAO.getAllSongs(sessionManager.getUserId());
        playList.stream()
                .filter(s -> currentResourceId == s.getResourceId())
                .findFirst()
                .ifPresent(s -> currentSongIndex = s.getRank() - 1);
    }

    public void changeSongFromPlaylist(int songIndex) {
        currentSongIndex = songIndex;
        playCurrentSong();
    }

    // 获取播放列表
    public List<Song> getPlayList() {
        return playList;
    }
}