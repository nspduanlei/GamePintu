package com.duanlei.pindu.model;

/**
 * Author: duanlei
 * Date: 2015-11-10
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    private String mShowUrl;

    public String toString() {
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getShowUrl() {
        return mShowUrl;
    }

    public void setShowUrl(String showUrl) {
        mShowUrl = showUrl;
    }
}
