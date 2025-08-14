package net.sowgro.farfalle;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Maintains the list of tabs, selected tab and a few tab actions
 */
public class TabService {

    public static final TabService INSTANCE = new TabService();

    private final ObservableField<Integer> selectedIndex = new ObservableField<>(0);
    private final ObservableArrayList<TabFragment> tabList = new ObservableArrayList<>();
    private Consumer<Integer> DrawerStateSetter = null;
    private Consumer<Integer> onScrollToIndexListener = null;

    private TabService() {}

    public TabFragment getTab(int index) {
        return tabList.get(index);
    }

    public int addTab(TabFragment tab) {
        tabList.add(tab);
        return tabList.indexOf(tab);
    }

    public boolean removeTab(TabFragment tab) {
        return tabList.remove(tab);
    }

    public TabFragment removeTab(int index) {
        return tabList.remove(index);
    }

    public ObservableList<TabFragment> tabListProperty() {
        return tabList;
    }

    public int getSelectedIndex() {
        return Objects.requireNonNull(selectedIndex.get());
    }

    public void setSelectedIndex(int index) {
        selectedIndex.set(index);
    }

    public ObservableField<Integer> selectedIndexProperty() {
        return selectedIndex;
    }

    public TabFragment getSelectedTab() {
        return getTab(getSelectedIndex());
    }

    public int size() {
        return tabList.size();
    }

    protected void setDrawerStateSetter(Consumer<Integer> onSetDrawerState) {
        this.DrawerStateSetter = onSetDrawerState;
    }

    /**
     * @see com.google.android.material.bottomsheet.BottomSheetBehavior#setState(int) 
     */
    public void setDrawerState(int state) {
        this.DrawerStateSetter.accept(state);
    }

    protected void setOnScrollToIndexListener(Consumer<Integer> onScrollToIndex) {
        this.onScrollToIndexListener = onScrollToIndex;
    }

    /**
     * Scrolls the tab selector to make a tab visible
     * @param index The index of the tab to make visible
     */
    public void scrollToIndex(int index) {
        if (onScrollToIndexListener != null) {
            onScrollToIndexListener.accept(index);
        }
    }
}
