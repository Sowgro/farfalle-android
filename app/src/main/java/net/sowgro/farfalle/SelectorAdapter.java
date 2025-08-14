package net.sowgro.farfalle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;

/**
 * An adapter for the recyclerview providing the selector for each tab
 */
public class SelectorAdapter extends RecyclerView.Adapter<SelectorAdapter.ViewHolder> {

    private final TabService tabs;

    SelectorAdapter() {
        this.tabs = TabService.INSTANCE;
        Util.connectList(tabs.tabListProperty(), this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_tab_selector, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TabFragment tab = tabs.getTab(position);
        Util.connect(tab.title, i -> holder.title.setText(i));
        Util.connect(tab.preview, i -> holder.imageView.setImageBitmap(i));
        holder.card.setOnClickListener(a -> {
            var item = holder.getLayoutPosition();
            tabs.setSelectedIndex(item);
            tabs.setDrawerState(BottomSheetBehavior.STATE_COLLAPSED);
        });
        holder.card.setOnLongClickListener(a -> tabs.removeTab(tab));
        Util.connect(tabs.selectedIndexProperty(),
                (p) -> holder.card.setStrokeWidth(p == holder.getLayoutPosition() ? 10 : 0));
    }

    @Override
    public int getItemCount() {
        return tabs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        MaterialCardView card;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            title = itemView.findViewById(R.id.tab_title);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
