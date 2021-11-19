package com.candybuzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.candybuzz.utils.HighScoreHelper;


public class MainActivity extends AppCompatActivity {
    public static final String SOUND = "SOUND", MUSIC = "MUSIC";
    private boolean mMusic = true, mSound = true;
    private Animation animation;


    String share_my_app = "create a google play console account and contact with the developer, he will add this button to your playstore link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.backgorund);
        setToFullScreen();
        Button btnStart = findViewById(R.id.btn_start);


        Button btnInviteFriends = findViewById(R.id.btn_invite_friends);
        ImageButton btnExit = findViewById(R.id.btn_exit);
        final ImageButton btnMusic = findViewById(R.id.btn_music);
        final ImageButton btnSound = findViewById(R.id.btn_sound);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        animation.setDuration(100);
        TextView highScore = findViewById(R.id.high_score);
        highScore.setText(String.valueOf(HighScoreHelper.getTopScore(this)));

        findViewById(R.id.activity_start).setOnClickListener(view -> setToFullScreen());

        btnStart.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GameEngineActivity.class);
            intent.putExtra(SOUND, mSound);
            intent.putExtra(MUSIC, mMusic);
            startActivity(intent);
        });

        btnExit.setOnClickListener(view -> {
            view.startAnimation(animation);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        });

        btnMusic.setOnClickListener(view -> {
            if (mMusic) {
                mMusic = false;
                btnMusic.setBackgroundResource(R.drawable.no_music);
            } else {
                mMusic = true;
                btnMusic.setBackgroundResource(R.drawable.music);
            }
        });

        btnSound.setOnClickListener(view -> {
            if (mSound) {
                mSound = false;
                btnSound.setBackgroundResource(R.drawable.no_sound);
            } else {
                mSound = true;
                btnSound.setBackgroundResource(R.drawable.sound);
            }
        });

        btnInviteFriends.setOnClickListener(view -> {


            Intent share_intent = new Intent(Intent.ACTION_SEND);
            share_intent.setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, share_my_app)
                    .putExtra(Intent.EXTRA_SUBJECT, "Candy Buzz");
            startActivity(Intent.createChooser(share_intent, "Share"));

        });

    }


    private void setToFullScreen() {
        findViewById(R.id.activity_start).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
