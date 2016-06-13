package com.google.android.gms.firedrill;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shayba on 6/13/16.
 */

public class Config {

    private static final String CODING_SPEED = "coding_speed";

    private static Config sInstance = new Config();
    private final FirebaseRemoteConfig mFirebaseRemoteConfig;

    private Config() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        FirebaseRemoteConfig.getInstance().setConfigSettings(configSettings);

        Map<String, Object> defaults = new HashMap<>();
        defaults.put(CODING_SPEED, 5);
        mFirebaseRemoteConfig.setDefaults(defaults);

        mFirebaseRemoteConfig.fetch()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Once the config is successfully fetched it must be activated before newly fetched
                        // values are returned.
                        mFirebaseRemoteConfig.activateFetched();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });

        sInstance = this;
    }

    public static long getCodingSpeed() {
        return sInstance.mFirebaseRemoteConfig.getLong(CODING_SPEED);
    }
}
