package com.example.udrawiguess.activity;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.udrawiguess.R;
import com.example.udrawiguess.adapter.PageAdapter;
import com.example.udrawiguess.adapter.RoomAdapter;
import com.example.udrawiguess.listItems.RoomItem;
import com.example.udrawiguess.socket.Listener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MainActivity extends FragmentActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView imageview;
    private String[] tabs = {"like", "play", "share"};
    private PageAdapter pageAdapter = new PageAdapter(this);
    private OkHttpClient client;
    private Listener listener;
    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        // set tab
        this.tabLayout = findViewById(R.id.activity_main_tab_layout);
        this.viewPager = findViewById(R.id.activity_main_view_pager2);
        this.viewPager.setAdapter(pageAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabs[position])
        ).attach();
//        client = new OkHttpClient();
//        Request request = new Request.Builder().url("ws://47.254.242.71/udrawiguess/ws").build();
//        listener = new Listener();
//        webSocket = client.newWebSocket(request, listener);
    }

    private void openAssignFolder(Context context, String path){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        try {
            context.startActivity(intent);
//            startActivity(Intent.createChooser(intent,"选择浏览工具"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}