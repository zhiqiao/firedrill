package com.google.android.gms.firedrill;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * Created by shayba on 6/13/16.
 */

public class ClientTeam {
    public String name;
    public boolean shippable;
    public int codingPercentage;

    public ClientTeam() {
        this.name = "";
        this.shippable = true;
        this.codingPercentage = 0;
    }

    public ClientTeam(DatabaseReference self) {
    }

    public ClientTeam(String name, boolean shippable) {
        this.name = name;
        this.shippable = shippable;
        this.codingPercentage = 0;
    }

    @Exclude
    public void code() {
        codingPercentage += Config.getCodingSpeed();
        if (codingPercentage >= 100) {
            shippable = true;
        }
    }
}
