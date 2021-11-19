package com.candybuzz;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import androidx.annotation.NonNull;

import com.candybuzz.utils.PixelHelper;

@SuppressLint("AppCompatCustomView")
public class Candy extends ImageView implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    private ValueAnimator mAnimator;
    private CandyListener mListener;
    private boolean mCatch;

    public Candy(Context context) {
        super(context);
    }

    public Candy(@NonNull Context context, int color, int rawHeight) {
        super(context);
        mListener = (CandyListener) context;
        this.setImageResource(R.drawable.candy_band);
        this.setColorFilter(color);
        setLayoutParams(new ViewGroup.LayoutParams(PixelHelper.pixelsToDp(rawHeight / 2, context),
                PixelHelper.pixelsToDp(rawHeight, context)));
    }

    public void releaseCandy(int screenHeight, int duration) {
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(duration);
        mAnimator.setFloatValues(screenHeight, 0f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setTarget(this);
        mAnimator.addListener(this);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        setY((float) valueAnimator.getAnimatedValue());
    }

    @Override
    public void onAnimationStart(Animator animator) {
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if (!mCatch) mListener.CatchCandy(this, false);
    }

    @Override
    public void onAnimationCancel(Animator animator) {
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!mCatch && event.getAction() == MotionEvent.ACTION_DOWN) {
            mListener.CatchCandy(this, true);
            mCatch = true;
            mAnimator.cancel();
        }

        return super.onTouchEvent(event);
    }

    public void setCatch(boolean isCandyCatch) {
        mCatch = isCandyCatch;
        if (isCandyCatch
        ) mAnimator.cancel();
    }

    public interface CandyListener {

        void CatchCandy(Candy candy, boolean userTouch);
    }
}
