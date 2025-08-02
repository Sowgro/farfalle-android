package net.sowgro.farfalle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import java.util.Objects;

public class FarfalleWebView extends WebView {

    @SuppressLint("SetJavaScriptEnabled")
    public FarfalleWebView(Context context) {
        super(context);
        super.setWebViewClient(new FarfalleWebViewClient());
        super.setWebChromeClient(new FarfalleWebChromeClient());
        super.getSettings().setJavaScriptEnabled(true);
    }

    interface OnDraw { void onDraw(Canvas canvas); }
    public OnDraw onDraw;

    @Override
    protected void onDraw(Canvas canvas) {
        if (onDraw != null) {
            onDraw.onDraw(canvas);
        }
        super.onDraw(canvas);
    }

    @NonNull
    @Override
    public FarfalleWebViewClient getWebViewClient() {
        return (FarfalleWebViewClient) super.getWebViewClient();
    }

    @NonNull
    @Override
    public FarfalleWebChromeClient getWebChromeClient() {
        return (FarfalleWebChromeClient) Objects.requireNonNull(super.getWebChromeClient());
    }

    public static class FarfalleWebViewClient extends WebViewClient {

        public interface OnUrlChangeListener { void f(WebView view, String url, boolean isReload); }
        private OnUrlChangeListener onUrlChangeListener;

        public interface OnPageFinishedListener { void f(WebView view, String url); }
        private OnPageFinishedListener onPageFinishedListener;

        public void setOnUrlChangeListener(OnUrlChangeListener onUrlChangeListener) {
            this.onUrlChangeListener = onUrlChangeListener;
        }

        public void setOnPageFinishedListener(OnPageFinishedListener onPageFinishedListener) {
            this.onPageFinishedListener = onPageFinishedListener;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (onPageFinishedListener != null) {
                onPageFinishedListener.f(view, url);
            }
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            if (onUrlChangeListener != null) {
                onUrlChangeListener.f(view, url, isReload);
            }
            super.doUpdateVisitedHistory(view, url, isReload);
        }
    }

    public static class FarfalleWebChromeClient extends WebChromeClient {

        public interface OnReceivedTitleListener { void f(WebView view, String title); }
        private OnReceivedTitleListener onReceivedTitleListener;

        public interface OnProgressChangedListener { void f(WebView view, int progress); }
        private OnProgressChangedListener onProgressChangedListener;

        public void setOnReceivedTitleListener(OnReceivedTitleListener onReceivedTitleListener) {
            this.onReceivedTitleListener = onReceivedTitleListener;
        }

        public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
            this.onProgressChangedListener = onProgressChangedListener;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (onReceivedTitleListener != null) {
                onReceivedTitleListener.f(view, title);
            }
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (onProgressChangedListener != null) {
                onProgressChangedListener.f(view, newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }


}
