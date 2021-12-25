package com.example.udrawiguess.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.udrawiguess.fragment.RoomFragment;

import java.util.HashMap;
import java.util.Map;

public class PageAdapter extends FragmentStateAdapter {
    private Map<Integer, Fragment> map = new HashMap<>();

    public PageAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public PageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public PageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = map.get(position);

        if(fragment == null) {
            switch(position) {
                case 0:
                    fragment = new RoomFragment();
                    break;
                case 1:
                    fragment = new RoomFragment();
                    break;
                case 2:
                    fragment = new RoomFragment();
                    break;
                default:
                    break;
            }
            map.put(position, fragment);
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
