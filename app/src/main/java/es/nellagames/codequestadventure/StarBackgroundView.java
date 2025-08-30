package es.nellagames.codequestadventure;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class StarBackgroundView extends View {
    private static final int NUM_STARS = 25;
    private float[] starX = new float[NUM_STARS];
    private float[] starY = new float[NUM_STARS];
    private float[] starSpeed = new float[NUM_STARS];
    private float[] starSize = new float[NUM_STARS];
    private Paint starPaint;
    private Random random = new Random();

    public StarBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setColor(Color.WHITE);
        for (int i = 0; i < NUM_STARS; i++) {
            starX[i] = random.nextInt(1000);
            starY[i] = random.nextInt(1000);
            starSpeed[i] = 2f + random.nextFloat() * 2f;
            starSize[i] = 3f + random.nextFloat() * 4f;
        }
        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        for (int i = 0; i < NUM_STARS; i++) {
            canvas.drawCircle(starX[i], starY[i], starSize[i], starPaint);
        }
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(30_000); // 30 segundos
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(animation -> {
            int w = getWidth();
            int h = getHeight();
            for (int i = 0; i < NUM_STARS; i++) {
                starY[i] += starSpeed[i];
                if (starY[i] > h) {
                    starY[i] = 0;
                    starX[i] = random.nextInt((w > 0) ? w : 1000);
                    starSize[i] = 2f + random.nextFloat() * 5f;
                }
            }
            invalidate();
        });
        animator.start();
    }
}
