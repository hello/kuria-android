package is.hellos.demos.graphs.respiration;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.Locale;

import is.hellos.demos.R;
import is.hellos.demos.activities.RespirationActivity;
import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.graphs.GraphView;
import is.hellos.demos.models.respiration.RespirationStat;

/**
 * Created by simonchen on 5/8/17.
 */

public class RespirationView extends GraphView
        implements ValueAnimator.AnimatorUpdateListener {

    private final static float RESTING_SCALE = 0.1f;
    private static final float MAX_SCALE = 0.9f;
    private static final float RADIUS_SCALE = 20;
    private final static long DEFAULT_DURATION_MS = 1000;
    private final ValueAnimator animator;
    @ColorInt
    private final int activeColor;
    @ColorInt
    private final int restingColor;
    @ColorInt
    private final int inactiveColor;
    @Nullable
    private RespirationStat currentRespirationStat;

    public RespirationView(Context context) {
        this(context, null);
    }

    public RespirationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RespirationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.activeColor = ContextCompat.getColor(context, R.color.respiration_expanding_circle);
        this.restingColor = ContextCompat.getColor(context, R.color.respiration_inner_circle_active);
        this.inactiveColor = ContextCompat.getColor(context, R.color.respiration_inner_circle_inactive);
        this.animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new AccelerateDecelerateInterpolator(context, attrs));
        animator.setDuration(DEFAULT_DURATION_MS);
        this.animator.setRepeatCount(ValueAnimator.INFINITE);
        this.animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(this);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                updateAnimator(currentRespirationStat);
                postOnAnimationDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RespirationView.this.sendRespirationBroadcast();
                    }
                }, animation.getDuration() / 2);
            }
        });
    }

    public void update(@NonNull final RespirationStat respirationStat) {
        this.currentRespirationStat = respirationStat;
        if (this.animator.isRunning()) {
            return;
        }

        updateAnimator(respirationStat);
    }

    private void updateAnimator(@Nullable final RespirationStat respirationStat) {
        if (respirationStat == null) {
            return;
        }

        //scaled radius end value
        this.animator.setFloatValues(
                getRestingRadius(),
                getValidScaledRadius(respirationStat.getEnergyDb(), RADIUS_SCALE),
                getRestingRadius()
        );
        // calculate duration to match respiration BPM where one full round approx one breath (inhale + exhale)
        this.animator.setDuration( (long) (respirationStat.getBreathDurationSeconds() * 1000));

        this.animator.start();

    }

    private float getRestingRadius() {
        return Math.min(getWidth(), getHeight()) * RESTING_SCALE;
    }

    private float getMaxRadius() {
        return Math.min(getWidth(), getHeight()) * MAX_SCALE;
    }

    float getValidScaledRadius(final float radius, final float scale) {
        return Math.min(radius * scale, getMaxRadius());
    }

    String getUnknownBreathRate() {
        return "--";
    }

    String getFormattedBreathRate() {
        if (currentRespirationStat == null || !currentRespirationStat.isHasRespiration()) {
            return getUnknownBreathRate();
        }
        return String.format(Locale.US, "%.0f",this.currentRespirationStat.getBreathsPerMinute());
    }

    private void sendRespirationBroadcast() {
        if (this.currentRespirationStat == null || !this.currentRespirationStat.isHasRespiration()) {
            return;
        }
        LocalBroadcastManager.getInstance(getContext())
                .sendBroadcast(RespirationActivity.RespirationBroadcastReceiver.getIntent(this.currentRespirationStat));
    }

    @ColorInt
    int getExpandingCircleColor(@FloatRange(from = 0, to = 1.0f) final float fraction) {
        return ColorUtils.blendARGB(restingColor, activeColor, fraction);
    }

    @ColorInt
    int getInnerCircleColor() {
        return restingColor;
    }

    @Override
    public GraphDrawable getGraphDrawable() {
        final RespirationDrawable drawable = new RespirationDrawable(
                getWidth(),
                getHeight(),
                getRestingRadius(),
                getUnknownBreathRate()
                );
        drawable.setInnerCircleColor(getInnerCircleColor());
        return drawable;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        final RespirationDrawable respirationDrawable = (RespirationDrawable) getBackground();
        respirationDrawable.setInnerCircleColor(getInnerCircleColor());
        respirationDrawable.setExpandingCircleColor(getExpandingCircleColor(animation.getAnimatedFraction()));
        respirationDrawable.setRadius((float) animation.getAnimatedValue());
        respirationDrawable.setBreathRateText(getFormattedBreathRate());
        respirationDrawable.invalidateSelf();
    }
}
