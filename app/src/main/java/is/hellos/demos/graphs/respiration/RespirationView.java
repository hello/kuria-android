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
import is.hellos.demos.broadcastreceivers.HapticFeedbackBroadcastReceiver;
import is.hellos.demos.graphs.GraphDrawable;
import is.hellos.demos.graphs.GraphView;
import is.hellos.demos.models.respiration.RespirationProb;
import is.hellos.demos.models.respiration.RespirationStat;

/**
 * Created by simonchen on 5/8/17.
 */

public class RespirationView extends GraphView
        implements ValueAnimator.AnimatorUpdateListener {

    private final static float RESTING_SCALE = 0.1f;
    private static final float MAX_SCALE = 0.4f;
    private final static long DEFAULT_DURATION_MS = 1000;
    private final ValueAnimator animator;
    private ValueAnimator colorAnimator;
    @ColorInt
    private final int activeColor;
    @ColorInt
    private final int restingColor;
    @ColorInt
    private final int inactiveColor;
    @ColorInt
    private final int inactiveSecondaryColor;
    @Nullable
    private RespirationStat currentRespirationStat;
    @Nullable
    private RespirationProb currentRespirationProb;

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
        this.inactiveSecondaryColor = ContextCompat.getColor(context, R.color.respiration_expanding_circle_inactive);
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
                updateDrawable(currentRespirationProb);
                updateDuration(currentRespirationStat);
                postOnAnimationDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RespirationView.this.sendRespirationBroadcast();
                    }
                }, animation.getDuration() / 2);
            }
        });
    }

    public void update(RespirationProb respirationProb) {
        this.currentRespirationProb = respirationProb;
        updateDrawable(respirationProb);
    }

    private void updateDrawable(@Nullable final RespirationProb respirationProb) {
        if (respirationProb == null) {
            return;
        }
        final float targetRadius = getValidInterpolatedRadius(respirationProb.getInhaling());
        final RespirationDrawable respirationDrawable = (RespirationDrawable) getBackground();
        respirationDrawable.setRadius(targetRadius);
        respirationDrawable.setExpandingCircleColor(getExpandingCircleColor(respirationProb.getInhaling()));

    }

    public void update(@NonNull final RespirationStat respirationStat) {
        this.currentRespirationStat = respirationStat;

        if (this.colorAnimator != null && this.colorAnimator.isRunning()) {
            this.colorAnimator.end();
        }

        this.colorAnimator = createColorAnimator(respirationStat);
        this.colorAnimator.start();

        if (this.animator.isRunning()) {
            return;
        }

        updateDuration(respirationStat);
    }

    ValueAnimator createColorAnimator(@NonNull final RespirationStat respirationStat) {
        final RespirationDrawable respirationDrawable = (RespirationDrawable) getBackground();
        @ColorInt
        final int targetColor = respirationStat.isHasRespiration() ? restingColor : inactiveColor;
        final ValueAnimator colorAnimator = ValueAnimator.ofArgb(respirationDrawable.getInnerCircleColor(), targetColor);
        colorAnimator.setInterpolator(this.animator.getInterpolator());
        colorAnimator.setDuration(DEFAULT_DURATION_MS);
        colorAnimator.setRepeatCount(0);
        colorAnimator.setRepeatMode(ValueAnimator.RESTART);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                respirationDrawable.setInnerCircleColor((int) animation.getAnimatedValue());
            }
        });
        return colorAnimator;
    }

    private void updateDuration(@Nullable final RespirationStat respirationStat) {
        if (respirationStat == null) {
            return;
        }
        // calculate duration to match respiration BPM where one full round approx one breath (inhale + exhale)
        this.animator.setDuration( (long) (respirationStat.getBreathDurationSeconds() * 1000));
        final RespirationDrawable respirationDrawable = (RespirationDrawable) getBackground();
        respirationDrawable.setBreathRateText(getFormattedBreathRate());
        //todo move start elsewhere
        this.animator.start();
    }

    private float getRestingRadius() {
        return Math.min(getWidth(), getHeight()) * RESTING_SCALE;
    }

    private float getMaxRadius() {
        return Math.min(getWidth(), getHeight()) * MAX_SCALE;
    }

    float getValidInterpolatedRadius(@FloatRange(from = 0, to = 1) final float value) {
        return (1 - value) * getRestingRadius() + value * getMaxRadius();
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
        final long duration = (long) this.currentRespirationStat.getBreathDurationSeconds() * 100;
        LocalBroadcastManager.getInstance(getContext())
                .sendBroadcast(HapticFeedbackBroadcastReceiver.getIntent(duration));
    }

    @ColorInt
    int getExpandingCircleColor(@FloatRange(from = 0, to = 1.0f) final float fraction) {
        if (this.currentRespirationStat != null && this.currentRespirationStat.isHasRespiration()) {
            return ColorUtils.blendARGB(restingColor, activeColor, fraction);
        } else {
            return inactiveSecondaryColor;
        }
    }

    @ColorInt
    int getInnerCircleColor() {
        if (this.currentRespirationStat != null && this.currentRespirationStat.isHasRespiration()) {
            return restingColor;
        } else {
            return inactiveColor;
        }
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
        respirationDrawable.invalidateSelf();
    }
}
