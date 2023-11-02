package org.example;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class SimpleServer extends WebSocketServer {

    private final Gson gson;

    public SimpleServer(InetSocketAddress address) {
        super(address);
        gson = new Gson();
    }

    private Map<String, FileChannel> fileChannelMap = new HashMap<>();
    private Map<String, Long> timeOutMap = new HashMap<>();

    private Long start;
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

//        conn.send("Welcome to the server!"); //This method sends a message to the new client
//        broadcast("new connection: " + handshake.getResourceDescriptor()); //This method sends a message to all clients connected
//        System.out.println("new connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String fileName = message;
        if (fileChannelMap.containsKey(fileName)) {
            FileChannel fileChannel = fileChannelMap.get(fileName);
            try {
                fileChannel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fileChannelMap.remove(fileName);
            System.out.println(fileName + "finished");
//            System.out.println(System.currentTimeMillis() - start);
        } else {
            if (start == null) {
                start = System.currentTimeMillis();
            }
            File file = new File(fileName);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannelMap.put(fileName, fileChannel);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        try {
//            String json = ByteBufferUtil.getString(message);
//            BufferMetaData bufferMetaData = gson.fromJson(json, BufferMetaData.class);

            String fileName = ByteBufferUtil.getString(message);
            FileChannel fileChannel = fileChannelMap.get(fileName);
//            if (bufferMetaData.finished) {
//                fileChannel.close();
//                System.out.println(bufferMetaData.srcFileName + "finished");
//            } else {
//            }
            fileChannel.write(message);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println("received ByteBuffer from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("server started successfully");
    }


    public static void main(String[] args) {
        String host = "localhost";
        int port = 8887;

        WebSocketServer server = new SimpleServer(new InetSocketAddress(host, port));
        server.run();
    }
}