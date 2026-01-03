package com.npdep.npps4;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StateAdapter extends FragmentStateAdapter {
    private final Bridge bridge;

    public StateAdapter(@NonNull FragmentActivity fragmentActivity, Bridge bridge) {
        super(fragmentActivity);
        this.bridge = bridge;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return StatusFragment.newInstance(bridge);
            case 1:
                return LogFragment.newInstance(bridge);
            default:
                throw new IndexOutOfBoundsException("fragment index out of range");
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
