package com.example.udrawiguess.socket;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.udrawiguess.fragment.RoomFragment;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class Listener extends okhttp3.WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    public OkHttpClient newClient() {
        return null;
    }
    public static Request newRequest(String url) {
        return new Request.Builder().url(url).build();
    }
    public Listener newWebSocket(OkHttpClient client) {
        return null;
    }
    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
//        webSocket.send("{Id: 1, Message: Hello}");
//        webSocket.send(ByteString.decodeHex("deadbeef"));
//        webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        System.out.println("String type");
        Intent intent = new Intent();
        intent.setAction("Create");

        System.out.println(text);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
        System.out.println(bytes.hex());
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
//        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        System.out.println("code: "+code + "reason: "+reason);
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        System.out.println("Error: "+t.getMessage());
    }

    public static String sendJson(int type, String content, String receiver, String sender) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", type);
            jsonObject.put("content", content);
            jsonObject.put("receiver", receiver);
            jsonObject.put("sender", sender);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static void parseJson(String text) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(text);
            int type = jsonObject.getInt("type");
            String content = jsonObject.getString("content");
            String receiver = jsonObject.getString("receiver");
            String sender = jsonObject.getString("sender");

            switch (type) {
                case MessageType.CREATE:

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void newCreate(RoomFragment roomFragment, String sender) {
        roomFragment.addRoom(sender, sender);
    }
}
