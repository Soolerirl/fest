package eu.fest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.webview)
public class WebViewActivity extends BaseActivity {
    @ViewById
    WebView webView1;

    @Extra("url")
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null == url && !isInternetOn()) {
            finish();
        }
        webView1.setWebViewClient(new WebViewClient());
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.loadUrl(url);
    }
}
