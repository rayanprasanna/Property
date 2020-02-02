package com.universl.realestate.response;

import android.support.annotation.NonNull;

public class ImageResponse {
    public boolean success;
    public int status;
    public UploadedImage data;

    public static class UploadedImage{
        public String id;
        public String title;
        public String description;
        public String type;
        boolean animated;
        public int width;
        int height;
        public int size;
        int views;
        int bandwidth;
        String vote;
        public boolean favorite;
        String account_url;
        String deletehash;
        public String name;
        public String link;

        @NonNull
        @Override public String toString() {
            return "UploadedImage{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", animated=" + animated +
                    ", width=" + width +
                    ", height=" + height +
                    ", size=" + size +
                    ", views=" + views +
                    ", bandwidth=" + bandwidth +
                    ", vote='" + vote + '\'' +
                    ", favorite=" + favorite +
                    ", account_url='" + account_url + '\'' +
                    ", deletehash='" + deletehash + '\'' +
                    ", name='" + name + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }

    @NonNull
    @Override public String toString() {
        return "ImageResponse{" +
                "success=" + success +
                ", status=" + status +
                ", data=" + data.toString() +
                '}';
    }
}

