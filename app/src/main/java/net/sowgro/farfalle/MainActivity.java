package net.sowgro.farfalle;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

/**
 * The main activity which holds a viewpager2 for the tabs and the bottom drawer
 */
public class MainActivity extends AppCompatActivity {

    private final TabService tabs;
    private Insets systemBars;

    public MainActivity() {
        this.tabs = TabService.INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.top), (v, insets) -> {
            systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabs.addTab(new TabFragment(this, TabFragment.HOME_PAGE));

        // bind actions
        ViewPager2 viewPager2 = findViewById(R.id.top);
        viewPager2.setAdapter(new ContentAdapter(this));
        viewPager2.setUserInputEnabled(false);
        Util.connect(tabs.selectedIndexProperty(), viewPager2::setCurrentItem);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int curPos) {
                tabs.setSelectedIndex(curPos);
            }
        });

        FragmentContainerView bottom = findViewById(R.id.bottom);
        BottomSheetBehavior<FragmentContainerView> bottomSheetBehavior = BottomSheetBehavior.from(bottom);
        bottomSheetBehavior.setPeekHeight(bottom.getHeight() + 10);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            int prevState = BottomSheetBehavior.STATE_COLLAPSED;
            @Override
            public void onStateChanged(@NonNull View view, int state) {
                if ((state == BottomSheetBehavior.STATE_DRAGGING && prevState == BottomSheetBehavior.STATE_COLLAPSED)) {
                    tabs.getSelectedTab().updatePreview();
                }
                prevState = state;
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                viewPager2.setTranslationY(-(view.getHeight() - (bottomSheetBehavior.getPeekHeight() + systemBars.bottom)) * v/2);
            }
        });
        tabs.setDrawerStateSetter(bottomSheetBehavior::setState);


    }
}