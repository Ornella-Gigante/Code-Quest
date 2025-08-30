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
    private float[] starSpeedY = new float[NUM_STARS];
    private float[] starSpeedX = new float[NUM_STARS];
    private float[] starSize = new float[NUM_STARS];
    private Paint starPaint;
    private Random random = new Random();

    public StarBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setColor(Color.WHITE);
        // Inicializa posiciones y velocidades aleatorias por estrella
        for (int i = 0; i < NUM_STARS; i++) {
            starX[i] = random.nextInt(1000); // X aleatoria en ancho virtual
            starY[i] = random.nextInt(1000); // Y aleatoria para desfasar las iniciales
            starSpeedY[i] = 2f + random.nextFloat() * 2f; // velocidad vertical distinta
            starSpeedX[i] = -0.5f + random.nextFloat(); // velocidad horizontal entre -0.5 y +0.5
            starSize[i] = 2f + random.nextFloat() * 4f;
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
                starY[i] += starSpeedY[i];
                starX[i] += starSpeedX[i];
                // Si sale por abajo, reinicializa en X/Y random arriba y con nuevo delta X
                if (starY[i] > h) {
                    starY[i] = 0;
                    starX[i] = random.nextInt((w > 0) ? w : 1000);
                    starSize[i] = 2f + random.nextFloat() * 4f;
                    starSpeedY[i] = 2f + random.nextFloat() * 2f;
                    starSpeedX[i] = -0.5f + random.nextFloat();
                }
                // Si sale por izquierda/derecha, rebota o reinicia
                if (starX[i] < 0) starX[i] = w - 1;
                if (starX[i] > w) starX[i] = 1;
            }
            invalidate();
        });
        animator.start();
    }
}
