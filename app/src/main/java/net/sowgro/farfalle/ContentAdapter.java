package net.sowgro.farfalle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ContentAdapter extends FragmentStateAdapter {
    private final TabService tabs;

    public ContentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.tabs = TabService.INSTANCE;
        Util.connectList(tabs.tabListProperty(), this);
    }

    @Override
    public int getItemCount() {
        return tabs.size();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return tabs.getTab(position);
    }
}
