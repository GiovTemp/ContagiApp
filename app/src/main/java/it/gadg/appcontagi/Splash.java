package it.gadg.appcontagi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

public class Splash extends AppCompatActivity {

    ImageView splash;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.splash);

        splash.animate().translationY(-3000).setDuration(2500).setStartDelay(8000);
        lottieAnimationView.animate().translationY(1400).setDuration(2500).setStartDelay(8000);

    }
}