package xyz.msojat.breathingpacer.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

import xyz.msojat.breathingpacer.R;


public class MyView extends View {

    private static float uiRefreshRate = 60; //FPS
    private static float animationRefreshingInterval; //millis
    private static float animationDurationInMillis; //millis
    private static float numberOfFrames;

    private int width;

    // Povezivanje MyView klase na glavnu dretvu (main thread)
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private Paint linePaint;
    private Paint dotPaint;

    private PointF startPoint;
    private PointF endPoint;
    private PointF checkPoint1;
    private PointF checkPoint2;

    private long previousFrameTime = 0;
    private long currentFrameTime = 0;
    private long previousAnimationTime = 0;
    private long currentAnimationTime = 0;

    private double inhaleTime = 3;
    private double exhaleTime = 6;
    private double pauseTime = 0.5;

    private PointF[] frames;
    private int currentFrame;

    private Path bezier;

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
        dotPaint.setStrokeWidth(2f);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1.5f);

        uiRefreshRate = ((WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getRefreshRate();

        animationRefreshingInterval = TimeUnit.SECONDS.toMillis(1L) / uiRefreshRate;
        calculateDurationTime();
    }

    /**
     * Računa točke (frames) i pokreće animaciju na glavnoj dretvi (main thread).
     */
    public void startAnimating() {
        calculateFrames();
        previousAnimationTime = System.currentTimeMillis();
        uiHandler.post(invalidateUI);
    }

    /**
     * Vraća trenutnu točku (frame) na 0 i zaustavlja animaciju.
     */
    public void stopAnimating() {
        currentFrame = 0;
        previousFrameTime = 0;
        currentFrameTime = 0;
        previousAnimationTime = 0;
        currentAnimationTime = 0;
        invalidate();
        uiHandler.removeCallbacks(invalidateUI);
    }

    private Runnable invalidateUI = new Runnable() {
        @Override
        public void run() {
            if (hasFrameToDraw()) {
                invalidate();
                uiHandler.postDelayed(this, ((long) animationRefreshingInterval));
            }
        }
    };

    /**
     * Metoda koja računa točke (frames) na grafu.
     */
    private void calculateFrames() {
        if (hasFrameToDraw()) {
            return;
        }

        frames = new PointF[(int) numberOfFrames + 1];
        PointF lastFrame = new PointF(0, 0);

        float t = 0f;
        int j = 0;
        float pathStepT;
        float periodPercentage;

        periodPercentage = (float) ((inhaleTime * 1000) / animationDurationInMillis);
        Log.d("bp", "inhalePercentage: " + periodPercentage);
        Log.d("bp", "numberOfFrames: " + numberOfFrames);

        pathStepT = 30f / (numberOfFrames * periodPercentage);
        Log.d("bp", "pathStepT inhale: " + pathStepT);

        while ((lastFrame.x < (width * 0.3)) && (j < frames.length)) {
            frames[j] = calculateBezierPoint(t / 100.0f, startPoint, checkPoint1,
                    checkPoint2, endPoint);
            t += pathStepT;
            lastFrame = frames[j];
            Log.d("bp", lastFrame.toString() + " j: " + j);
            j++;
        }

//        for (j = 0; j < (numberOfFrames * periodPercentage); j++) {
//            frames[j] = calculateBezierPoint(t / 100.0f, startPoint, checkPoint1,
//                    checkPoint2, endPoint);
//            t += pathStepT;
//        }

        periodPercentage = (float) ((pauseTime * 1000) / animationDurationInMillis);
        pathStepT = 5f / (numberOfFrames * periodPercentage);
        Log.d("bp", "pathStepT pause: " + pathStepT);

        while ((lastFrame.x < (width * 0.35)) && (j < frames.length)) {
            frames[j] = calculateBezierPoint(t / 100.0f, startPoint, checkPoint1,
                    checkPoint2, endPoint);
            t += pathStepT;
            lastFrame = frames[j];
            Log.d("bp", lastFrame.toString() + " j: " + j);
            j++;
        }
//        for (j = j; j < (numberOfFrames * periodPercentage); j++) {
//            frames[j] = calculateBezierPoint(t / 100.0f, startPoint, checkPoint1,
//                    checkPoint2, endPoint);
//            t += pathStepT;
//            Log.d("bp", frames[j].toString());
//        }

        periodPercentage = (float) ((exhaleTime * 1000) / animationDurationInMillis);
        pathStepT = 60f / (numberOfFrames * periodPercentage);
        Log.d("bp", "pathStepT exhale: " + pathStepT);

        while ((lastFrame.x < (width * 0.95)) && (j < frames.length)) {
            frames[j] = calculateBezierPoint(t / 100.0f, startPoint, checkPoint1,
                    checkPoint2, endPoint);
            t += pathStepT;
            lastFrame = frames[j];
            Log.d("bp", lastFrame.toString() + " j: " + j);
            j++;
        }
//        for (j = j; j < (numberOfFrames * periodPercentage); j++) {
//            frames[j] = calculateBezierPoint(t / 100.0f, startPoint, checkPoint1,
//                    checkPoint2, endPoint);
//            t += pathStepT;
//        }

        periodPercentage = (float) ((pauseTime * 1000) / animationDurationInMillis);
        pathStepT = 5f / (numberOfFrames * periodPercentage);
        Log.d("bp", "pathStepT pause: " + pathStepT);

        while ((lastFrame.x < width) && (j < frames.length)) {
            frames[j] = calculateBezierPoint(t / 100.0f, startPoint, checkPoint1,
                    checkPoint2, endPoint);
            t += pathStepT;
            lastFrame = frames[j];
            Log.d("bp", lastFrame.toString() + " j: " + j);
            j++;
        }
//        for (j = j; j < numberOfFrames; j++) {
//            frames[j] = calculateBezierPoint(t / 100.0f, startPoint, checkPoint1,
//                    checkPoint2, endPoint);
//            t += pathStepT;
//        }

        frames[frames.length - 1] = new PointF(endPoint.x, endPoint.y);

        currentFrame = 0;
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.width = w;
        startPoint = new PointF(10, h - 10);
        endPoint = new PointF(w - 10, h - 10);
        checkPoint1 = new PointF((w * 0.40f) - (w / 100f * 0f), -1f * h);
        checkPoint2 = new PointF((w * 0.20f) - (w / 100f * 0f), h - 10f);

        bezier = new Path();
        bezier.moveTo(startPoint.x, startPoint.y);
        bezier.cubicTo(checkPoint1.x, checkPoint1.y, checkPoint2.x,
                checkPoint2.y, endPoint.x, endPoint.y);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        //TODO: DELETE - Dots showing t
        drawDot(canvas, calculateBezierPoint(0.30f, startPoint, checkPoint1, checkPoint2, endPoint), dotPaint);
        drawDot(canvas, calculateBezierPoint(0.35f, startPoint, checkPoint1, checkPoint2, endPoint), dotPaint);
        drawDot(canvas, calculateBezierPoint(0.95f, startPoint, checkPoint1, checkPoint2, endPoint), dotPaint);
        drawDot(canvas, calculateBezierPoint(1f, startPoint, checkPoint1, checkPoint2, endPoint), dotPaint);

        float timeDifference = 1;
        currentFrameTime = System.currentTimeMillis();
        currentAnimationTime = System.currentTimeMillis();

        if (bezier != null) {
            drawBezier(canvas, bezier, linePaint);
        }
        
        if ((currentAnimationTime - previousAnimationTime) >= animationDurationInMillis) {
            previousAnimationTime = System.currentTimeMillis();
            currentFrame = 0;
            drawDot(canvas, startPoint, dotPaint);
            return;
        }

        if (!hasFrameToDraw()) {
            drawDot(canvas, startPoint, dotPaint);
            return;
        }

        if (previousFrameTime == 0) {
            previousFrameTime = System.currentTimeMillis();
        }

        if (!(currentFrameTime == previousFrameTime)) {
            if ((currentFrameTime - previousFrameTime) > animationRefreshingInterval) {
                timeDifference = ((currentFrameTime - previousFrameTime) /
                        animationRefreshingInterval);
            }
            previousFrameTime = currentFrameTime;
        }

        if ((currentFrame + timeDifference) >= frames.length) {
            currentFrame = 0;
        } else {
            currentFrame = (int) (currentFrame + timeDifference);
        }

        final PointF currentPoint = frames[currentFrame];

//        TODO: Delete comment
//        Log.d("bp", currentPoint.toString() + " <-Point; Frame -> " + currentFrame + " ; Frames Lenght: " + frames.length);
        drawDot(canvas, currentPoint, dotPaint);
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

    private void drawBezier(final Canvas canvas, final Path path, final Paint paint) {
        canvas.drawPath(path, paint);
    }


    private PointF calculateBezierPoint(float t, PointF s, PointF c1, PointF c2, PointF e) {
        float u = 1f - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;

        PointF p = new PointF(s.x * uuu, s.y * uuu);
        p.x += 3 * uu * t * c1.x;
        p.y += 3 * uu * t * c1.y;
        p.x += 3 * u * tt * c2.x;
        p.y += 3 * u * tt * c2.y;
        p.x += ttt * e.x;
        p.y += ttt * e.y;

        return p;
    }

    public void changeDurationTime(double inhaleTime, double exhaleTime, double pauseTime) {
        this.inhaleTime = inhaleTime;
        this.exhaleTime = exhaleTime;
        this.pauseTime = pauseTime;
        calculateDurationTime();
        frames = null;
    }

    private void calculateDurationTime() {
        animationDurationInMillis = (float) ((inhaleTime + exhaleTime + (pauseTime*2)) * 1000);
        numberOfFrames = animationDurationInMillis / animationRefreshingInterval;
    }
}
