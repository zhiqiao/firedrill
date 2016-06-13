package com.google.android.gms.firedrill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {
    boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        final TextView gameId = (TextView) findViewById(R.id.game_id);
        Button join = (Button) findViewById(R.id.join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join(gameId.getText().toString());
            }
        });
    }

    private void join(final String gameId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference(gameId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (initialized) {
                    return;
                }
                if (dataSnapshot.getValue() == null) {
                    // Release engineer.
                    DatabaseReference re = database.getReference(gameId + "/releaseEngineer");
                    re.setValue(new ReleaseEngineer());
                } else {
                    // Software engineer.
                    DatabaseReference swe = ref.push();
                    swe.setValue(new SoftwareEngineer());
                }
                initialized = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
