package net.sowgro.farfalle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;

public class SelectorAdapter extends RecyclerView.Adapter<SelectorAdapter.ViewHolder> {

    private static final int FOOTER_VIEW = 1;

    private final LayoutInflater mInflater;
    private final ObservableList<TabFragment> tabs;
    private final MainActivity mainActivity;

    interface OnClickListener { void f(int position); }
    OnClickListener onClickListener = (ignored) -> {};

    // data is passed into the constructor
    SelectorAdapter(Context context, ObservableList<TabFragment> tabs, MainActivity mainActivity) {
        this.mInflater = LayoutInflater.from(context);
        this.tabs = tabs;
        this.mainActivity = mainActivity;
        tabs.addOnListChangedCallback(new ObservableList.OnListChangedCallback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(ObservableList<TabFragment> sender) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<TabFragment> sender, int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(ObservableList<TabFragment> sender, int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(ObservableList<TabFragment> sender, int fromPosition, int toPosition, int itemCount) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<TabFragment> sender, int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW) {
            View view = mInflater.inflate(R.layout.fragment_tab_footer, parent, false);
            return new FooterViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.fragment_tab_selector, parent, false);
            return new ViewHolder(view);
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            return;
        }

        TabFragment tab = tabs.get(position);
        Util.connect(tab.title, i -> holder.title.setText(i));
        Util.connect(tab.preview, i -> holder.imageView.setImageBitmap(i));
        holder.card.setOnClickListener(a -> onClickListener.f(holder.getLayoutPosition()));
        holder.card.setOnLongClickListener(a -> tabs.remove(tab));
        Util.connect(mainActivity.selectedPage, p -> {
            holder.card.setStrokeWidth(p == holder.getLayoutPosition() ? 10 : 0);
//            holder.card.setChecked(p == position);
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return tabs.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
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

    public class FooterViewHolder extends ViewHolder {
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            Button button = itemView.findViewById(R.id.button);
            button.setOnClickListener((a) -> {
                TabFragment t = new TabFragment(a.getContext());
                tabs.add(t);
                int pos = tabs.indexOf(t);
                mainActivity.viewPager2.setCurrentItem(pos);
                ((LinearLayoutManager) mainActivity.recyclerView.getLayoutManager())
                        .scrollToPositionWithOffset(pos, 5);
                mainActivity.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            });
        }
    }
}
