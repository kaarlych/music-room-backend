package com.example.musicroom.controller;

import com.example.musicroom.dto.AddSongRequest;
import com.example.musicroom.dto.RoomResponse;
import com.example.musicroom.model.Room;
import com.example.musicroom.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<RoomResponse> getRoomData(Authentication authentication) {
        Room room = roomService.getRoom();
        RoomResponse response = new RoomResponse(
                room.getQueue(),
                room.getCurrentSong(),
                room.getUsers()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/queue")
    public ResponseEntity<?> addSong(@RequestBody AddSongRequest request, Authentication authentication) {
        roomService.addSong(request.getUrl(), authentication.getName());
        return ResponseEntity.ok("Song added to queue");
    }
}