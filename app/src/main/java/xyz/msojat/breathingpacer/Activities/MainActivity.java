package xyz.msojat.breathingpacer.activities;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import xyz.msojat.breathingpacer.R;
import xyz.msojat.breathingpacer.custom_view.MyView;

public class MainActivity extends AppCompatActivity {

    private boolean isDrawing = false;

    private Button btnStart;
    private Button btnEnd;
    private Chronometer chronometer;
    private TextView tvInhaleTime;
    private TextView tvExhaleTime;
    private TextView tvPauseTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MyView animacija = (MyView) findViewById(R.id.my_view);
        init();

        tvInhaleTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animacija.startAnimating();
                if(isDrawing == false) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                }
                isDrawing = true;
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animacija.stopAnimating();
                chronometer.stop();
                isDrawing = false;
            }
        });
    }

    private void init(){
        btnStart = (Button) findViewById(R.id.btn_start);
        btnEnd = (Button) findViewById(R.id.btn_end);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        tvInhaleTime = (TextView) findViewById(R.id.tv_inhale_time);
        tvExhaleTime = (TextView) findViewById(R.id.tv_exhale_time);
        tvPauseTime = (TextView) findViewById(R.id.tv_pause_time);
    }
}
