package com.example.mymusicplayer.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mymusicplayer.data.DBHelper;
import com.example.mymusicplayer.data.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {

    private SQLiteDatabase db;

    public PlaylistDAO(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // 添加歌曲到播放列表（指定用户）
    public long addSong(Song song, int userId) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_SONG_TITLE, song.getSongTitle());
        values.put(DBHelper.COLUMN_ARTIST, song.getArtist());
        values.put(DBHelper.COLUMN_RESOURCE_ID, song.getResourceId());
        values.put(DBHelper.COLUMN_COVER_ID, song.getCoverId());
        values.put(DBHelper.COLUMN_USER_ID, userId); // 关联用户ID
        return db.insert(DBHelper.TABLE_PLAYLIST, null, values);
    }

    // 获取指定用户的播放列表
    @SuppressLint("Range")
    public List<Song> getAllSongs(int userId) {
        List<Song> songList = new ArrayList<>();

        String selection = DBHelper.COLUMN_USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(
                DBHelper.TABLE_PLAYLIST,
                null,
                selection,
                selectionArgs,
                null, null,
                DBHelper.COLUMN_ID + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SONG_TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ARTIST));
                int rank = cursor.getPosition() + 1; // 自动生成排名
                int resourceId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_RESOURCE_ID));
                int coverId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_COVER_ID));

                songList.add(new Song(title, artist, rank, resourceId, coverId));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return songList;
    }

    // 删除指定用户的歌曲
    public void deleteSongByResourceId(int resourceId, int userId) {
        String whereClause = DBHelper.COLUMN_RESOURCE_ID + "=? AND " + DBHelper.COLUMN_USER_ID + "=?";
        String[] whereArgs = {String.valueOf(resourceId), String.valueOf(userId)};
        db.delete(DBHelper.TABLE_PLAYLIST, whereClause, whereArgs);
    }

    // 清空指定用户的播放列表
    public void clearPlaylist(int userId) {
        String whereClause = DBHelper.COLUMN_USER_ID + "=?";
        String[] whereArgs = {String.valueOf(userId)};
        db.delete(DBHelper.TABLE_PLAYLIST, whereClause, whereArgs);
    }

    // 关闭数据库连接
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}