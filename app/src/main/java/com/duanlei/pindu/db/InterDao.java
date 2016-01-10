package com.duanlei.pindu.db;

import com.duanlei.pindu.model.GalleryItem;

/**
 * Created by duanlei on 16/1/10.
 */
public interface InterDao {

    void add(GalleryItem galleryItem);

    void delete(int id);

    GalleryItem read(int id);

    void update(GalleryItem galleryItem);
}
