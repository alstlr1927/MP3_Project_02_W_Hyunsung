package com.cookandroid.mp3_project_02_w_hyunsung;

public class MusicData {
    private String id;
    private String artist;
    private String title;
    private String albumCover;
    private String duration;
    private int liked;
    private int playCount;

    public MusicData(String id, String artist, String title, String albumCover, String duration, int playCount, int liked) {
        super();
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.albumCover = albumCover;
        this.duration = duration;
        this.playCount = playCount;
        this.liked = liked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof MusicData) {
            MusicData data = (MusicData) obj;
            equal = (this.id).equals(data.getId());
        }
        return equal;
    }

}
