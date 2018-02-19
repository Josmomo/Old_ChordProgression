package com.jonassjoberg.chordprogression;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josmomo on 2/9/2018.
 */

public class Constants {

    private static final String TAG = Constants.class.getName();

    public static final String ADMOB_APP_ID_TEST = "ca-app-pub-3940256099942544~3347511713";
    public static final String ADMOB_APP_ID= "ca-app-pub-2905699833595071~1909992404";

    public static final boolean DEBUG = false;

    // BPM and timing variables
    public static final int BPM_MAX = 208;
    public static final int BPM_MIN = 40;
    public static final int BPM_DEFAULT = 120;
    public static final int MINUTE_IN_MILLIS = 60000;

    // Font and output variables
    public static final String FONT_PATH = "fonts/JazzChords.ttf";
    public static final String FONT_CHORDS_FLAT = "♭";
    public static final String FONT_CHORDS_SHARP = "♯";
    public static final String FONT_SINGLE_NOTE_FOURTH = "\uECA5";
    public static final String FONT_SINGLE_NOTE_HALF = "\uECA3";
    public static final Map<Integer, String> BASE_NOTES_MAP = new HashMap<Integer, String>() {{
        put(0, "C");
        put(1, "C" + FONT_CHORDS_SHARP);
        put(2, "D");
        put(3, "E" + FONT_CHORDS_FLAT);
        put(4, "E");
        put(5, "F");
        put(6, "F" + FONT_CHORDS_SHARP);
        put(7, "G");
        put(8, "A" + FONT_CHORDS_FLAT);
        put(9, "A");
        put(10, "B" + FONT_CHORDS_FLAT);
        put(11, "B");
    }};
    public static final Map<Integer, Integer> FIFTH_NOTES_MAP = new HashMap<Integer, Integer>() {{
        put(0, 0);
        put(1, 7);
        put(2, 2);
        put(3, 9);
        put(4, 4);
        put(5, 11);
        put(6, 6);
        put(7, 1);
        put(8, 8);
        put(9, 3);
        put(10, 10);
        put(11, 5);
    }};
    public static final Map<String, String> CHORD_STRING_MAP = new HashMap<String, String>() {{
        put("M", "");
        put("m", "m");
        put("sus", "\uE185");
        put("7", "\uE197");
        put("m7", "m\uE197");
        put("maj7", "\uE18A\uE197");
        put("2", "\uE192");
        put("6", "\uE196");
        put("m6", "m\uE196");
        put("dim7", "\uE184\uE197");
        put("9", "\uE199");
        put("m9", "m\uE199");
        put("maj9", "\uE18A\uE199");
        put("11", "\uE182");
        put("13", "\uE183");
        put(" b9", "\uE188\uE199");
        put("m7b5", "m\uE197\uE188\uE195");
        put("/1", "/1");
        put("/2", "/2");
        put("/3", "/3");
        put("/5", "/5");
        put("dim/b3", "\uE184/");
    }};

    public static final Map<Integer, String> CHORD_TYPES_MAP = new HashMap<Integer, String>() {{
        put(0, "M");
        put(1, "m");
        put(2, "sus");
        put(3, "7");
        put(4, "m7");
        put(5, "maj7");
        put(6, "2");
        put(7, "6");
        put(8, "m6");
        put(9, "dim7");
        put(10, "9");
        put(11, "m9");
        put(12, "maj9");
        put(13, "11");
        put(14, "13");
        put(15, " b9");
        put(16, "m7b5");
        put(17, "/1");
        put(18, "/2");
        put(19, "/3");
        put(20, "/5");
        put(21, "dim/b3");
    }};

    // Chord generation variables
    public static final int NOTE_C = 0;
    public static final int NOTE_B_SHARP = 0;
    public static final int NOTE_C_SHARP = 1;
    public static final int NOTE_D_FLAT = 1;
    public static final int NOTE_D = 2;
    public static final int NOTE_D_SHARP = 3;
    public static final int NOTE_E_FLAT = 3;
    public static final int NOTE_E = 4;
    public static final int NOTE_F_FLAT = 4;
    public static final int NOTE_F = 5;
    public static final int NOTE_E_SHARP = 5;
    public static final int NOTE_F_SHARP = 6;
    public static final int NOTE_G_FLAT = 6;
    public static final int NOTE_G = 7;
    public static final int NOTE_G_SHARP = 8;
    public static final int NOTE_A_FLAT = 8;
    public static final int NOTE_A = 9;
    public static final int NOTE_A_SHARP = 10;
    public static final int NOTE_B_FLAT = 10;
    public static final int NOTE_B = 11;
    public static final int NOTE_C_FLAT = 11;

