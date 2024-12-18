package com.example.mymusicplayer.data.model;

import com.example.mymusicplayer.R;

public class Song {
    private String songTitle;
    private String artist;
    private int rank;
    private int resourceId = -1;
    private int coverId = R.drawable.ic_library;

    public Song() {
    }

    public String getSongInfo() {
        return songTitle + " - " + artist;
    }

    public static Song songRank(String songTitle, String artist, int rank) {
        Song song = new Song();
        song.songTitle = songTitle;
        song.artist = artist;
        song.rank = rank;
        return song;
    }

    public static Song songRank(String songTitle, String artist, int rank, int resourceId) {
        Song song = new Song();
        song.songTitle = songTitle;
        song.artist = artist;
        song.rank = rank;
        song.resourceId = resourceId;
        return song;
    }

    public Song(String songTitle, String artist, int resourceId) {
        this.songTitle = songTitle;
        this.artist = artist;
        this.resourceId = resourceId;
    }

    public Song(String songTitle, String artist, int resourceId, int coverId) {
        this.songTitle = songTitle;
        this.artist = artist;
        this.resourceId = resourceId;
        this.coverId = coverId;
    }

    public Song(String songTitle, String artist, int rank, int resourceId, int coverId) {
        this.songTitle = songTitle;
        this.artist = artist;
        this.rank = rank;
        this.resourceId = resourceId;
        this.coverId = coverId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public int getRank() {
        return rank;
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getCoverId() {
        return coverId;
    }
}
