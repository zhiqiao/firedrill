package com.google.android.gms.firedrill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ReleaseEngActivity extends Activity {

    String gameId;
    int lastRollout = 0;
    final List<ClientTeam> clientTeams = new ArrayList<>();
    final Map<ClientTeam, DatabaseReference> refMAp = new HashMap<>();

    TextView percentage;
    SeekBar rollout;
    TextView timer;
    TextView stackTrace;

    String[] percentages = new String[] {"0%", "0.02%", "0.1%", "1%", "5%", "25%", "50%", "100%"};
    CountDown countdown;

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
        rollout.setEnabled(false);
        rollout.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (lastRollout == percentages.length - 1) {
                    // We rolled out to 100% and won!
                    WinActivity.start(ReleaseEngActivity.this, gameId);
                    return;
                }
                Random gen = new Random();
                int random = gen.nextInt(Math.min(clientTeams.size() + 1, 3));
                System.out.println("*********" + random);
                for (int i = 0; i< random; i++) {

                    int index = gen.nextInt(clientTeams.size());
                    System.out.println("*********" + index);
                    ClientTeam team = clientTeams.get(index);
                    team.shippable = false;
                    refMAp.get(team).setValue(team);
                }
                if (lastRollout == 0) {
                    countdown.start();
                }
                seekBar.setProgress(++lastRollout);
                percentage.setText("Public rollout: " + percentages[lastRollout]);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Typeface tf = Typeface.createFromAsset(getAssets(), "digital-7.ttf");
        timer = (TextView) findViewById(R.id.countdown);
        timer.setTypeface(tf);
        stackTrace = (TextView) findViewById(R.id.stack_trace);
        countdown = new CountDown();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference re = database.getReference(gameId + "/releaseEngineer");
        re.setValue(new ReleaseEngineer());
        DatabaseReference teams = database.getReference(gameId + "/teams");
        teams.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clientTeams.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ClientTeam team = child.getValue(ClientTeam.class);
                    clientTeams.add(team);
                    refMAp.put(team, child.getRef());
                }
                onTeamsChanged(clientTeams);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new AppInviteInvitation.IntentBuilder("[ACTION REQUIRED] Please dial into this meeting ASAP")
                        .setEmailSubject("go/meet/" + gameId)
                        .setDeepLink(Uri.parse("gmscore.google.com/" + gameId))
                        .setCustomImage(Uri.parse("https://lh4.ggpht.com/fX0oncZTwPIETqwHYjYzW2o44N3NqsAB_X16KTJzTDFK4UdcGrtKaMxuVtCX-3Ovzqw=w100"))
                        .setMessage("If you don't care about product excellence you can stop reading now.\nThe GmsCore team needs your help to ensure a quality and timely release.\nPlease dial into the release team meeting ASAP! Thanks.")
                        .build();
                startActivityForResult(intent, 12345);
            }
        });
    }

    public void onTeamsChanged(List<ClientTeam> clientTeams) {
        LinearLayout teams = (LinearLayout) findViewById(R.id.teams);
        teams.removeAllViews();
        boolean shippable = true;
        SeekBar seekBar = (SeekBar) findViewById(R.id.rollout);
        seekBar.setEnabled(!clientTeams.isEmpty());
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
                seekBar.setEnabled(false);
                appendCrashStackTrace(crash, team.name);
            }
        }
        stackTrace.setText(crash.toString());
    }

    String[] messages = new String[] {
            "java.io.IOException: File not found!",
            "java.lang.IllegalStateException: THIS SHOULD NEVER HAPPEN",
            "java.lang.NullPointerException",
            "java.lang.IllegalArgumentException: expecting non-negative number",
            "java.lang.ClassNotFoundException: MainActivity",
            "java.lang.UnsupportedOperationException: TODO implement this",
            "android.database.sqlite.SQLiteDatabaseCorruptException",
    };

    String[] classes = new String[] {
            "Frobnicator",
            "ProxyFactoryImplImpl",
            "MainActivity",
            "AbstractBuilderAdapter",
            "RequestMessageNano",
            "ResponseMessageNano",
            "LogHelper",
            "PersistentService",
            "ClearcutLoggerImpl",
            "SqliteOpenHelper",
            "MysteryBusinessLogic",
            "LastMinuteHack",
            "AbstractThreadSafeLooperWrapper",
            "WorkerThread",
            "MyRunnable",
    };

    String[] methods = new String[] {
            "log(String)",
            "quit()",
            "onDestroy()",
            "checkNotNull(Object)",
            "<init>(Context)",
            "<clinit>()",
            "busyLoop()",
            "busyLoopLocked()",
            "drainBattery(int)",
            "collectAllUserInformation(Context, Account)",
            "corruptDatabase(Context)",
            "leakMemory(byte[])",
            "getGservicesValue(Context, String, Object)",
            "sync(Context)",
            "crash()",
            "hack(boolean)",
            "isUserSignedIn()",
            "isUserOptedIn()",
            "incrementAndGet()",
            "getAndIncrement()",
    };

    private void appendCrashStackTrace(StringBuilder sb, String teamName) {
        Random random = new Random();
        sb.append(messages[random.nextInt(messages.length)] + "\n");
        String at = "    at com.google.android.gms." + teamName.replace(" ", "").toLowerCase();
        for (int i = 0; i < random.nextInt(20); i++) {
            sb.append(at + "." + classes[random.nextInt(classes.length)] + "." + methods[random.nextInt(methods.length)] + "\n");
        }
        sb.append("\n");
    }

    private class CountDown extends CountDownTimer {

        public CountDown() {
            super(1000 * 120, 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long secondsLeft = (millisUntilFinished / 1000) % 60;
            long minutesLeft = (millisUntilFinished / 1000) / 60;
            timer.setText("0" + minutesLeft + ":" + secondsLeft);
        }

        @Override
        public void onFinish() {

        }
    }
}
