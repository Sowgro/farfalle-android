package net.sowgro.farfalle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

/**
 * An adapter for the recyclerview providing only a single item with the add tab button
 */
public class FooterAdapter extends RecyclerView.Adapter<FooterAdapter.ViewHolder> {

    private final TabService tabs;

    public FooterAdapter() {
        tabs = TabService.INSTANCE;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_tab_footer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.button.setOnClickListener((a) -> {
            TabFragment t = new TabFragment(a.getContext(), TabFragment.HOME_PAGE);
            int pos = tabs.addTab(t);
            tabs.setSelectedIndex(pos);
            tabs.scrollToIndex(pos);
            tabs.setDrawerState(BottomSheetBehavior.STATE_COLLAPSED);
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
        }
    }
}
