package com.example.udrawiguess.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.udrawiguess.R;
import com.example.udrawiguess.Util;
import com.example.udrawiguess.fragment.RoomFragment;
import com.example.udrawiguess.service.WebSocketService;
import com.example.udrawiguess.socket.MessageType;
import com.example.udrawiguess.view.DrawView;

import java.util.Random;

import okhttp3.WebSocket;
import okio.ByteString;

public class DrawActivity extends AppCompatActivity {
    private ImageButton imageButtonClean;
    private ImageButton imageButtonBack;
    private ImageButton imageButtonRedo;
    private ImageButton imageButtonSave;
    private DrawView drawView;
    private EditText editText;
    private ImageView imageView;

    private WebSocketService webSocketService;
    private WebSocketService.InnerIBinder binder;
    private ServiceConnection serviceConnection;
    private WebSocket webSocket;
    private String Id;
    private String partnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        init();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(3000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    DrawActivity.this.drawView.invalidate();
//                    System.out.println("invalidate()");
//                }
//
//            }
//        }).start();
    }

    private void init() {
        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        if(intent.hasExtra("partnerId")) {
            partnerId = intent.getStringExtra("partnerId");
        }
        startService();
        bindService();
        doRegisterReceiver();

        drawView = findViewById(R.id.activity_draw_draw_view);
        drawView.setOnTouchMessageListener(new DrawView.OnTouchMessageListener() {
            @Override
            public void onTouch(byte[] bytes) {
                webSocket.send(new ByteString(bytes));
            }
        });
//        drawView.setOnTouchMessageListener(new DrawView.OnTouchMessageListener() {
//            @Override
//            public void onTouch(String str) {
//                webSocket.send(Util.sendJson(MessageType.DRAW, str, partnerId, Id));
////                webSocket.send(Util.sendJson(MessageType.DRAW, bytes.toString(), partnerId, Id));
//            }
//        });
        editText = findViewById(R.id.layout_main_title_edit_text);
        imageView = findViewById(R.id.layout_main_title_image_view);
        imageButtonClean = findViewById(R.id.activity_draw_clean);
        imageButtonClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.refresh();
            }
        });
        imageButtonBack = findViewById(R.id.activity_draw_undo);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.undo();
            }
        });
        imageButtonRedo = findViewById(R.id.activity_draw_redo);
        imageButtonRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.redo();
            }
        });
        imageButtonSave = findViewById(R.id.activity_draw_save);
        imageButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.save(DrawActivity.this);
            }
        });
    }

    private void startService() {
        Intent intent = new Intent(this, WebSocketService.class);
        this.startService(intent);
    }

    private void bindService() {
        Intent intent = new Intent(this, WebSocketService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (WebSocketService.InnerIBinder) service;
                webSocketService = binder.getService();
                webSocket = webSocketService.getWebSocket();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void doRegisterReceiver() {
        BroadcastReceiver addReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                partnerId = intent.getStringExtra("sender");
                System.out.println("partnerId: "+partnerId);
            }
        };
        IntentFilter filterAdd = new IntentFilter("add");
        registerReceiver(addReceiver, filterAdd);

        BroadcastReceiver drawReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                byte[] bytes = intent.getByteArrayExtra("bytes");
                drawView.setBitMapByteArray(bytes);
            }
        };
//        BroadcastReceiver drawReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String str = intent.getStringExtra("bytes");
//                drawView.setBitMapString(str);
//            }
//        };
        IntentFilter filterDraw = new IntentFilter("draw");
        registerReceiver(drawReceiver, filterDraw);

        BroadcastReceiver drawPlayerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        IntentFilter filterDrawPlayer = new IntentFilter("draw_player");
        registerReceiver(drawPlayerReceiver, filterDrawPlayer);

        BroadcastReceiver guessPlayerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        IntentFilter filterGuessPlayer = new IntentFilter("guess_player");
        registerReceiver(guessPlayerReceiver, filterGuessPlayer);
    }
}