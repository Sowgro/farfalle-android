package net.sowgro.farfalle;

import android.annotation.SuppressLint;

import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class Util {

    public interface ConnectCallback<T> {
        void set(T value);
    }

    /**
     * Connects an observable field to a setter
     * It sets the current value then listens for and sets future values
     * @param field The observable field
     * @param setter The setter function
     * @param <T> The type of the observable value
     */
    public static <T> void connect(ObservableField<T> field, ConnectCallback<T> setter) {
        setter.set(field.get());
        field.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                setter.set(field.get());
            }
        });
    }

    /**
     * Notifies the adapter of changes to the list
     * @param list The list to observe
     * @param adapter The adapter to notify
     * @param <E> The type of item in the list
     */
    public static <E> void connectList(ObservableList<E> list, RecyclerView.Adapter<?> adapter) {
        list.addOnListChangedCallback(new ObservableList.OnListChangedCallback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(ObservableList<E> sender) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<E> sender, int positionStart, int itemCount) {
                adapter.notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(ObservableList<E> sender, int positionStart, int itemCount) {
                adapter.notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(ObservableList<E> sender, int fromPosition, int toPosition, int itemCount) {
                adapter.notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<E> sender, int positionStart, int itemCount) {
                adapter.notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    /**
     * Notifies the adapter of changes to the list
     * @param list The list to observe
     * @param adapter The adapter to notify
     * @param <E> The type of item in the list
     */
    public static <E> void connectList(ObservableList<E> list, FragmentStateAdapter adapter) {
        list.addOnListChangedCallback(new ObservableList.OnListChangedCallback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(ObservableList<E> sender) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<E> sender, int positionStart, int itemCount) {
                adapter.notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(ObservableList<E> sender, int positionStart, int itemCount) {
                adapter.notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(ObservableList<E> sender, int fromPosition, int toPosition, int itemCount) {
                adapter.notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<E> sender, int positionStart, int itemCount) {
                adapter.notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

}
