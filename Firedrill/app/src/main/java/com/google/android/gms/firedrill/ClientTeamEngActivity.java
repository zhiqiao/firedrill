package com.google.android.gms.firedrill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClientTeamEngActivity extends Activity {

    DatabaseReference teamRef;
    ClientTeam team;

    String gameId;

    public static void start(Context context, String gameId) {
        context.startActivity(new Intent(context, ClientTeamEngActivity.class).putExtra("game_id", gameId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_team_eng);
        gameId = getIntent().getStringExtra("game_id");

        // Software engineer.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(gameId + "/teams");
        teamRef = ref.push();
        team = new ClientTeam();
        teamRef.setValue(team);

        ((EditText) findViewById(R.id.team_name)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTeamNameChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code();
            }
        });
    }

    private void onTeamNameChanged(String name) {
        team.name = name;
        teamRef.setValue(team);
    }

    private void code() {
        team.code();
        teamRef.setValue(team);
    }
}
