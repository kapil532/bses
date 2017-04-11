package com.langoor.app.blueshak.support;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import com.langoor.app.blueshak.MainActivity;
import com.divrt.co.R;
import com.langoor.app.blueshak.global.GlobalFunctions;


public class WebViewFragment extends Fragment {

    public static final String TAG = "WebViewFragment";
    public static final String WEBVIEW_CLIENT_URL_KEY= "WebViewFragment";
    private Activity activity;
    private Context context;
    WebView webView;
    String url;


    public static WebViewFragment newInstance(Context context, String url){
        WebViewFragment webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WEBVIEW_CLIENT_URL_KEY, url);
        webViewFragment.setArguments(bundle);
        return webViewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.webview, container, false);
        webView=(WebView)view.findViewById(R.id.webview);
        activity = getActivity();
        context = getActivity();
        url = getArguments().getString(WEBVIEW_CLIENT_URL_KEY);
        startWebView(url);
        return view;
    }
    @Override
    public void onResume() {
        MainActivity.setTitle(getString(R.string.support),0);
        super.onResume();
    }
    private void startWebView(String url) {

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        GlobalFunctions.showProgress(context,"Loading...");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                GlobalFunctions.hideProgress();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                GlobalFunctions.hideProgress();
                Toast.makeText(activity, "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });
        webView.loadUrl(url);
    }
}
