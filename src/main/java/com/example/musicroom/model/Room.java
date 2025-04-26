package com.example.musicroom.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class Room {
    private String password;
    private List<User> users = new CopyOnWriteArrayList<>();
    private List<Song> queue = new CopyOnWriteArrayList<>();
    private Song currentSong;

    public Room(String password) {
        this.password = password;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(String username) {
        users.removeIf(user -> user.getUsername().equals(username));
    }

    public boolean containsUser(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public void addSong(Song song) {
        queue.add(song);
        if (currentSong == null && queue.size() == 1) {
            playNextSong();
        }
    }

    public void playNextSong() {
        if (!queue.isEmpty()) {
            currentSong = queue.remove(0);
        } else {
            currentSong = null;
        }
    }
}