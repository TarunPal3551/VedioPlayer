package in.avilaksh.vedioplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean onBoarding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getSharedPreferences("OnBoardingValue", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sharedPreferences.getBoolean("OnBoarding", true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.getBoolean("OnBoarding", true)) {
                    editor.putBoolean("OnBoarding", false);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), OnBoardingActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        }, 2500);
    }
}
