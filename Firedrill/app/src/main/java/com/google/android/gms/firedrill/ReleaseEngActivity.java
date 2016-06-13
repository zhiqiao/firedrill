package com.google.android.gms.firedrill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class ReleaseEngActivity extends Activity {

    String gameId;
    int lastRollout = 0;

    String[] percentages = new String[] {"0%", "0.02%", "0.1%", "1%", "5%", "25%", "50%", "100%"};

    public static void start(Context context, String gameId) {
        context.startActivity(new Intent(context, ReleaseEngActivity.class).putExtra("game_id", gameId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_eng);
        gameId = getIntent().getStringExtra("game_id");
        final TextView percentage = (TextView) findViewById(R.id.percentage);
        SeekBar rollout = (SeekBar) findViewById(R.id.rollout);
        rollout.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(++lastRollout);
                seekBar.setEnabled(false);
                percentage.setText("Public rollout: " + percentages[lastRollout]);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
