package org.example;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

public class EmptyClient extends WebSocketClient {

    private ExecutorService executor;

    public EmptyClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public EmptyClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
//		send("Hello, it is me. Mario :)");
        System.out.println("new connection opened");
        executor = Executors.newCachedThreadPool();
        long start = System.currentTimeMillis();
        sendFile("C:\\Users\\GMJ\\Desktop\\ElephantsDream.mp4");
        sendFile("C:\\Users\\GMJ\\Desktop\\BigBuckBunny.mp4");
        long end = System.currentTimeMillis();
        System.out.println("total timeout: " + (end - start));
    }

    private void sendFileBatch(String filePath) {
        File file = new File(filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = fileInputStream.readAllBytes();
            send(bytes);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFile(final String filePath) {
        File file = new File(filePath);
        send(file.getName());
        sendFileBatch(filePath);
//		executor.execute(new Runnable() {
//			@Override
//			public void run() {
//				File file = new File(filePath);
//				send();
//				String id = UUID.randomUUID().toString();
//				TransportInfo transportInfo = new TransportInfo(id, file.getName());
//				BufferMetaData bufferMetaData = new BufferMetaData();
//				bufferMetaData.srcFileName = file.getName();
//				bufferMetaData.finished = false;
//				Gson gson = new Gson();
//				String json = gson.toJson(transportInfo);
//				send(file.getName());
//				try {
//					FileInputStream fileInputStream = new FileInputStream(file);
//					FileChannel fci = fileInputStream.getChannel();
//					ByteBuffer buffer = ByteBuffer.allocate(1024 * 4);
//
//					long start = System.currentTimeMillis();
//					System.out.println(String.format("send %s start, cur time is %d", file.getName(), start));
//					while (true) {
//						ByteBufferUtil.putString(buffer, file.getName());
//						int read = fci.read(buffer);
//						if (read == -1) {
//							send(file.getName());
//							break;
//						}
//						buffer.flip();
//						send(buffer);
//						buffer.clear();
//					}
//					long end = System.currentTimeMillis();
//					System.out.println(String.format("send %s end, cur time is %d, timeout is %d", file.getName(), end, end - start));
//
//				} catch (FileNotFoundException e) {
//					throw new RuntimeException(e);
//				} catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		});
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    public static void main(String[] args) throws URISyntaxException {
        WebSocketClient client = new EmptyClient(new URI("ws://localhost:8887"));
        client.connect();
    }
}