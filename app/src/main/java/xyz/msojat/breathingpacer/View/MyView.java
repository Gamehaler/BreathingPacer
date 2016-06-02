package xyz.msojat.breathingpacer.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;


public class MyView extends View {

    private static final long UI_REFRESH_RATE = 60L; //FPS
    private static final long ANIMATION_REFRESHING_INTERVAL = TimeUnit.SECONDS.toMillis(1L) / UI_REFRESH_RATE; //millis
    private static long animationDurationInMillis = 6000L; //millis
    private static long numberOfFrames = animationDurationInMillis / ANIMATION_REFRESHING_INTERVAL;

    // Povezivanje MyView klase sa main dretvom
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private Paint linePaint;
    private Paint dotPaint;

    private PointF startPoint;
    private PointF endPoint;
    private float pathStepX;
    private float pathStepY;

    private PointF[] frames;
    private int currentFrame;

    public MyView(final Context context) {
        super(context);
        init();
    }

    public MyView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(Color.RED);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setStrokeWidth(1.5f);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLUE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1.0f);
    }

    public void startAnimating() {
        calculateFrames();
        uiHandler.post(invalidateUI);
    }

    public void stopAnimating() {
        uiHandler.removeCallbacks(invalidateUI);
    }

    private Runnable invalidateUI = new Runnable() {
        @Override
        public void run() {
            if (hasFrameToDraw()) {
                invalidate();
                uiHandler.postDelayed(this, ANIMATION_REFRESHING_INTERVAL);
            }
        }
    };

    private void calculateFrames() {
        if(hasFrameToDraw()) {
            return;
        }

        frames = new PointF[(int) numberOfFrames + 1];

        float x = startPoint.x;
        float y = startPoint.y;

        for(int i = 0; i < numberOfFrames; i++){
            frames[i] = new PointF(x, y);
            x += pathStepX;
            y -= pathStepY;
        }

        frames[frames.length - 1] = new PointF(endPoint.x, endPoint.y);

        currentFrame = 0;
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        startPoint = new PointF(10, h - 10);
        endPoint = new PointF(w - 10, 10);

        pathStepX = Math.abs(endPoint.x - startPoint.x) / numberOfFrames;
        pathStepY = Math.abs(endPoint.y - startPoint.y) / numberOfFrames;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        drawLine(canvas, startPoint, endPoint, linePaint);

        if(!hasFrameToDraw()) {
            drawDot(canvas, startPoint, dotPaint);
            return;
        }

        final PointF currentPoint = frames[currentFrame];
        drawDot(canvas, currentPoint, dotPaint);

        currentFrame++;
    }

    private boolean hasFrameToDraw() {
        // Vraća TRUE ukoliko ima jos točaka za nacrtati
        // frames mora biti razlicit od null
        // currentFrame ne smije biti zadnji u nizu frame-ova za crtanje
        return frames != null && currentFrame < frames.length;
    }

    private void drawDot(final Canvas canvas, final PointF point, final Paint paint) {
        canvas.drawCircle(point.x, point.y, 10.0f, paint);
    }

    private void drawLine(final Canvas canvas, final PointF start, final PointF stop,
                          final Paint paint) {
        canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
    }
}
