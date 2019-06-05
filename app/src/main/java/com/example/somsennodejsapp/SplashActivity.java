package com.example.somsennodejsapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(1000)
                .withBackgroundColor(Color.parseColor("#1A1A1A"))
                .withHeaderText("Header")
                .withFooterText("Footer")
                .withBeforeLogoText("Trendee")
                .withAfterLogoText("Your friendly neighborhood app")
                .withLogo(R.mipmap.ic_launcher_round);

        config.getHeaderTextView().setTextColor(Color.WHITE);
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);

        config.getBeforeLogoTextView().setTextSize(80);

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);

    }
}