    public static final int CHORD_TYPE_MAJOR = 0;
    public static final int CHORD_TYPE_MINOR = 1;
    public static final int CHORD_TYPE_SUS = 2;
    public static final int CHORD_TYPE_7 = 3;
    public static final int CHORD_TYPE_MINOR_7 = 4;
    public static final int CHORD_TYPE_MAJ_7 = 5;
    public static final int CHORD_TYPE_2 = 6;
    public static final int CHORD_TYPE_6 = 7;
    public static final int CHORD_TYPE_MINOR_6 = 8;
    public static final int CHORD_TYPE_DIM_7 = 9;
    public static final int CHORD_TYPE_9 = 10;
    public static final int CHORD_TYPE_MINOR_9 = 11;
    public static final int CHORD_TYPE_MAJ_9 = 12;
    public static final int CHORD_TYPE_11 = 13;
    public static final int CHORD_TYPE_13 = 14;
    public static final int CHORD_TYPE_b9 = 15;
    public static final int CHORD_TYPE_MINOR_7_b5 = 16;
    public static final int CHORD_TYPE_SLASH_1 = 17;
    public static final int CHORD_TYPE_SLASH_2 = 18;
    public static final int CHORD_TYPE_SLASH_3 = 19;
    public static final int CHORD_TYPE_SLASH_5 = 20;
    public static final int CHORD_TYPE_DIM_SLASH_b3 = 21;

    public static boolean ENABLE_METRONOME = true;

    public static boolean ENABLE_CHORD_TYPE_MAJOR = true;
    public static boolean ENABLE_CHORD_TYPE_MINOR = true;
    public static boolean ENABLE_CHORD_TYPE_SUS = true;
    public static boolean ENABLE_CHORD_TYPE_7 = true;
    public static boolean ENABLE_CHORD_TYPE_MINOR_7 = true;
    public static boolean ENABLE_CHORD_TYPE_MAJ_7 = true;
    public static boolean ENABLE_CHORD_TYPE_2 = true;
    public static boolean ENABLE_CHORD_TYPE_6 = true;
    public static boolean ENABLE_CHORD_TYPE_MINOR_6 = true;
    public static boolean ENABLE_CHORD_TYPE_DIM_7 =true;
    public static boolean ENABLE_CHORD_TYPE_9 = true;
    public static boolean ENABLE_CHORD_TYPE_MINOR_9 = true;
    public static boolean ENABLE_CHORD_TYPE_MAJ_9 = true;
    public static boolean ENABLE_CHORD_TYPE_11 = true;
    public static boolean ENABLE_CHORD_TYPE_13 = true;
    public static boolean ENABLE_CHORD_TYPE_b9 = true;
    public static boolean ENABLE_CHORD_TYPE_MINOR_7_b5 = true;
    public static boolean ENABLE_CHORD_TYPE_SLASH_1 = true;
    public static boolean ENABLE_CHORD_TYPE_SLASH_2 = true;
    public static boolean ENABLE_CHORD_TYPE_SLASH_3 = true;
    public static boolean ENABLE_CHORD_TYPE_SLASH_5 = true;
    public static boolean ENABLE_CHORD_TYPE_DIM_SLASH_b3 = true;

    public static final int NUMBER_OF_CHORD_TYPES = 22;
    public static final int NUMBER_OF_NOTES = 12;
    public static final int N = NUMBER_OF_NOTES * NUMBER_OF_CHORD_TYPES + 1;
    public static int PROGRESSION_TIME_SIGNATURE = 4;
    public static int PROGRESSION_KEY_NOTE = NOTE_C;
    public static int PROGRESSION_KEY_CHORD_TYPE = CHORD_TYPE_MINOR;
    public static final int PULL_MULTIPLIER = 5;
    public static final int BLUE_MULTIPLIER = 1;
    public static final int GREEN_MULTIPLIER = 1;
    public static final int FIFTH_ROTATION = 7;
}
