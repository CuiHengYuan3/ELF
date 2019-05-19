package com.example.lenovo.elf.util;

import java.io.Serializable;

public class SongListModel implements Serializable {


    private  String songName;
 private String singerName;
 private  String picUrl;
 private int   songID;
 private boolean isBeCollected=false;//默认没有被收藏

    public boolean isBeCollected() {
        return isBeCollected;
    }

    public void setBeCollected(boolean beCollected) {
        isBeCollected = beCollected;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }
}