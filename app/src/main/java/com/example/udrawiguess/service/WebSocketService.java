package com.example.udrawiguess.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.udrawiguess.Util;
import com.example.udrawiguess.fragment.RoomFragment;
import com.example.udrawiguess.socket.Listener;
import com.example.udrawiguess.socket.MessageType;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class WebSocketService extends Service {
    private Listener listener;
    private OkHttpClient client;
    private WebSocket webSocket;
    private InnerIBinder binder = new InnerIBinder();

    public WebSocketService() {
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    @Override
    public void onCreate() {
        client = new OkHttpClient();
        Request request = Util.newRequest("ws://47.254.242.71/udrawiguess/ws");
        listener = new Listener();
        webSocket = client.newWebSocket(request, listener);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    class Listener extends okhttp3.WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
//        webSocket.send("{Id: 1, Message: Hello}");
//        webSocket.send(ByteString.decodeHex("deadbeef"));
//        webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            JSONObject jsonObject;
            int type= -1;
            String content = null;
            String receiver = null;
            String sender = null;
            try {
                jsonObject = new JSONObject(text);
                type = jsonObject.getInt("type");
                content = jsonObject.getString("content");
                receiver = jsonObject.getString("receiver");
                sender = jsonObject.getString("sender");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            switch (type) {
                case MessageType.CREATE:
                    intent.setAction("create");
                    intent.putExtra("sender", sender);
                    sendBroadcast(intent);
                    break;
                case MessageType.ADD:
                    intent.setAction("add");
                    intent.putExtra("sender", sender);
                    sendBroadcast(intent);
                    break;
                case MessageType.DRAW_PLAYER:
                    intent.setAction("draw_player");
                    sendBroadcast(intent);
                    System.out.println("I receive a draw_player message");
                    System.out.println(content);
                    break;
                case MessageType.GUESS_PLAYER:
                    intent.setAction("guess_player");
                    sendBroadcast(intent);
                    System.out.println("I receive a guess_player message");
                    System.out.println(content);
                    break;
                case MessageType.DRAW:
                    intent.setAction("draw");
                    intent.putExtra("bytes", content);
                    sendBroadcast(intent);
            }
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
            byte[] byteArray = bytes.toByteArray();
            Intent intent = new Intent();
            intent.setAction("draw");
            intent.putExtra("bytes", byteArray);
            sendBroadcast(intent);
        }

        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
//        webSocket.close(NORMAL_CLOSURE_STATUS, null);
            System.out.println("code: " + code + "reason: " + reason);
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
            System.out.println("Error: " + t.getMessage());
        }

    }

    public class InnerIBinder extends Binder {
        public WebSocketService getService () {
            return WebSocketService.this;
        }

    }
}

