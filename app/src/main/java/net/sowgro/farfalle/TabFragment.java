package net.sowgro.farfalle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

public class TabFragment extends Fragment {

    public static final String SEARCH_ENGINE = "https://google.com/search?q=";
    private static final String HOME_PAGE = "https://google.com/";

    FarfalleWebView webView;
    EditText urlBar;
    ProgressBar progressBar;

    ObservableField<Bitmap> preview = new ObservableField<>(null);
    ObservableField<String> title = new ObservableField<>("");
    private View v;

    public TabFragment(Context context) {
        webView = new FarfalleWebView(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_tab_content, container, false);
        super.onCreate(savedInstanceState);

        ConstraintLayout constraintLayout = v.findViewById(R.id.root);
        progressBar = v.findViewById(R.id.progressBar);
        urlBar = v.findViewById(R.id.urlBar);

        var layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.bottomToBottom = R.id.root;
        layoutParams.endToEnd = R.id.root;
        layoutParams.startToStart = R.id.root;
        layoutParams.topToBottom = R.id.urlBar;

        if (webView.getParent() != null) {
            ((ConstraintLayout) webView.getParent()).removeView(webView);
            constraintLayout.addView(webView, layoutParams);
        } else {
            constraintLayout.addView(webView, layoutParams);
            webView.loadUrl(HOME_PAGE);
        }

        webView.getWebChromeClient().setOnReceivedTitleListener((a, b) -> {
            title.set(b);
        });

        webView.getWebViewClient().setOnUrlChangeListener((a, url, c) -> {
            urlBar.setText(url);
        });

        webView.getWebChromeClient().setOnProgressChangedListener((webview, progress) -> {
            if (progress == 100) {
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
            }
        });

        urlBar.setOnEditorActionListener((a, b, c) -> {
                webView.loadUrl(navOrSearch(a.getText().toString()));
                a.clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(urlBar.getWindowToken(), 0);
                return true;
        });
        return v;
    }

    public void updatePreview() {
        var bitmap = Bitmap.createBitmap(webView.getWidth(), webView.getWidth(), Bitmap.Config.RGB_565);
        var locationOfViewInWindow = new int[2];
        webView.getLocationInWindow(locationOfViewInWindow);
        PixelCopy.request(
                getActivity().getWindow(),
                new Rect(
                        locationOfViewInWindow[0],
                        locationOfViewInWindow[1],
                        locationOfViewInWindow[0] + webView.getWidth(),
                        locationOfViewInWindow[1] + webView.getWidth()
                ),
                bitmap,
                copyResult -> {
                    if (copyResult == PixelCopy.SUCCESS) {
                        preview.set(bitmap);
                    }
                },
                new Handler());
    }

    String navOrSearch(String s) {
        if (!s.contains(" ") && s.contains(".")) {
            if (s.contains("://")) {
                return s;
            } else {
                return "http://"+s;
            }
        } else {
            return SEARCH_ENGINE + s.replace(" ", "%20");
        }
    }

    public static class ContentAdapter extends FragmentStateAdapter {
        private final ObservableList<TabFragment> tabs;

        public ContentAdapter(@NonNull FragmentActivity fragmentActivity, ObservableList<TabFragment> tabs) {
            super(fragmentActivity);
            this.tabs = tabs;
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

        @Override
        public int getItemCount() {
            return tabs.size();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return tabs.get(position);
        }
    }
}