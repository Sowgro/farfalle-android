package net.sowgro.farfalle;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The layout for the drawer containing toolbar and tab selector
 */
public class DrawerFragment extends Fragment {

    private final TabService tabs;

    public DrawerFragment() {
        this.tabs = TabService.INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drawer, container, false);

        // bind actions
        Button back = v.findViewById(R.id.back);
        back.setOnClickListener((a) -> tabs.getSelectedTab().webView.goBack());

        Button forward = v.findViewById(R.id.forward);
        forward.setOnClickListener(b -> tabs.getSelectedTab().webView.goForward());

        Button reload = v.findViewById(R.id.reload);
        reload.setOnClickListener(b -> tabs.getSelectedTab().webView.reload());

        Button search = v.findViewById(R.id.search);
        search.setOnClickListener(a -> {
            var urlBar = tabs.getSelectedTab().urlBar;
            urlBar.requestFocus();
            InputMethodManager imm = (InputMethodManager) container.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(urlBar, InputMethodManager.SHOW_IMPLICIT);
        });

        Button menu = v.findViewById(R.id.menu);
        menu.setOnClickListener(a ->
                Toast.makeText(container.getContext(), "Not Implemented", Toast.LENGTH_SHORT).show());

        RecyclerView recyclerView = v.findViewById(R.id.tabs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new ConcatAdapter(new SelectorAdapter(), new FooterAdapter()));
        tabs.setOnScrollToIndexListener((value) ->
                ((LinearLayoutManager) recyclerView.getLayoutManager())
                .scrollToPositionWithOffset(value, 5)
        );

        return v;
    }
}
