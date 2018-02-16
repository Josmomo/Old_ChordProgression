package com.jonassjoberg.chordprogression;

import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.LinkedList;

public class EndlessModeActivity extends AppCompatActivity {

    private static final String TAG = EndlessModeActivity.class.getName();

    private View mContentView;
    private View mLayoutOffBeat;
    private View mLayoutOnBeat;
    private SeekBar mSeekBarBpm;
    private TextView mTextViewBpm;
    private ConstraintLayout mConstraintLayout;
    private ConstraintSet mApplyConstraintSet = new ConstraintSet();
    private FactoryTextViewChord factoryTextViewChord;
    private LinkedList<TextView> mLinkedListTextViewChords = new LinkedList<TextView>();

    private int mBpm = Constants.BPM_DEFAULT;
    private int mPeriodInMillis = Constants.MINUTE_IN_MILLIS / mBpm;
    private int mTimeType = Constants.TIME_4_4;
    private int mTimeCount = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_endless_mode);
        getSupportActionBar().hide();

        mContentView = findViewById(R.id.endlessModeActivityFullscreenContent);
        mLayoutOffBeat = findViewById(R.id.endlessModeActivityConstraintLayoutOffBeat);
        mLayoutOnBeat = findViewById(R.id.endlessModeActivityConstraintLayoutOnBeat);
        mSeekBarBpm = findViewById(R.id.endlessModeActivitySeekBarBpm);
        mTextViewBpm = findViewById(R.id.endlessModeActivityTextViewBpm);
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.endlessModeActivityFullscreenContent);

        mSeekBarBpm.setMax(Constants.BPM_MAX - Constants.BPM_MIN);
        mSeekBarBpm.setProgress(mBpm - Constants.BPM_MIN);
        mSeekBarBpm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (Constants.DEBUG) {
                    Log.d(TAG, "mSeekBarBpm onProgressChanged()");
                }
                // Update mBpm and adjust the tick speed accordingly
                mBpm = Constants.BPM_MIN + i;
                mPeriodInMillis = Constants.MINUTE_IN_MILLIS / mBpm;
                mTextViewBpm.setText("Bpm: " + Integer.toString(mBpm));

                // Cancel currently running tick and start a new
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, mPeriodInMillis);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mApplyConstraintSet.clone(mConstraintLayout);
        factoryTextViewChord = new FactoryTextViewChord(getApplicationContext());

        // Manually setup the starting chord
        TextView textViewTmp1 = new TextView(this);
        TextView textViewTmp2 = new TextView(this);
        TextView textViewTmp3 = new TextView(this);
        TextView textViewTmp4 = factoryTextViewChord.getStartingChord();
        mLinkedListTextViewChords.add(textViewTmp1);
        mLinkedListTextViewChords.add(textViewTmp2);
        mLinkedListTextViewChords.add(textViewTmp3);
        mLinkedListTextViewChords.add(textViewTmp4);
        mConstraintLayout.addView(textViewTmp1);
        mConstraintLayout.addView(textViewTmp2);
        mConstraintLayout.addView(textViewTmp3);
        mConstraintLayout.addView(textViewTmp4);

        mLinkedListTextViewChords.get(0).setId(R.id.endlessModeActivityTextViewLeft);
        mLinkedListTextViewChords.get(1).setId(R.id.endlessModeActivityTextViewCenter);
        mLinkedListTextViewChords.get(2).setId(R.id.endlessModeActivityTextViewRight);
        mLinkedListTextViewChords.get(3).setId(R.id.endlessModeActivityTextViewNew);
        applyConstraintsToView();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, mPeriodInMillis * mTimeType);
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            // Switch to next chord if a bar has passed
            if (mTimeCount == mTimeType - 1) {

                Transition transition = new AutoTransition();
                transition.setDuration(Constants.MINUTE_IN_MILLIS /(mBpm * Constants.TIME_4_4));
                transition.setInterpolator(new AccelerateDecelerateInterpolator());

                TransitionManager.beginDelayedTransition(mConstraintLayout, transition);
                mConstraintLayout.removeView(mLinkedListTextViewChords.remove());

                while (mLinkedListTextViewChords.size() < 4) {
                    TextView textViewNewChord = factoryTextViewChord.next();
                    mLinkedListTextViewChords.add(textViewNewChord);
                    mConstraintLayout.addView(textViewNewChord);
                }

                mLinkedListTextViewChords.get(0).setId(R.id.endlessModeActivityTextViewLeft);
                mLinkedListTextViewChords.get(1).setId(R.id.endlessModeActivityTextViewCenter);
                mLinkedListTextViewChords.get(2).setId(R.id.endlessModeActivityTextViewRight);
                mLinkedListTextViewChords.get(3).setId(R.id.endlessModeActivityTextViewNew);

                applyConstraintsToView();
            }

            // Call the view to create ripple effect as beat
            // Invalidate in order to perform the animation
            if (mTimeCount == 0) {
                mLayoutOnBeat.performClick();
                mLayoutOnBeat.setPressed(true);
                mLayoutOnBeat.invalidate();
                mLayoutOnBeat.setPressed(false);
                mLayoutOnBeat.invalidate();
            } else {
                mLayoutOffBeat.performClick();
                mLayoutOffBeat.setPressed(true);
                mLayoutOffBeat.invalidate();
                mLayoutOffBeat.setPressed(false);
                mLayoutOffBeat.invalidate();
            }

            // Schedule the next beat
            mTimeCount++;
            if (mTimeCount >= mTimeType) {
                mTimeCount = 0;
            }
            handler.postDelayed(this, mPeriodInMillis);
        }
    };

    private void applyConstraintsToView() {
        // Clear
        mApplyConstraintSet.clear(R.id.endlessModeActivityTextViewLeft);
        mApplyConstraintSet.clear(R.id.endlessModeActivityTextViewCenter);
        mApplyConstraintSet.clear(R.id.endlessModeActivityTextViewRight);
        mApplyConstraintSet.clear(R.id.endlessModeActivityTextViewNew);

        // Remove left-most chord
        mApplyConstraintSet.setVisibility(R.id.endlessModeActivityTextViewLeft, ConstraintSet.GONE);

        // Wall <-- Center --> Right
        mApplyConstraintSet.constrainWidth(R.id.endlessModeActivityTextViewCenter, ConstraintSet.WRAP_CONTENT);
        mApplyConstraintSet.constrainHeight(R.id.endlessModeActivityTextViewCenter, ConstraintSet.WRAP_CONTENT);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewCenter, ConstraintSet.TOP, mConstraintLayout.getId(), ConstraintSet.TOP);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewCenter, ConstraintSet.BOTTOM, mConstraintLayout.getId(), ConstraintSet.BOTTOM);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewCenter, ConstraintSet.LEFT, mConstraintLayout.getId(), ConstraintSet.LEFT);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewCenter, ConstraintSet.RIGHT, R.id.endlessModeActivityTextViewRight, ConstraintSet.LEFT);
        //mApplyConstraintSet.setVisibility(mLinkedListTextViewChords.get(1).getId(), ConstraintSet.VISIBLE);

        // Center <-- Right --> New
        mApplyConstraintSet.constrainWidth(R.id.endlessModeActivityTextViewRight, ConstraintSet.WRAP_CONTENT);
        mApplyConstraintSet.constrainHeight(R.id.endlessModeActivityTextViewRight, ConstraintSet.WRAP_CONTENT);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewRight, ConstraintSet.TOP, mConstraintLayout.getId(), ConstraintSet.TOP);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewRight, ConstraintSet.BOTTOM, mConstraintLayout.getId(), ConstraintSet.BOTTOM);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewRight, ConstraintSet.LEFT, R.id.endlessModeActivityTextViewCenter, ConstraintSet.RIGHT);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewRight, ConstraintSet.RIGHT, mLinkedListTextViewChords.get(3).getId(), ConstraintSet.LEFT);
        mApplyConstraintSet.setVisibility(R.id.endlessModeActivityTextViewRight, ConstraintSet.VISIBLE);

        // Right <-- New --> Wall
        mApplyConstraintSet.constrainWidth(R.id.endlessModeActivityTextViewNew, ConstraintSet.WRAP_CONTENT);
        mApplyConstraintSet.constrainHeight(R.id.endlessModeActivityTextViewNew, ConstraintSet.WRAP_CONTENT);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewNew, ConstraintSet.TOP, mConstraintLayout.getId(), ConstraintSet.TOP);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewNew, ConstraintSet.BOTTOM, mConstraintLayout.getId(), ConstraintSet.BOTTOM);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewNew, ConstraintSet.LEFT, R.id.endlessModeActivityTextViewRight, ConstraintSet.RIGHT);
        mApplyConstraintSet.connect(R.id.endlessModeActivityTextViewNew, ConstraintSet.RIGHT, mConstraintLayout.getId(), ConstraintSet.RIGHT);
        mApplyConstraintSet.setVisibility(R.id.endlessModeActivityTextViewNew, ConstraintSet.VISIBLE);

        // Center the new chain
        mApplyConstraintSet.centerHorizontally(R.id.endlessModeActivityTextViewRight, mConstraintLayout.getId());
        mApplyConstraintSet.setHorizontalChainStyle(mConstraintLayout.getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);

        // Apply everything
        mApplyConstraintSet.applyTo(mConstraintLayout);
    }
}
