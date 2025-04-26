package com.example.musicroom.handler;

import com.example.musicroom.model.WebSocketMessage;
import com.example.musicroom.security.JwtService;
import com.example.musicroom.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final RoomService roomService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketHandler(RoomService roomService, JwtService jwtService) {
        this.roomService = roomService;
        this.jwtService = jwtService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractTokenFromUri(session);
        if (token != null && jwtService.validateJwtToken(token)) {
            String username = jwtService.getUsernameFromJwtToken(token);
            roomService.registerSession(username, session);
        } else {
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketMessage webSocketMessage = objectMapper.readValue(message.getPayload(), WebSocketMessage.class);

        if ("SONG_ENDED".equals(webSocketMessage.getType())) {
            roomService.songEnded(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        roomService.removeSession(session);
    }

    private String extractTokenFromUri(WebSocketSession session) {
        String uri = session.getUri().toString();
        Map<String, String> params = UriComponentsBuilder.fromUriString(uri)
                .build()
                .getQueryParams()
                .toSingleValueMap();
        return params.get("token");
    }
}
