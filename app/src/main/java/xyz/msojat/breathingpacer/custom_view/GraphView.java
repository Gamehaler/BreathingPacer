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


public class GraphView extends View {

    private static float uiRefreshRate = 60; //FPS
    private static float animationRefreshingInterval; //millis
    private static float animationDurationInMillis; //millis
    private static float numberOfFrames;

    private float xControlPoint1;
    private float xControlPoint2;
    private float xControlPoint3;
    private float xControlPoint4;
    private float xControlPoint5;

    private float inhaleCoefficient = 1f;
    private float exhaleCoefficient = 1f;
    private float pauseCoefficient = 1f;

    private float frameStep = 1;

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

    public GraphView(final Context context) {
        super(context);
        init();
    }

    public GraphView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
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

        float t = 0f;
        float pathStepT;

        pathStepT = 100.0f / numberOfFrames;

        for (int i = 0; i < numberOfFrames; i++) {
            frames[i] = calculateBezierPoint(t / 100.0f, startPoint, checkPoint1,
                    checkPoint2, endPoint);
            t += pathStepT;
        }

        frames[frames.length - 1] = new PointF(endPoint.x, endPoint.y);

        currentFrame = 0;
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        startPoint = new PointF(10, h - 10);
        endPoint = new PointF(w - 10, h - 10);
        checkPoint1 = new PointF((w * 0.40f) - (w / 100f * 0f), -1f * h);
        checkPoint2 = new PointF((w * 0.20f) - (w / 100f * 0f), h - 10f);

        bezier = new Path();
        bezier.moveTo(startPoint.x, startPoint.y);
        bezier.cubicTo(checkPoint1.x, checkPoint1.y, checkPoint2.x,
                checkPoint2.y, endPoint.x, endPoint.y);

        xControlPoint1 = startPoint.x;
        xControlPoint2 = w * 0.2413f;
        xControlPoint3 = w * 0.268f;
        xControlPoint4 = w * 0.8217f;
        xControlPoint5 = endPoint.x;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        float currentCoefficient = 1;
        float timeDifference = 1;
        currentFrameTime = System.currentTimeMillis();
        currentAnimationTime = System.currentTimeMillis();

        if (frames != null) {
            if (((currentAnimationTime - previousAnimationTime) < animationDurationInMillis) && currentFrame == frames.length-1){
                drawBezier(canvas, bezier, linePaint);
                drawDot(canvas, frames[(frames.length-1)], dotPaint);
                previousFrameTime = currentFrameTime;
                return;
            }

            if (frames[currentFrame].x < xControlPoint2) {
                currentCoefficient = inhaleCoefficient;
            } else if ((frames[currentFrame].x > xControlPoint2) && (frames[currentFrame].x < xControlPoint3)) {
                currentCoefficient = pauseCoefficient;
            } else if ((frames[currentFrame].x > xControlPoint3) && (frames[currentFrame].x < xControlPoint4)) {
                currentCoefficient = exhaleCoefficient;
            } else if (frames[currentFrame].x > xControlPoint4) {
                currentCoefficient = pauseCoefficient;
            }
        }

        // Crtanje bezierove krivulje.
        if (bezier != null) {
            drawBezier(canvas, bezier, linePaint);
        }

        // VRAĆANJE TOČKE na početak NAKON isteka punog VREMENA ANIMACIJE.
        if ((currentAnimationTime - previousAnimationTime) >= animationDurationInMillis) {
            previousAnimationTime = System.currentTimeMillis();
            currentFrame = 0;
            frameStep = 0;
            drawDot(canvas, startPoint, dotPaint);
            return;
        }

        // Ukoliko nema izračunate točke, CRTA TOČKU NA POČETNOJ POZICIJI.
        if (!hasFrameToDraw()) {
            drawDot(canvas, startPoint, dotPaint);
            return;
        }

        if (previousFrameTime == 0) {
            previousFrameTime = System.currentTimeMillis();
        }

        // Provjera preskakanja frame-a.
        if (!(currentFrameTime == previousFrameTime)) {
            if ((currentFrameTime - previousFrameTime) > animationRefreshingInterval) {
                timeDifference = ((currentFrameTime - previousFrameTime) /
                        animationRefreshingInterval);
            }
            previousFrameTime = currentFrameTime;
        }


        frameStep = frameStep + (timeDifference * currentCoefficient);

        // Računanje koji frame prikazati.
        if ((currentFrame + frameStep) >= frames.length) {
            frameStep = 0;
        } else {
            currentFrame = (int) (currentFrame + frameStep);
            int intFrameStep = (int) frameStep;
            frameStep = frameStep - intFrameStep;
        }

        // Dohvaćanje trenutne točke iz polja.
        final PointF currentPoint = frames[currentFrame];

        // Crtanje točke.
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
        calculateAnimationCoefficient();
        calculateDurationTime();
    }

    private void calculateAnimationCoefficient() {
        float temp = (float) (inhaleTime / 3f);
        inhaleCoefficient = 1f / temp;

        temp = (float) (exhaleTime / 6f);
        exhaleCoefficient = 1f /temp;

        temp = (float) (pauseTime / 0.5f);
        pauseCoefficient = 1f / temp;
    }

    private void calculateDurationTime() {
        animationDurationInMillis = (float) ((inhaleTime + exhaleTime + (pauseTime * 2)) * 1000);
        numberOfFrames = 10000 / animationRefreshingInterval;
    }
}
