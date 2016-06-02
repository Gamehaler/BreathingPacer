package xyz.msojat.breathingpacer.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import xyz.msojat.breathingpacer.R;
import xyz.msojat.breathingpacer.View.MyView;

public class MainActivity extends AppCompatActivity {

    private Button pocetak;
    private Button kraj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MyView animacija = (MyView) findViewById(R.id.my_view);

        pocetak = (Button) findViewById(R.id.btn_pocetak);
        kraj = (Button) findViewById(R.id.btn_kraj);

        pocetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animacija.startAnimating();
            }
        });

        kraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animacija.stopAnimating();
            }
        });
    }
}
