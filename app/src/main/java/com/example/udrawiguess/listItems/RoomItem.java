package com.example.udrawiguess.listItems;

public class RoomItem {
    private String name;
    private String id;

    public RoomItem(){}

    public RoomItem(String roomName, String roomId) {
        this.name = roomName;
        this.id = roomId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
