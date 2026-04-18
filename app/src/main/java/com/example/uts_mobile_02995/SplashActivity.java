package com.example.uts_mobile_02995;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView animationView = findViewById(R.id.lottieSplash);
        animationView.playAnimation();

        new Handler().postDelayed(() -> {
            SharedPreferences preferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
            boolean sudahLogin = preferences.getBoolean("sudah_login", false);
            boolean pernahLihatLanding = preferences.getBoolean("pernah_lihat_landing", false);

            Intent intent;
            if (sudahLogin) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else if (!pernahLihatLanding) {
                intent = new Intent(SplashActivity.this, OnBoardingActivity.class);
                preferences.edit().putBoolean("pernah_lihat_landing", true).apply();
            } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}