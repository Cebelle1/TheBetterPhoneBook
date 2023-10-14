package com.sp.thebetterphonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HelpManualWebView extends AppCompatActivity {

    private WebView idWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_manual_web_view);
        idWebView=findViewById(R.id.idWebView);
        idWebView.setWebViewClient(new WebViewClient());
        //idWebView.setWebChromeClient(new WebChromeClient());
        idWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        idWebView.getSettings().setDomStorageEnabled(true);
        idWebView.getSettings().setAppCacheEnabled(true);
        idWebView.getSettings().setLoadsImagesAutomatically(true);
        idWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        idWebView.getSettings().setBuiltInZoomControls(true);

        idWebView.loadUrl("https://loveahgerl.wixsite.com/website");
    }

    @Override
    public void onBackPressed() {
        if(idWebView.canGoBack()){
            idWebView.goBack();
        }else{
            Intent intent = new Intent (HelpManualWebView.this,MainContactList.class);
            startActivity(intent);
            finish();
        }
    }
}
