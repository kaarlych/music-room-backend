package com.example.musicroom.model;

import lombok.Data;
import java.util.UUID;

@Data
public class Song {
    private String id;
    private String url;
    private String videoId;
    private String title;
    private String addedBy;

    public Song(String url, String addedBy) {
        this.id = UUID.randomUUID().toString();
        this.url = url;
        this.addedBy = addedBy;
        this.videoId = extractVideoId(url);
    }

    private String extractVideoId(String url) {
        // Extract video ID from URL formats like youtube.com/watch?v=VIDEO_ID or youtu.be/VIDEO_ID
        if (url.contains("youtube.com/watch")) {
            int startIndex = url.indexOf("v=") + 2;
            int endIndex = url.indexOf("&", startIndex);
            if (endIndex == -1) {
                endIndex = url.length();
            }
            return url.substring(startIndex, endIndex);
        } else if (url.contains("youtu.be/")) {
            int startIndex = url.lastIndexOf("/") + 1;
            int endIndex = url.indexOf("?", startIndex);
            if (endIndex == -1) {
                endIndex = url.length();
            }
            return url.substring(startIndex, endIndex);
        }
        // If no recognized format, return original URL
        return url;
    }
}