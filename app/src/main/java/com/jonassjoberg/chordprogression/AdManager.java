package com.jonassjoberg.chordprogression;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by Josmomo on 2/10/2018.
 */

public class AdManager {

    private static final String TAG = AdManager.class.getName();

    private static AdRequest mAdRequest;

    public AdManager(Context context) {
        MobileAds.initialize(context.getApplicationContext(), Constants.ADMOB_APP_ID);
    }

    public AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }
}
