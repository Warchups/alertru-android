package com.gnommostudios.alertru.alertru_android.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.gnommostudios.alertru.alertru_android.R;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 500;

    private ImageView imageSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imageSplash = (ImageView) findViewById(R.id.splashscreen_image);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(SPLASH_DISPLAY_LENGTH);
        imageSplash.startAnimation(scaleAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);

                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
