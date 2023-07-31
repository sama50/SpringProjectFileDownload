package com.example.springproject;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyWebSocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle WebSocket messages received from clients

        // System.out.println("Received WebSocket Message: " + message.getPayload());

        // Create a JSON-formatted response
        String jsonResponse = "{\"message\": \"" + "Success" + "\"}";
        TextMessage response = new TextMessage(jsonResponse);
        session.sendMessage(response);
    }
}
