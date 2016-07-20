package com.example.kevin.mapapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.ui.userinfo.OrderDetailActivity;

import java.util.HashMap;
import java.util.Map;

public class CustomWebView extends WebView {
    private static final String URL_PREFIX = ConnectionManager.SERVER_ADDR +"/webview";

    private SharedPreferences userinfo;

    public CustomWebView(Context context) {
        this(context, null);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        userinfo = getContext().getSharedPreferences("User_info", Context.MODE_PRIVATE);

        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);

                switch (uri.getPath()) {
                    case "/activities/order_detail":
                        Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                        intent.putExtra("oid", uri.getQueryParameter("oid"));
                        getContext().startActivity(intent);
                        ((Activity)getContext()).overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                        break;
                    default:
                        view.loadUrl(url);
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public void loadUrl(String url) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-access-token", userinfo.getString("token", ""));

        super.loadUrl(URL_PREFIX + url, headers);
    }
}
