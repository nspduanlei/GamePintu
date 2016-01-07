package com.duanlei.pindu.network;

import android.net.Uri;

import com.duanlei.pindu.model.GalleryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * 获取贴图库中的数据
 * <p/>
 * Author: duanlei
 * Date: 2015-11-06
 */
public class TieTuKuFetcher {


    private static final String ENDPOINT_NEW = "http://api.tietuku.com/v2/api/getnewpic";

    private static final String ENDPOINT_RANDRE = "http://api.tietuku.com/v2/api/getrandrec";

    private static final String API_KEY = "nZmYnsdnZW2UyJqSkmGbw5Nol5aWl5JnmGNqY2aVcGpoa56Vx2eXZcOblWaZY50=";

    public static final String PREF_SEARCH_QUERY = "searchQuery";
    public static final String PREF_LAST_RESULT_ID = "lastResultId";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * 获取图片列表
     *
     * @return
     */
    public ArrayList<GalleryItem> fetchItems(int pageIndex) {
        String url = Uri.parse(ENDPOINT_NEW).buildUpon()
                .appendQueryParameter("key", API_KEY)
                .appendQueryParameter("returntype", "json")
                .appendQueryParameter("p", String.valueOf(pageIndex))
                .appendQueryParameter("cid", "1")
                .build().toString();
        return downloadGalleryItems(url);
    }

    /**
     * 搜索图片
     *
     * @param query
     * @return
     */
    public ArrayList<GalleryItem> search(String query) {
        String url = Uri.parse(ENDPOINT_RANDRE).buildUpon()
                .appendQueryParameter("key", API_KEY)
                .appendQueryParameter("returntype", "json")
//                .appendQueryParameter("p", "1")
//                .appendQueryParameter("cid", "1")
                .build().toString();

        return downloadGalleryRandomItems(url);
    }

    void parseItems(ArrayList<GalleryItem> items, JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject objItem = jsonArray.getJSONObject(i);
            String id = objItem.optString("id");
            String caption = objItem.optString("name");
            //String linkurl = handlerImageUrl(objItem.optString("linkurl"));

            String linkurl = objItem.optString("linkurl");

            String showurl = objItem.optString("showurl");
            GalleryItem item = new GalleryItem();
            item.setId(id);
            item.setCaption(caption);
            item.setUrl(linkurl);
            item.setShowUrl(showurl);
            items.add(item);
        }
    }

    /**
     * 获取网络内容解析
     *
     * @param url
     * @return
     */
    public ArrayList<GalleryItem> downloadGalleryItems(String url) {
        ArrayList<GalleryItem> items = new ArrayList<>();
        try {
            String jsonString = getUrl(url);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("pic");
            parseItems(items, jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 随机图片解析
     *
     * @param url
     * @return
     */
    public ArrayList<GalleryItem> downloadGalleryRandomItems(String url) {
        ArrayList<GalleryItem> items = new ArrayList<>();
        try {
            String jsonString = getUrl(url);
            JSONArray jsonArray = new JSONArray(jsonString);
            parseItems(items, jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

//    /**
//     * 图片url处理，贴图库如何直接获得缩略图，需要将url处理后得到
//     */
//    public String handlerImageUrl(String url) {
//
//        int t = url.lastIndexOf(".");
//        return url.substring(0, t) + "t" + url.substring(t, url.length());
//    }
}
