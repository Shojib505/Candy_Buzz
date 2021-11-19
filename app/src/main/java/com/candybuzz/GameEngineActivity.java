package com.candybuzz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;


import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.candybuzz.utils.HighScoreHelper;
import com.candybuzz.utils.SoundHelper;


public class GameEngineActivity extends AppCompatActivity implements Candy.CandyListener {
    private static final int MIN_ANIMATION_DELAY = 700, MAX_ANIMATION_DELAY = 1500,
            MIN_ANIMATION_DURATION = 1000, MAX_ANIMATION_DURATION = 6000, NUMBER_OF_LIFE = 5;
    private final Random mRandom = new Random();
    private final int[] mCandyColor = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.
            GRAY, Color.GREEN, Color.LTGRAY,Color.MAGENTA,Color.RED,Color.YELLOW};
    private int mCandyPerLevel = 10, mCandyCatch, mScreenWidth, mScreenHeight, mLevel, mScore, mLifeUsed;
    private boolean mPlaying, mSound, mMusic, mGame, mGameStopped = true;
    private TextView mScoreDisplay, mLevelDisplay;
    private Button mGoButton;
    private ViewGroup mContentView;
    private SoundHelper mSoundHelper, mMusicHelper;
    private final List<ImageView> mLifeImages = new ArrayList<>();
    private final List<Candy> mCandies = new ArrayList<>();
    private Animation mAnimation;


    @SuppressLint("FindViewByIdCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_play_activity);
        getWindow().setBackgroundDrawableResource(R.drawable.backgorund);
        mContentView = findViewById(R.id.activity_main);
        setToFullScreen();
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        mMusicHelper = new SoundHelper(this);
        mMusicHelper.prepareMusicPlayer(this);
        Intent intent = getIntent();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenWidth = mContentView.getWidth();
                    mScreenHeight = mContentView.getHeight();
                }
            });
        }

        mContentView.setOnClickListener(view -> setToFullScreen());
        mScoreDisplay = findViewById(R.id.score_display);
        mLevelDisplay = findViewById(R.id.level_display);
        mLifeImages.add(findViewById(R.id.heart1));
        mLifeImages.add(findViewById(R.id.heart2));
        mLifeImages.add(findViewById(R.id.heart3));
        mLifeImages.add(findViewById(R.id.heart4));
        mLifeImages.add(findViewById(R.id.heart5));
        mGoButton = findViewById(R.id.go_button);
        updateDisplay();
        mSoundHelper = new SoundHelper(this);
        mSoundHelper.prepareMusicPlayer(this);
        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);

        mAnimation.setDuration(100);

        if (intent.hasExtra(MainActivity.SOUND))
            mSound = intent.getBooleanExtra(MainActivity.SOUND, true);

        if (intent.hasExtra(MainActivity.MUSIC))
            mMusic = intent.getBooleanExtra(MainActivity.MUSIC, true);

        findViewById(R.id.btn_back_gameplay).setOnClickListener(view -> {
            view.startAnimation(mAnimation);
            gameOver();
            finish();
        });
    }

    private void setToFullScreen() {
        findViewById(R.id.activity_main).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mGame) {
            if (mMusic) mMusicHelper.playMusic();
        }
    }

    private void startGame() {
        setToFullScreen();
        mScore = 0;
        mLevel = 0;
        mLifeUsed = 0;
        mGameStopped = false;
        mGame = true;
        if (mMusic) mMusicHelper.playMusic();
        for (ImageView pin : mLifeImages) pin.setImageResource(R.drawable.life);
        startLevel();
    }

    private void startLevel() {
        mLevel++;
        updateDisplay();
        new CandyLauncher().execute(mLevel);
        mPlaying = true;
        mCandyCatch = 0;
        mGoButton.setVisibility(View.INVISIBLE);
    }

    private void finishLevel() {
        Toast.makeText(this, getString(R.string.finish_level) + mLevel, Toast.LENGTH_SHORT).show();
        mPlaying = false;
        mGoButton.setText(MessageFormat.format("{0} {1}", getString(R.string.level_start), mLevel + 1));
        mGoButton.setVisibility(View.VISIBLE);
    }


    public void goButtonClickHandler(View view) {
        if (mGameStopped) startGame();
        else startLevel();
    }

    @Override
    public void CatchCandy(Candy candy, boolean userTouch) {
        mCandyCatch++;
        if (mSound) mSoundHelper.playSound();
        mContentView.removeView(candy);
        mCandies.remove(candy);
        if (userTouch) mScore++;
        else {
            mLifeUsed++;
            if (mLifeUsed <= mLifeImages.size())

                mLifeImages.get(mLifeUsed - 1).setImageResource(R.drawable.no_life);

            Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

            vibrator.vibrate(400);

            if (mLifeUsed == NUMBER_OF_LIFE) {
                gameOver();
                return;
            }
        }

        updateDisplay();

        if (mCandyCatch == mCandyPerLevel) {
            finishLevel();
            mCandyPerLevel += 10;
        }
    }

    private void gameOver() {
        //
        if (mMusic) mMusicHelper.pauseMusic();
        mGame = false;

        for (Candy candy : mCandies) {
            mContentView.removeView(candy);
            candy.setCatch(true);
        }

        mCandies.clear();
        mPlaying = false;
        mGameStopped = true;
        mGoButton.setText(R.string.start_game);

        if (HighScoreHelper.isTopScore(this, mScore)) {

            HighScoreHelper.setTopScore(this, mScore);

        }

        mGoButton.setVisibility(View.VISIBLE);
    }

    private void updateDisplay() {
        mScoreDisplay.setText(String.valueOf(mScore));
        mLevelDisplay.setText(String.valueOf(mLevel));
    }

    private void launchCandy(int x) {
        Candy candy = new Candy(this, mCandyColor[mRandom.nextInt(mCandyColor.length)], 150);
        mCandies.add(candy);
        candy.setX(x);
        candy.setY(mScreenHeight + candy.getHeight());
        mContentView.addView(candy);
        candy.releaseCandy(mScreenHeight, Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameOver();
        if (mGame) {
            if (mMusic) mMusicHelper.pauseMusic();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CandyLauncher extends AsyncTask<Integer, Integer, Void> {

        @Nullable
        @Override
        protected Void doInBackground(@NonNull Integer... params) {
            if (params.length != 1) throw new AssertionError(getString(R.string.assertion_message));
            int minDelay = Math.max(MIN_ANIMATION_DELAY, (MAX_ANIMATION_DELAY - ((params[0] - 1) * 500))) / 2;
            int candyLaunched = 0;

            while (mPlaying && candyLaunched < mCandyPerLevel) {
                Random random = new Random(new Date().getTime());
                publishProgress(random.nextInt(mScreenWidth - 200));
                candyLaunched++;

                try {
                    Thread.sleep(random.nextInt(minDelay) + minDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            launchCandy(values[0]);
        }
    }
}
