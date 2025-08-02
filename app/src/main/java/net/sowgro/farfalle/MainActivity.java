package net.sowgro.farfalle;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.*;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends AppCompatActivity {

    private Insets systemBars;
    public ViewPager2 viewPager2;
    public RecyclerView recyclerView;
    public ObservableField<Integer> selectedPage = new ObservableField<>(0);
    public BottomSheetBehavior<View> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View bottomSheet = findViewById(R.id.bottom_sheet);
        viewPager2 = findViewById(R.id.main);
        bottomSheetBehavior = from(bottomSheet);

        ObservableArrayList<TabFragment> tabs = new ObservableArrayList<>();
        TabFragment.ContentAdapter adapter = new TabFragment.ContentAdapter(this, tabs);

        tabs.add(new TabFragment(this));

        viewPager2.setAdapter(adapter);
        viewPager2.setUserInputEnabled(false);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int curPos) {
                selectedPage.set(curPos);
            }
        });

        recyclerView = findViewById(R.id.tabs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SelectorAdapter adapter1 = new SelectorAdapter(this, tabs, this);
        adapter1.onClickListener = item -> {
            viewPager2.setCurrentItem(item);
            bottomSheetBehavior.setState(STATE_COLLAPSED);
        };
        recyclerView.setAdapter(adapter1);

        this.findViewById(R.id.back).setOnClickListener((a) ->
                tabs.get(viewPager2.getCurrentItem()).webView.goBack());
        this.findViewById(R.id.forward).setOnClickListener(b ->
                tabs.get(viewPager2.getCurrentItem()).webView.goForward());
        this.findViewById(R.id.reload).setOnClickListener(b ->
                tabs.get(viewPager2.getCurrentItem()).webView.reload());
        this.findViewById(R.id.search).setOnClickListener(a -> {
            var urlBar = tabs.get(viewPager2.getCurrentItem()).urlBar;
            urlBar.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(urlBar, InputMethodManager.SHOW_IMPLICIT);
        });
        this.findViewById(R.id.menu).setOnClickListener(a ->
                Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show());

        ConstraintLayout buttons = findViewById(R.id.buttons);
        bottomSheetBehavior.setPeekHeight(buttons.getHeight() + 10);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetCallback() {
            int prevState = STATE_COLLAPSED;
            @Override
            public void onStateChanged(@NonNull View view, int slideOffset) {
                if ((slideOffset == STATE_DRAGGING && prevState == STATE_COLLAPSED)) {
                    tabs.get(selectedPage.get()).updatePreview();
                }
                prevState = slideOffset;
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                viewPager2.setTranslationY(-(view.getHeight() - (bottomSheetBehavior.getPeekHeight() + systemBars.bottom)) * v/2);
            }
        });


    }
}