package com.jonassjoberg.chordprogression;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Josmomo on 2/8/2018.
 */

public class FactoryTextViewChord {

    private static final String TAG = FactoryTextViewChord.class.getName();

    private Context mContext;
    private Typeface mTypeface;
    private FactoryChord mFactoryChord = new FactoryChord();

    public FactoryTextViewChord(Context context) {
        this.mContext = context;
        this.mTypeface = Typeface.createFromAsset(context.getAssets(), Constants.FONT_PATH);
    }

    public TextView next() {
        TextView retTextView = new TextView(mContext);
        retTextView.setTypeface(mTypeface);
        retTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        retTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50.f);
        retTextView.setText(mFactoryChord.nextChord());
        return retTextView;
    }

    public TextView getStartingChord() {
        TextView retTextView = new TextView(mContext);
        retTextView.setTypeface(mTypeface);
        retTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        retTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50.f);
        retTextView.setText(mFactoryChord.getStartingChord());
        return retTextView;
    }
}
