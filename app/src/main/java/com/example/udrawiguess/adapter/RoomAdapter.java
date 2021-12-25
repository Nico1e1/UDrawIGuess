package com.example.udrawiguess.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.udrawiguess.R;
import com.example.udrawiguess.listItems.RoomItem;

import java.util.List;

public class RoomAdapter extends ArrayAdapter<RoomItem> {
    private Context context;
    private int resource;
    private ViewHolder viewHolder;
    private RoomAdapterListener roomAdapterListener;

    public RoomAdapter(@NonNull Context context, int resource, @NonNull List<RoomItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RoomItem roomItem = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(this.resource, parent, false);
            this.viewHolder = new ViewHolder();
            viewHolder.button = convertView.findViewById(R.id.room_item_button);
            convertView.setTag(viewHolder);
            viewHolder.button.setText(roomItem.getName());
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    roomAdapterListener.myOnLick(position);
                }
            });
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public void setRoomAdapterListener(RoomAdapterListener listener) {
        this.roomAdapterListener = listener;
    }

    private final class ViewHolder {
        Button button;
    }

    public interface RoomAdapterListener {
        void myOnLick(int i);
    }
}
