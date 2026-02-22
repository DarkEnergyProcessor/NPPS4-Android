package com.npdep.npps4;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StateAdapter extends FragmentStateAdapter {
    private final Bridge bridge;

    interface Factory<T> {
        T newInstance(Bridge bridge);
    }

    static class FragmentDef {
        Factory<? extends Fragment> factory;
        String name;

        FragmentDef(String name, Factory<? extends Fragment> factory) {
            this.name = name;
            this.factory = factory;
        }
    }

    static final FragmentDef[] fragments = {
            new FragmentDef("Server", b -> new StatusFragment()),
            new FragmentDef("Tools", b -> new ToolsFragment())
    };

    public StateAdapter(@NonNull FragmentActivity fragmentActivity, Bridge bridge) {
        super(fragmentActivity);
        this.bridge = bridge;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position].factory.newInstance(bridge);
    }

    @NonNull
    public static String getFragmentName(int i) {
        return fragments[i].name;
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }
}
