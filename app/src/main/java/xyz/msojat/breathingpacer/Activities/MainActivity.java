package xyz.msojat.breathingpacer.activities;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import xyz.msojat.breathingpacer.R;
import xyz.msojat.breathingpacer.custom_view.MyView;

public class MainActivity extends AppCompatActivity {

    private boolean crta = false;

    private MyView animacija;
    private Button pocetak;
    private Button kraj;
    private Chronometer trajanje;

    @Override
    protected void onStop() {
        animacija.stopAnimating();
        trajanje.stop();
        crta = false;
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animacija = (MyView) findViewById(R.id.my_view);

        pocetak = (Button) findViewById(R.id.btn_pocetak);
        kraj = (Button) findViewById(R.id.btn_kraj);
        trajanje = (Chronometer) findViewById(R.id.chronometer);



        pocetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animacija.startAnimating();
                if(crta == false) {
                    trajanje.setBase(SystemClock.elapsedRealtime());
                    trajanje.start();
                }
                crta = true;
            }
        });

        kraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animacija.stopAnimating();
                trajanje.stop();
                crta = false;
            }
        });
    }
}
