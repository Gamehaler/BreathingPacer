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

import org.joda.time.LocalTime;

import java.util.concurrent.TimeUnit;

import xyz.msojat.breathingpacer.R;


public class MyView extends View {

    private static float uiRefreshRate = 60; //FPS
    private static float animationRefreshingInterval; //millis
    private static float animationDurationInMillis; //millis
    private static float numberOfFrames;

    // Povezivanje MyView klase sa main dretvom
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private Paint linePaint;
    private Paint dotPaint;

    private PointF startPoint;
    private PointF endPoint;
    private PointF checkPoint1;
    private PointF checkPoint2;
    private float pathStepX;
    private float pathStepY;

    private LocalTime prethodnoVrijeme;
    private LocalTime trenutnoVrijeme;

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
        animationDurationInMillis = 10000L;
        numberOfFrames = animationDurationInMillis / animationRefreshingInterval;
    }

    /**
     * Računa točke (frames) i pokreće animaciju na glavnoj dretvi (main thread).
     */
    public void startAnimating() {
        calculateFrames();
        uiHandler.post(invalidateUI);
    }

    /**
     * Vraća trenutnu točku (frame) na 0 i zaustavlja animaciju.
     */
    public void stopAnimating() {
        currentFrame = 0;
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
        float x = startPoint.x;
        float y = startPoint.y;

        pathStepT = 100.0f / numberOfFrames;

        for (int i = 0; i < numberOfFrames; i++) {
            frames[i] = calculateBezierPoint(t / 100.0f, startPoint, checkPoint1,
                    checkPoint2, endPoint);
            t += pathStepT;
        }

        /*
        for(int i = 0; i < numberOfFrames; i++){
            frames[i] = new PointF(x, y);
            x += pathStepX;
            y -= pathStepY;
        }
        */

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

        pathStepX = Math.abs(endPoint.x - startPoint.x) / numberOfFrames;
        pathStepY = Math.abs(endPoint.y - startPoint.y) / numberOfFrames;

        bezier = new Path();
        bezier.moveTo(startPoint.x, startPoint.y);
        bezier.cubicTo(checkPoint1.x, checkPoint1.y, checkPoint2.x,
                checkPoint2.y, endPoint.x, endPoint.y);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        int razlika = 1;

        if (bezier != null) {
            drawBezier(canvas, bezier, linePaint);
        }

        if (!hasFrameToDraw()) {
            drawDot(canvas, startPoint, dotPaint);
            return;
        }

        trenutnoVrijeme = new LocalTime();

        if (prethodnoVrijeme == null) {
            prethodnoVrijeme = new LocalTime();
        }

        if (!trenutnoVrijeme.equals(prethodnoVrijeme)) {
            if ((trenutnoVrijeme.compareTo(prethodnoVrijeme)) > animationRefreshingInterval) {
                razlika = (int) ((trenutnoVrijeme.compareTo(prethodnoVrijeme)) /
                        animationRefreshingInterval);
            }
            prethodnoVrijeme = trenutnoVrijeme;
        }

        if ((currentFrame + razlika) >= frames.length) {
            currentFrame = 0;
        } else {
            currentFrame = currentFrame + razlika;
        }

        final PointF currentPoint = frames[currentFrame];
        drawDot(canvas, currentPoint, dotPaint);

//        if (currentFrame + 1 == frames.length){
//            currentFrame = -1;
//        }
//        currentFrame++;
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


    PointF calculateBezierPoint(float t, PointF s, PointF c1, PointF c2, PointF e) {
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
}
