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

@ServerEndpoint("/chat/{hostname}")
@ApplicationScoped
public class ChatWebSocket {

    Map<String, Session> sessions = new ConcurrentHashMap<>();



    @OnOpen
    public void onOpen(Session session, @PathParam("hostname") String hostname) {
//        broadcast("Host " + hostname + " joined");
        sessions.put(hostname, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("hostname") String hostname) {
        sessions.remove(hostname);
        broadcast("User " + hostname + " left");
    }

    @OnError
    public void onError(Session session, @PathParam("hostname") String hostname, Throwable throwable) {
        sessions.remove(hostname);
        broadcast("User " + hostname + " left on error: " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("hostname") String hostname) {
        broadcast(">> " + hostname + ": " + message);
    }

    public void broadcast(String message) {

        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }

            });
        });
    }

}