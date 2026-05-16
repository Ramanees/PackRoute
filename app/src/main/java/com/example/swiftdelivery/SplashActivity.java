package com.example.swiftdelivery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftdelivery.user.UserLoginActivity;

public class SplashActivity extends AppCompatActivity {

    // Handler for scheduling the delayed task (disappearance of splash screen)
    private Handler handler = new Handler();
    // Runnable that contains the task to execute after the delay
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Navigate to UserLoginActivity after the delay
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, UserLoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, 2000); // Scheduling a 2 second delay
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // To prevent memory leaks
    }
}