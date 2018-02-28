package com.jonassjoberg.chordprogression;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.ads.AdView;

public class SetupActivity extends AppCompatActivity {

    private Typeface mTypeface;

    private AdView mAdView;

    private Spinner mSpinnerKeyNote;
    private Spinner mSpinnerKeyChordType;
    private Spinner mSpinnerTimeSignature;
    private Button mButtonSimple;
    private Button mButtonAll;
    private Button mButtonStart;

    private Switch mSwitchMajor;
    private Switch mSwitchMinor;
    private Switch mSwitchSus;
    private Switch mSwitch7;
    private Switch mSwitchMinor7;
    private Switch mSwitchMaj7;
    private Switch mSwitch2;
    private Switch mSwitch6;
    private Switch mSwitchMinor6;
    private Switch mSwitchDim7;
    private Switch mSwitch9;
    private Switch mSwitchMinor9;
    private Switch mSwitchMaj9;
    private Switch mSwitch11;
    private Switch mSwitch13;
    private Switch mSwitchb9;
    private Switch mSwitchMinor7b5;
    private Switch mSwitchSlash1;
    private Switch mSwitchSlash2;
    private Switch mSwitchSlash3;
    private Switch mSwitchSlash5;
    private Switch mSwitchDimSlashb3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mTypeface = Typeface.createFromAsset(getApplicationContext().getAssets(), Constants.FONT_PATH);

        final AdManager mAdManager = new AdManager(this);
        mAdView = findViewById(R.id.setupActivityAdView);
        mSpinnerKeyNote = findViewById(R.id.setupActivitySpinnerKeyNote);
        mSpinnerKeyChordType = findViewById(R.id.setupActivitySpinnerKeyChordType);
        mSpinnerTimeSignature = findViewById(R.id.setupActivitySpinnerTimeSignature);
        mButtonSimple = findViewById(R.id.setupActivityButtonSimple);
        mButtonAll = findViewById(R.id.setupActivityButtonAll);
        mButtonStart = findViewById(R.id.setupActivityButtonStart);
        mSwitchMajor = findViewById(R.id.setupActivitySwitchMajor);
        mSwitchMinor = findViewById(R.id.setupActivitySwitchMinor);
        mSwitchSus = findViewById(R.id.setupActivitySwitchSus);
        mSwitch7 = findViewById(R.id.setupActivitySwitch7);
        mSwitchMinor7 = findViewById(R.id.setupActivitySwitchMinor7);
        mSwitchMaj7 = findViewById(R.id.setupActivitySwitchMaj7);
        mSwitch2 = findViewById(R.id.setupActivitySwitch2);
        mSwitch6 = findViewById(R.id.setupActivitySwitch6);
        mSwitchMinor6 = findViewById(R.id.setupActivitySwitchMinor6);
        mSwitchDim7 = findViewById(R.id.setupActivitySwitchDim7);
        mSwitch9 = findViewById(R.id.setupActivitySwitch9);
        mSwitchMinor9 = findViewById(R.id.setupActivitySwitchMinor9);
        mSwitchMaj9 = findViewById(R.id.setupActivitySwitchMaj9);
        mSwitch11 = findViewById(R.id.setupActivitySwitch11);
        mSwitch13 = findViewById(R.id.setupActivitySwitch13);
        mSwitchb9 = findViewById(R.id.setupActivitySwitchb9);
        mSwitchMinor7b5 = findViewById(R.id.setupActivitySwitchMinor7b5);
        mSwitchSlash1 = findViewById(R.id.setupActivitySwitchSlash1);
        mSwitchSlash2 = findViewById(R.id.setupActivitySwitchSlash2);
        mSwitchSlash3 = findViewById(R.id.setupActivitySwitchSlash3);
        mSwitchSlash5 = findViewById(R.id.setupActivitySwitchSlash5);
        mSwitchDimSlashb3 = findViewById(R.id.setupActivitySwitchDimSlashb3);

        mSwitchMajor.setTypeface(mTypeface);
        mSwitchMinor.setTypeface(mTypeface);
        mSwitchSus.setTypeface(mTypeface);
        mSwitch7.setTypeface(mTypeface);
        mSwitchMinor7.setTypeface(mTypeface);
        mSwitchMaj7.setTypeface(mTypeface);
        mSwitch2.setTypeface(mTypeface);
        mSwitch6.setTypeface(mTypeface);
        mSwitchMinor6.setTypeface(mTypeface);
        mSwitchDim7.setTypeface(mTypeface);
        mSwitch9.setTypeface(mTypeface);
        mSwitchMinor9.setTypeface(mTypeface);
        mSwitchMaj9.setTypeface(mTypeface);
        mSwitch11.setTypeface(mTypeface);
        mSwitch13.setTypeface(mTypeface);
        mSwitchb9.setTypeface(mTypeface);
        mSwitchMinor7b5.setTypeface(mTypeface);
        mSwitchSlash1.setTypeface(mTypeface);
        mSwitchSlash2.setTypeface(mTypeface);
        mSwitchSlash3.setTypeface(mTypeface);
        mSwitchSlash5.setTypeface(mTypeface);
        mSwitchDimSlashb3.setTypeface(mTypeface);

