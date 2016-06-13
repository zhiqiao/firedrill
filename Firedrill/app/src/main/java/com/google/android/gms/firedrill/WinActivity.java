package com.google.android.gms.firedrill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class WinActivity extends Activity {

    String gameId;

    public static void start(Context context, String gameId) {
        context.startActivity(new Intent(context, WinActivity.class).putExtra("game_id", gameId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        gameId = getIntent().getStringExtra("game_id");
    }
}
