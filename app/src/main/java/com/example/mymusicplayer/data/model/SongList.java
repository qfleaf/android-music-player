package com.example.mymusicplayer.data.model;

public class SongList {
    private String name;
    private int playCount;
    private int coverResourceId;

    public SongList(String name, int playCount, int coverResourceId) {
        this.name = name;
        this.playCount = playCount;
        this.coverResourceId = coverResourceId;
    }

    public String getName() {
        return name;
    }

    public int getPlayCount() {
        return playCount;
    }

    public int getCoverResourceId() {
        return coverResourceId;
    }
}