        mAdView.loadAd(mAdManager.getAdRequest());
        ArrayAdapter<CharSequence> adapterNote = ArrayAdapter.createFromResource(this, R.array.key_notes_array, R.layout.custom_spinner_item);
        adapterNote.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        mSpinnerKeyNote.setAdapter(adapterNote);
        mSpinnerKeyNote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Constants.PROGRESSION_KEY_NOTE = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<CharSequence> adapterChordType = ArrayAdapter.createFromResource(this, R.array.key_chord_type_array, R.layout.custom_spinner_item);
        adapterChordType.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        mSpinnerKeyChordType.setAdapter(adapterChordType);
        mSpinnerKeyChordType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Constants.PROGRESSION_KEY_CHORD_TYPE = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<CharSequence> adapterTimeSignature = ArrayAdapter.createFromResource(this, R.array.time_signature_array, R.layout.custom_spinner_item);
        adapterTimeSignature.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        mSpinnerTimeSignature.setAdapter(adapterTimeSignature);
        mSpinnerTimeSignature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Constants.PROGRESSION_TIME_SIGNATURE = i + 2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mSpinnerTimeSignature.setSelection(2);
        mButtonSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Switch on Major, Minor and 7
                mSwitchMajor.setChecked(true);
                mSwitchMinor.setChecked(true);
                mSwitchSus.setChecked(false);
                mSwitch7.setChecked(true);
                mSwitchMinor7.setChecked(false);
                mSwitchMaj7.setChecked(false);
                mSwitch2.setChecked(false);
                mSwitch6.setChecked(false);
                mSwitchMinor6.setChecked(false);
                mSwitchDim7.setChecked(false);
                mSwitch9.setChecked(false);
                mSwitchMinor9.setChecked(false);
                mSwitchMaj9.setChecked(false);
                mSwitch11.setChecked(false);
                mSwitch13.setChecked(false);
                mSwitchb9.setChecked(false);
                mSwitchMinor7b5.setChecked(false);
                mSwitchSlash1.setChecked(false);
                mSwitchSlash2.setChecked(false);
                mSwitchSlash3.setChecked(false);
                mSwitchSlash5.setChecked(false);
                mSwitchDimSlashb3.setChecked(false);
            }
        });
        mButtonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Switch on all options
                mSwitchMajor.setChecked(true);
                mSwitchMinor.setChecked(true);
                mSwitchSus.setChecked(true);
                mSwitch7.setChecked(true);
                mSwitchMinor7.setChecked(true);
                mSwitchMaj7.setChecked(true);
                mSwitch2.setChecked(true);
                mSwitch6.setChecked(true);
                mSwitchMinor6.setChecked(true);
                mSwitchDim7.setChecked(true);
                mSwitch9.setChecked(true);
                mSwitchMinor9.setChecked(true);
                mSwitchMaj9.setChecked(true);
                mSwitch11.setChecked(true);
                mSwitch13.setChecked(true);
                mSwitchb9.setChecked(true);
                mSwitchMinor7b5.setChecked(true);
                mSwitchSlash1.setChecked(true);
                mSwitchSlash2.setChecked(true);
                mSwitchSlash3.setChecked(true);
                mSwitchSlash5.setChecked(true);
                mSwitchDimSlashb3.setChecked(true);
            }
        });
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.ENABLE_CHORD_TYPE_MAJOR = mSwitchMajor.isChecked();
                Constants.ENABLE_CHORD_TYPE_MINOR = mSwitchMinor.isChecked();
                Constants.ENABLE_CHORD_TYPE_SUS = mSwitchSus.isChecked();
                Constants.ENABLE_CHORD_TYPE_7 = mSwitch7.isChecked();
                Constants.ENABLE_CHORD_TYPE_MINOR_7 = mSwitchMinor7.isChecked();
                Constants.ENABLE_CHORD_TYPE_MAJ_7 = mSwitchMaj7.isChecked();
                Constants.ENABLE_CHORD_TYPE_2 = mSwitch2.isChecked();
                Constants.ENABLE_CHORD_TYPE_6 = mSwitch6.isChecked();
                Constants.ENABLE_CHORD_TYPE_MINOR_6 = mSwitchMinor6.isChecked();
                Constants.ENABLE_CHORD_TYPE_DIM_7 = mSwitchDim7.isChecked();
                Constants.ENABLE_CHORD_TYPE_9 = mSwitch9.isChecked();
                Constants.ENABLE_CHORD_TYPE_MINOR_9 = mSwitchMinor9.isChecked();
                Constants.ENABLE_CHORD_TYPE_MAJ_9 = mSwitchMaj9.isChecked();
                Constants.ENABLE_CHORD_TYPE_11 = mSwitch11.isChecked();
                Constants.ENABLE_CHORD_TYPE_13 = mSwitch13.isChecked();
                Constants.ENABLE_CHORD_TYPE_b9 = mSwitchb9.isChecked();
                Constants.ENABLE_CHORD_TYPE_MINOR_7_b5 = mSwitchMinor7b5.isChecked();
                Constants.ENABLE_CHORD_TYPE_SLASH_1 = mSwitchSlash1.isChecked();
                Constants.ENABLE_CHORD_TYPE_SLASH_2 = mSwitchSlash2.isChecked();
                Constants.ENABLE_CHORD_TYPE_SLASH_3 = mSwitchSlash3.isChecked();
                Constants.ENABLE_CHORD_TYPE_SLASH_5 = mSwitchSlash5.isChecked();
                Constants.ENABLE_CHORD_TYPE_DIM_SLASH_b3 = mSwitchDimSlashb3.isChecked();

                Intent intent = new Intent(view.getContext(), EndlessModeActivity.class);
                startActivity(intent);
            }
        });
    }
}
