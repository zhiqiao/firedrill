package com.google.android.gms.firedrill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoseActivity extends Activity {

    String gameId;

    public static void start(Context context, String gameId) {
        context.startActivity(new Intent(context, LoseActivity.class).putExtra("game_id", gameId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);
        gameId = getIntent().getStringExtra("game_id");
        DatabaseReference re = FirebaseDatabase.getInstance().getReference(gameId);
        re.removeValue();
        Bundle bundle = new Bundle();
        bundle.putLong(FirebaseAnalytics.Param.SCORE, 0);
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle);
    }
}
