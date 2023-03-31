package com.example.gaanbajaw;

public class MusicFiles {

    private String path;
    private String tital;
    private String artist;
    private String album;
    private String duration;
    private String id;

    public MusicFiles(String path, String tital, String artist, String album, String duration,String id) {
        this.path = path;
        this.tital = tital;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.id = id;
    }

    public MusicFiles() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTital() {
        return tital;
    }

    public void setTital(String tital) {
        this.tital = tital;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
