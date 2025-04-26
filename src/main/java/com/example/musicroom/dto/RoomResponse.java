package com.example.musicroom.dto;

import com.example.musicroom.model.Song;
import com.example.musicroom.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoomResponse {
    private List<Song> queue;
    private Song currentSong;
    private List<User> users;
}