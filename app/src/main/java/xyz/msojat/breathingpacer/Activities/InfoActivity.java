package xyz.msojat.breathingpacer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import xyz.msojat.breathingpacer.R;

public class InfoActivity extends AppCompatActivity {
    private boolean firstOpen;
    private Button nastavak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        nastavak = (Button) findViewById(R.id.btn_nastavak);
        nastavak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        });
    }
}
