
package com.csu.census;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.csu.census.data.AppStore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        AppStore.seed(this);

        ImageView logo = findViewById(R.id.splashLogo);

        logo.setScaleX(0.72f);
        logo.setScaleY(0.72f);
        logo.setAlpha(0.4f);

        logo.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .alpha(1.0f)
            .setDuration(850)
            .setInterpolator(
                new AccelerateDecelerateInterpolator()
            )
            .start();

        new Handler(Looper.getMainLooper())
            .postDelayed(() -> {

                Intent nextActivity;

                boolean isLoggedIn =
                    getSharedPreferences(
                        "csu_census_store",
                        MODE_PRIVATE
                    ).contains("active_id");

                if (isLoggedIn) {

                    nextActivity = new Intent(
                        this,
                        MainActivity.class
                    );

                } else {

                    nextActivity = new Intent(
                        this,
                        LoginActivity.class
                    );
                }

                startActivity(nextActivity);
                finish();

            }, 1450);
    }
}
