package com.georgebanin.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;

@ServerEndpoint("/chat/{hostname}")
@ApplicationScoped
@Slf4j
public class ChatWebSocket {

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("hostname") String hostname) {
        log.info("Host {} joined with session ID {}", hostname, session.getId());
        broadcast("Host " + hostname + " joined");
        sessions.put(hostname, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("hostname") String hostname) {
        log.info("Host {} left with session ID {}", hostname, session.getId());
        sessions.remove(hostname);
        broadcast("User " + hostname + " left");
    }

    @OnError
    public void onError(Session session, @PathParam("hostname") String hostname, Throwable throwable) {
        log.error("Error for host {} with session ID {}: {}", hostname, session.getId(), throwable.getMessage());
        sessions.remove(hostname);
        broadcast("User " + hostname + " left on error: " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("hostname") String hostname) {
        log.info("Message from {}: {}", hostname, message);
        broadcast(">> " + hostname + ": " + message);
    }

    public void broadcast(String message) {
        log.debug("Broadcasting message: {}", message);
        sessions.values().forEach(s -> s.getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null) {
                log.error("Unable to send message: {}", result.getException().getMessage());
            }
        }));
    }

}