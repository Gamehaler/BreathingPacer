package xyz.msojat.breathingpacer.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import xyz.msojat.breathingpacer.R;
import xyz.msojat.breathingpacer.custom_view.MyView;

public class MainActivity extends AppCompatActivity {

    private boolean isDrawing = false;

    private double inhaleTime = 3;
    private double exhaleTime = 6;
    private double pauseTime = 0.5;

    private Button btnStart;
    private Button btnEnd;
    private Chronometer chronometer;
    private LinearLayout llInhale;
    private LinearLayout llExhale;
    private LinearLayout llPause;
    private TextView tvInhaleTime;
    private TextView tvExhaleTime;
    private TextView tvPauseTime;
    private AlertDialog.Builder builder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MyView animacija = (MyView) findViewById(R.id.my_view);
        init();

        llInhale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = builder.setView(getLayoutInflater()
                        .inflate(R.layout.dialog_enter_time, null))
                        .setPositiveButton("Prihvati", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!TextUtils.isEmpty(((EditText) ((Dialog)dialog).findViewById(R.id.et_time))
                                        .getText()
                                        .toString())) {
                                    inhaleTime = Double.valueOf(
                                            ((EditText) ((Dialog) dialog).findViewById(R.id.et_time))
                                                    .getText()
                                                    .toString());
                                }else {
                                    dialog.cancel();
                                }
                                animacija.changeDurationTime(inhaleTime, exhaleTime, pauseTime);
                                tvInhaleTime.setText(String.valueOf(inhaleTime) + "s");
                            }
                        })
                        .setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                dialog.show();
            }
        });

        llExhale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = builder.setView(getLayoutInflater()
                        .inflate(R.layout.dialog_enter_time, null))
                        .setPositiveButton("Prihvati", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!TextUtils.isEmpty(((EditText) ((Dialog) dialog).findViewById(R.id.et_time))
                                        .getText()
                                        .toString())) {
                                    exhaleTime = Double.valueOf(
                                            ((EditText) ((Dialog) dialog).findViewById(R.id.et_time))
                                                    .getText()
                                                    .toString());
                                } else {
                                    dialog.cancel();
                                }
                                animacija.changeDurationTime(inhaleTime, exhaleTime, pauseTime);
                                tvExhaleTime.setText(String.valueOf(exhaleTime) + "s");
                            }
                        })
                        .setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                dialog.show();
            }
        });

        llPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = builder.setView(getLayoutInflater()
                        .inflate(R.layout.dialog_enter_time, null))
                        .setPositiveButton("Prihvati", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!TextUtils.isEmpty(((EditText) ((Dialog) dialog).findViewById(R.id.et_time))
                                        .getText()
                                        .toString())) {
                                    pauseTime = Double.valueOf(
                                            ((EditText) ((Dialog) dialog).findViewById(R.id.et_time))
                                                    .getText()
                                                    .toString());
                                } else {
                                    dialog.cancel();
                                }
                                animacija.changeDurationTime(inhaleTime, exhaleTime, pauseTime);
                                tvPauseTime.setText(String.valueOf(pauseTime) + "s");
                            }
                        })
                        .setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                dialog.show();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animacija.startAnimating();
                if (isDrawing == false) {
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

    private void init() {
        btnStart = (Button) findViewById(R.id.btn_start);
        btnEnd = (Button) findViewById(R.id.btn_end);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        llInhale = (LinearLayout) findViewById(R.id.ll_inhale);
        llExhale = (LinearLayout) findViewById(R.id.ll_exhale);
        llPause = (LinearLayout) findViewById(R.id.ll_pause);
        tvInhaleTime = (TextView) findViewById(R.id.tv_inhale_time);
        tvExhaleTime = (TextView) findViewById(R.id.tv_exhale_time);
        tvPauseTime = (TextView) findViewById(R.id.tv_pause_time);
        builder = new AlertDialog.Builder(this);
    }
}
