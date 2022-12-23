package com.sirajsaleem.my_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebsiteActivity extends AppCompatActivity implements MethodsFactory{

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        if(getIntent().getBooleanExtra("back", false)){
            overridePendingTransition(R.anim.navigation_back_slide_in, R.anim.navigation_back_slide_out);
        }

        findViews();
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("sirajsaleem.com");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        webView.loadUrl("https://sirajsaleem.com");
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void goBack(String string) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("back", true);
        startActivity(intent);
    }

    @Override
    public void findViews() {
        webView = findViewById(R.id.webView);
    }

    @Override
    public void handlerManager() {
        //not required in this activity
    }

    @Override
    public void onBackPressed() {
        Pattern pattern = Pattern.compile("https://sirajsaleem\\.com/#.*");
        Matcher matcher = pattern.matcher(webView.getUrl());
        if(matcher.matches()){
            goBack(null);
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                goBack(null);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Pattern pattern = Pattern.compile("https://sirajsaleem\\.com/#.*");
            Matcher matcher = pattern.matcher(webView.getUrl());
            if(matcher.matches()){
                goBack(null);
            } else {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    goBack(null);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}