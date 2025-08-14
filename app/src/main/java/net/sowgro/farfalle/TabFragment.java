package net.sowgro.farfalle;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

/**
 * The layout for each tab, containing a webview and url bar
 */
public class TabFragment extends Fragment {

    public static final String SEARCH_ENGINE = "https://google.com/search?q=";
    public static final String HOME_PAGE = "https://google.com/";

    WebView webView;
    EditText urlBar;
    ProgressBar progressBar;

    ObservableField<Bitmap> preview = new ObservableField<>(null);
    ObservableField<String> title = new ObservableField<>("");
    private final String url;
    private final TabService tabs;
    private final Context context;

    @SuppressLint("SetJavaScriptEnabled")
    public TabFragment(Context context, String url) {
        this.context = context;
        this.url = url;
        this.tabs = TabService.INSTANCE;
        webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_content, container, false);
        super.onCreate(savedInstanceState);

        ConstraintLayout constraintLayout = v.findViewById(R.id.root);
        progressBar = v.findViewById(R.id.progressBar);
        urlBar = v.findViewById(R.id.urlBar);

        // remount webview
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
            webView.loadUrl(url);
        }

        // bind
        var webViewClient = new WebViewClient() {
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                urlBar.setText(url);
                super.doUpdateVisitedHistory(view, url, isReload);
            }
        };
        webView.setWebViewClient(webViewClient);

        var webChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String newTitle) {
                title.set(newTitle);
                super.onReceivedTitle(view, newTitle);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        };
        webView.setWebChromeClient(webChromeClient);

        webView.setOnCreateContextMenuListener(this::createContextMenu);

        urlBar.setOnEditorActionListener((a, b, c) -> {
                webView.loadUrl(navOrSearch(a.getText().toString()));
                a.clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(urlBar.getWindowToken(), 0);
                return true;
        });
        return v;
    }

    /**
     * Updates the preview image
     */
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

    /**
     * Determines if the text entered in the URL bar is a full URL, URL without scheme or search term.
     * @param s The input string
     * @return A valid full URL to load
     */
    private String navOrSearch(String s) {
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

    private void createContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo info) {
        contextMenu.add("Open in new tab...").setOnMenuItemClickListener((m) -> {
            WebView.HitTestResult result = webView.getHitTestResult();

            if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
                var url = result.getExtra();
                tabs.addTab(new TabFragment(context, url));
            }
            return true;
        });
        contextMenu.add("Copy to clipboard").setOnMenuItemClickListener((m) -> {
            var cb = ((ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE));

            WebView.HitTestResult result = webView.getHitTestResult();

            if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
                var url = result.getExtra();
                cb.setPrimaryClip(
                        new ClipData(
                                new ClipDescription(
                                        "copied url",
                                        new String[]{"text/uri-list"}
                                ),
                                new ClipData.Item(url)
                        )
                );
            }
            return true;
        });

    }
}