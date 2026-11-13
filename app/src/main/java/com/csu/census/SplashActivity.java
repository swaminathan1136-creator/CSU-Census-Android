package com.csu.census;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.csu.census.data.AppStore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);

        setContentView(R.layout.activity_splash);

        AppStore.seed(this);

        ImageView logo = findViewById(R.id.splashLogo);
        TextView title = findViewById(R.id.splashTitle);
        TextView subtitle = findViewById(R.id.splashSubtitle);
        View progress = findViewById(R.id.splashProgress);

        logo.setScaleX(0.3f);
        logo.setScaleY(0.3f);
        logo.setAlpha(0f);
        title.setAlpha(0f);
        title.setTranslationY(40f);
        subtitle.setAlpha(0f);
        subtitle.setTranslationY(30f);
        progress.setAlpha(0f);

        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.3f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.3f, 1f);
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f);

        AnimatorSet logoSet = new AnimatorSet();
        logoSet.playTogether(logoScaleX, logoScaleY, logoAlpha);
        logoSet.setDuration(700);
        logoSet.setInterpolator(new OvershootInterpolator(1.1f));
        logoSet.start();

        title.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(400)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        subtitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(550)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        progress.animate()
                .alpha(1f)
                .setStartDelay(700)
                .setDuration(400)
                .start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1800);
    }
}