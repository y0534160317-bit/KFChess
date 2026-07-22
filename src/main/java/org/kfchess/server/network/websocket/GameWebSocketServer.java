package org.kfchess.server.network.websocket;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class GameWebSocketServer extends WebSocketServer {

    public GameWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client connected: "
                + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code,
                        String reason,
                        boolean remote) {

        System.out.println("Client disconnected.");
    }

    @Override
    public void onMessage(WebSocket conn,
                          String message) {

        System.out.println("Received: " + message);
    }

    @Override
    public void onError(WebSocket conn,
                        Exception ex) {

        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started.");
    }
}