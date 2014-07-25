package com.ig.igwebcms.core.model;

import java.util.Date;

public class VedioProperties {
    private String videoId;
    private String title;
    private String thumbnail;
    private String publishDate;
    public String getPublishDate() {
        return publishDate;
    }
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
    public String getVideoId() {
        return videoId;
    }
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public VedioProperties(String videoId, String title, String thumbnail,String publishDate) {
        super();
        this.videoId = videoId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.publishDate = publishDate;

    }


}
