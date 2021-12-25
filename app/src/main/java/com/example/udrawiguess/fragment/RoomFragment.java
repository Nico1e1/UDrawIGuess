package com.example.udrawiguess.fragment;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.udrawiguess.R;
import com.example.udrawiguess.Util;
import com.example.udrawiguess.activity.DrawActivity;
import com.example.udrawiguess.adapter.RoomAdapter;
import com.example.udrawiguess.listItems.RoomItem;
import com.example.udrawiguess.service.WebSocketService;
import com.example.udrawiguess.socket.MessageType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.WebSocket;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomFragment extends Fragment {
    private ListView listView;
    private List<RoomItem> roomItems;
    private RoomAdapter roomAdapter;
    private ImageView imageView;
    private WebSocketService webSocketService;
    private WebSocketService.InnerIBinder binder;
    private ServiceConnection serviceConnection;
    private String Id = new Random().nextInt(100)+"";
//    private Listener listener;
//    private OkHttpClient client;
    private WebSocket webSocket;

    public RoomFragment() {
        // Required empty public constructor
    }

    public static RoomFragment newInstance(String param1, String param2) {
        RoomFragment fragment = new RoomFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        startService();
        bindService();

        this.listView = view.findViewById(R.id.fragment_room_list_view);
        this.roomItems = new ArrayList<>();
        this.roomAdapter = new RoomAdapter(getActivity(), R.layout.room_item, roomItems);
        roomAdapter.setRoomAdapterListener(new RoomAdapter.RoomAdapterListener() {
            @Override
            public void myOnLick(int i) {
                RoomItem roomItem = roomItems.get(i);
                String partnerId = roomItem.getId();
                Context context = getContext();
                Intent intent = new Intent(context, DrawActivity.class);
                intent.putExtra("Id", Id);
                intent.putExtra("partnerId", partnerId);
                webSocket.send(Util.sendJson(MessageType.ADD, "", partnerId, Id));
                context.startActivity(intent);
            }
        });
        this.listView.setAdapter(this.roomAdapter);
//        add();
        this.imageView = view.findViewById(R.id.layout_main_title_image_view);
        this.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Intent intent = new Intent(context, DrawActivity.class);
                intent.putExtra("Id", Id);
                webSocket.send(Util.sendJson(MessageType.CREATE, "", "", Id));
//                getActivity().unbindService(serviceConnection);
                context.startActivity(intent);
            }
        });
        doRegisterReceiver();
//        String str = Util.sendJson(MessageType.INIT, "Hello", "", "1");
//        System.out.println(str);
//        webSocket.send(str);

        return view;
    }

    public void addRoom(String roomName, String roomId) {
        this.roomItems.add(new RoomItem("Room "+roomName, roomId));
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

    private void startService() {
        Intent intent = new Intent(this.getActivity(), WebSocketService.class);
        this.getActivity().startService(intent);
    }

    private void bindService() {
        Intent intent = new Intent(this.getActivity(), WebSocketService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (WebSocketService.InnerIBinder) service;
                webSocketService = binder.getService();
                webSocket = webSocketService.getWebSocket();

                String str = Util.sendJson(MessageType.INIT, "Hello", "", Id);
                webSocket.send(str);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void doRegisterReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sender = intent.getStringExtra("sender");
                addRoom(sender, sender);
                roomAdapter.notifyDataSetChanged();
            }
        };
        IntentFilter filter = new IntentFilter("create");
        this.getActivity().registerReceiver(receiver, filter);
    }
}