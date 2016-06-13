package com.google.android.gms.firedrill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReleaseEngActivity extends Activity {

    String gameId;
    int lastRollout = 0;

    TextView percentage;
    SeekBar rollout;
    TextView stackTrace;

    String[] percentages = new String[] {"0%", "0.02%", "0.1%", "1%", "5%", "25%", "50%", "100%"};

    public static void start(Context context, String gameId) {
        context.startActivity(new Intent(context, ReleaseEngActivity.class).putExtra("game_id", gameId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_eng);
        gameId = getIntent().getStringExtra("game_id");
        percentage =  (TextView) findViewById(R.id.percentage);
        rollout = (SeekBar) findViewById(R.id.rollout);
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
        stackTrace = (TextView) findViewById(R.id.stack_trace);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference re = database.getReference(gameId + "/releaseEngineer");
        re.setValue(new ReleaseEngineer());
        DatabaseReference teams = database.getReference(gameId + "/teams");
        teams.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ClientTeam> teams = new ArrayList();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    teams.add(child.getValue(ClientTeam.class));
                }
                onTeamsChanged(teams);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onTeamsChanged(List<ClientTeam> clientTeams) {
        LinearLayout teams = (LinearLayout) findViewById(R.id.teams);
        teams.removeAllViews();
        boolean shippable = true;
        StringBuilder crash = new StringBuilder();
        for (ClientTeam team : clientTeams) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(team.name);
            checkBox.setChecked(team.shippable);
            checkBox.setTextColor(team.shippable ? Color.GREEN : Color.RED);
            checkBox.setEnabled(false);
            teams.addView(checkBox);
            shippable &= team.shippable;
            if (!team.shippable) {
                appendCrashStackTrace(crash, team.name);
            }
        }
        rollout.setEnabled(shippable);
        stackTrace.setText(crash.toString());
    }

    private void appendCrashStackTrace(StringBuilder sb, String teamName) {
        sb.append(teamName + " has a bug!\n");
    }
}
