package com.example.musicroom.service;

import com.example.musicroom.model.Room;
import com.example.musicroom.model.Song;
import com.example.musicroom.model.User;
import com.example.musicroom.model.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final Room room = new Room("music-room-password"); // Default password
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean authenticateUser(String username, String password) {
        // Check if password matches the room password
        if (!room.getPassword().equals(password)) {
            return false;
        }

        // Create new user if not exists
        if (!room.containsUser(username)) {
            User user = new User(username, password, true);
            room.addUser(user);
        }

        return true;
    }

    public User findUserByUsername(String username) {
        return room.getUsers().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public void registerSession(String username, WebSocketSession session) {
        sessions.put(session.getId(), session);
        sessionUserMap.put(session.getId(), username);
        broadcastUserList();
        sendQueueUpdate(session);
        sendCurrentSong(session);
    }

    public void removeSession(WebSocketSession session) {
        String username = sessionUserMap.get(session.getId());
        if (username != null) {
            room.removeUser(username);
            sessionUserMap.remove(session.getId());
        }
        sessions.remove(session.getId());
        broadcastUserList();
    }

    public void addSong(String url, String username) {
        Song song = new Song(url, username);
        room.addSong(song);
        broadcastQueueUpdate();
        broadcastCurrentSong();
    }

    public void songEnded(WebSocketSession session) {
        room.playNextSong();
        broadcastCurrentSong();
    }

    public Room getRoom() {
        return room;
    }

    private void broadcastUserList() {
        WebSocketMessage message = new WebSocketMessage(
                "USERS_UPDATE",
                room.getUsers().stream()
                        .map(User::getUsername)
                        .collect(Collectors.toList())
        );
        broadcast(message);
    }

    private void broadcastQueueUpdate() {
        WebSocketMessage message = new WebSocketMessage("QUEUE_UPDATE", room.getQueue());
        broadcast(message);
    }

    private void broadcastCurrentSong() {
        WebSocketMessage message = new WebSocketMessage("CURRENT_SONG", room.getCurrentSong());
        broadcast(message);
    }

    private void sendQueueUpdate(WebSocketSession session) {
        WebSocketMessage message = new WebSocketMessage("QUEUE_UPDATE", room.getQueue());
        sendToSession(session, message);
    }

    private void sendCurrentSong(WebSocketSession session) {
        WebSocketMessage message = new WebSocketMessage("CURRENT_SONG", room.getCurrentSong());
        sendToSession(session, message);
    }

    private void broadcast(WebSocketMessage message) {
        sessions.values().forEach(session -> sendToSession(session, message));
    }

    private void sendToSession(WebSocketSession session, WebSocketMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}