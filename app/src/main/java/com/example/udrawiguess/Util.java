package com.example.udrawiguess;

import org.json.JSONObject;

import okhttp3.Request;

public class Util {
    public static Request newRequest(String url) {
        return new Request.Builder().url(url).build();
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

    public static void compare(byte[] bytes1, byte[] bytes2) {
        if (bytes1 == null && bytes2 == null) {
            System.out.println("both null");
            return;
        }
        if(bytes1 == null || bytes2 == null) {
            System.out.println("only one is null");
            return;
        }
        if(bytes1.length != bytes2.length) {
            System.out.println("different length, "+"bytes1 is "+bytes1.length+", bytes2 is "+bytes2.length);
            return;
        }
        boolean flag = true;
        for(int i = 0 ; i < bytes1.length ; i++) {
            if(bytes1[i] != bytes2[i]) {
                System.out.println("bytes1 is "+bytes1[i]+", bytes2 is "+bytes2[i]);
                flag = false;
            }
        }
        if(flag) {
            System.out.println("they are the same");
        }
    }


}
