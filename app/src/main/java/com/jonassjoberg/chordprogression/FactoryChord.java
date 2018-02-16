package com.jonassjoberg.chordprogression;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Josmomo on 2/12/2018.
 */

public class FactoryChord {

    private static final String TAG = FactoryChord.class.getName();

    private int modulo(int x, int n) {
        int r = x % n;
        if (r < 0) {
            r += n;
        }
        return r;
    }

    private class Chord {

        private int mNote = 0;
        private int mChordType = 0;
        private int mKeyNote = 0;
        private int mKeyChordType = 0;

        Chord(int note, int chordType, int keyNote, int keyChordType) {
            this.mNote = note;
            this.mChordType = chordType;
            this.mKeyNote = keyNote;
            this.mKeyChordType = keyChordType;

            if (this.mKeyChordType == Constants.CHORD_TYPE_MINOR) {
                this.mKeyNote = modulo(this.mKeyNote - 3 * Constants.FIFTH_ROTATION, Constants.NUMBER_OF_NOTES);
            } else {
                this.mKeyNote = modulo(this.mKeyNote, Constants.NUMBER_OF_NOTES);
            }
        }

        public String toString() {
            String base = Constants.BASE_NOTES_MAP.get(modulo((mNote + Constants.FIFTH_NOTES_MAP.get(mKeyNote) * Constants.FIFTH_ROTATION), Constants.NUMBER_OF_NOTES));
            String extension = Constants.CHORD_STRING_MAP.get(Constants.CHORD_TYPES_MAP.get(mChordType));
            if (mChordType == Constants.CHORD_TYPE_SLASH_1) {
                extension = "/" + Constants.BASE_NOTES_MAP.get(modulo(Constants.PROGRESSION_KEY_NOTE + Constants.FIFTH_NOTES_MAP.get(mKeyNote) * Constants.FIFTH_ROTATION, Constants.NUMBER_OF_NOTES));
            }
            if (mChordType == Constants.CHORD_TYPE_SLASH_2) {
                extension = "/" + Constants.BASE_NOTES_MAP.get(modulo(Constants.PROGRESSION_KEY_NOTE + Constants.NOTE_D + Constants.FIFTH_NOTES_MAP.get(mKeyNote) * Constants.FIFTH_ROTATION, Constants.NUMBER_OF_NOTES));
            }
            if (mChordType == Constants.CHORD_TYPE_SLASH_3) {
                extension = "/" + Constants.BASE_NOTES_MAP.get(modulo(Constants.PROGRESSION_KEY_NOTE + Constants.NOTE_E + Constants.FIFTH_NOTES_MAP.get(mKeyNote) * Constants.FIFTH_ROTATION, Constants.NUMBER_OF_NOTES));
            }
            if (mChordType == Constants.CHORD_TYPE_SLASH_5) {
                extension = "/" + Constants.BASE_NOTES_MAP.get(modulo(Constants.PROGRESSION_KEY_NOTE + Constants.NOTE_G + Constants.FIFTH_NOTES_MAP.get(mKeyNote) * Constants.FIFTH_ROTATION, Constants.NUMBER_OF_NOTES));
            }
            if (mChordType == Constants.CHORD_TYPE_DIM_SLASH_b3) {
                extension = Constants.CHORD_STRING_MAP.get(Constants.CHORD_TYPES_MAP.get(mChordType)) + Constants.BASE_NOTES_MAP.get(modulo(Constants.PROGRESSION_KEY_NOTE + Constants.NOTE_E_FLAT + Constants.FIFTH_NOTES_MAP.get(mKeyNote) * Constants.FIFTH_ROTATION, Constants.NUMBER_OF_NOTES));
            }
            return base + extension;
        }
    }

    private Chord[] mChordMatrix = new Chord[Constants.N];
    private double[][] mInitialStateProbabilityDistribution = new double[1][Constants.N];
    private double[][] mTransitionProbabilityMatrix = new double[Constants.N][Constants.N];

    private double[][] multiplyMatrix(double[][] A, double[][] B) {
        double[][] ret = new double[A.length][B[0].length];
        for (int r = 0; r < A.length; ++r) {
            for (int c = 0; c < B[0].length; ++c) {
                for (int n = 0; n < A[0].length; ++n) {
                    ret[r][c] += A[r][n] * B[n][c];
                }
            }
        }
        return ret;
    }

    private double[] divisionVector(double[] vector, double div) {
        double[] ret = new double[vector.length];
        for (int i=0; i<vector.length; i++) {
            ret[i] = vector[i] / div;
        }
        return ret;
    }

    private double sumVector(double[] vector) {
        double sum = 0;
        for (int i=0; i<vector.length; i++) {
            sum += vector[i];
        }
        return sum;
    }

    private int chooseRandom(double[] weights) {
        Random random = new Random();
        double r = random.nextDouble();
        int index = 0;
        double sum = 0;
        while (index < weights.length) {
            sum += weights[index];
            if (sum > r) {
                break;
            }
            index++;
        }
        return index;
    }

    private void printMatrix(double [][] M) {
        StringBuilder sb = new StringBuilder();
        for (int r=0; r<M.length; r++) {
            for (int c=0; c<M[0].length; c++) {
                sb.append(" " + M[r][c]);
            }
            System.out.println(sb.toString());
            sb = new StringBuilder();
        }
        System.out.println('\n');
    }

    public FactoryChord() {
        populateChordMatrix();
        populateInitialStateVector(Constants.PROGRESSION_KEY_NOTE, Constants.PROGRESSION_KEY_CHORD_TYPE);
        populateTransitionProbabilityMatrix();
    }

    public String nextChord() {
        double[][] result = multiplyMatrix(mInitialStateProbabilityDistribution, mTransitionProbabilityMatrix);
        int chord = chooseRandom(result[0]);
        populateInitialStateVector(mChordMatrix[chord].mNote, mChordMatrix[chord].mChordType);
        return mChordMatrix[chord].toString();
    }

    public String getStartingChord() {
        if (Constants.PROGRESSION_KEY_CHORD_TYPE == Constants.CHORD_TYPE_MINOR) {
            return mChordMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR].toString();
        }
        return mChordMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR].toString();
    }

    private void populateChordMatrix() {
        // Init with every chord combination
        for (int note=0; note<Constants.NUMBER_OF_NOTES; note++) {
            for (int chordType=0; chordType<Constants.CHORD_TYPES_MAP.size(); chordType++) {
                mChordMatrix[note * Constants.NUMBER_OF_CHORD_TYPES + chordType] = new Chord(note, chordType, Constants.PROGRESSION_KEY_NOTE, Constants.PROGRESSION_KEY_CHORD_TYPE);
            }
        }

        // Last place reserved for default chord
        if (Constants.PROGRESSION_KEY_CHORD_TYPE == Constants.CHORD_TYPE_MINOR) {
            mChordMatrix[Constants.N - 1] = new Chord(Constants.NOTE_A, Constants.CHORD_TYPE_MINOR, Constants.PROGRESSION_KEY_NOTE, Constants.PROGRESSION_KEY_CHORD_TYPE);
        }
        mChordMatrix[Constants.N - 1] = new Chord(Constants.NOTE_C, Constants.CHORD_TYPE_MAJOR, Constants.PROGRESSION_KEY_NOTE, Constants.PROGRESSION_KEY_CHORD_TYPE);
    }

    private void populateInitialStateVector(int startingNote, int startingChordType) {
        for (int i = 0; i< mInitialStateProbabilityDistribution[0].length; i++) {
            mInitialStateProbabilityDistribution[0][i] = 0;
        }

        // If the progression have found itself in a dead end, start over from the progression mKeyNote
        if ((startingNote * Constants.NUMBER_OF_CHORD_TYPES + startingChordType) < mInitialStateProbabilityDistribution[0].length - 1) {
            mInitialStateProbabilityDistribution[0][startingNote * Constants.NUMBER_OF_CHORD_TYPES + startingChordType] += 1;
        } else {
            mInitialStateProbabilityDistribution[0][Constants.PROGRESSION_KEY_NOTE * Constants.NUMBER_OF_CHORD_TYPES + Constants.PROGRESSION_KEY_CHORD_TYPE] += 1;
        }
        //printMatrix(mInitialStateProbabilityDistribution);
    }

    private void populateTransitionProbabilityMatrix() {

        for (int i = 0; i< mTransitionProbabilityMatrix.length; i++) {
            for (int j = 0; j< mTransitionProbabilityMatrix[0].length; j++) {
                mTransitionProbabilityMatrix[i][j] = 0;
            }
        }

        // I_2_6_M7_M9_sus
        ArrayList<Integer> listAllowedChords = new ArrayList<Integer>();
        ArrayList<Integer> listBlue = new ArrayList<Integer>();
        ArrayList<Integer> listGreen = new ArrayList<Integer>();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            // Blue
            listBlue.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listBlue.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listBlue.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            // Green
            listGreen.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listGreen.add(Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listGreen.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listGreen.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listGreen.add(Constants.NOTE_G_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listGreen.add(Constants.NOTE_A_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listGreen.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }

        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            // Blue
            listBlue.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
            listBlue.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
            listBlue.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
            listBlue.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
            // Green
            listGreen.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            // Blue
            listBlue.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
            listBlue.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            // Blue
            listBlue.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
            // Green
            listGreen.add(Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
            listGreen.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
            listGreen.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
            listGreen.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
            listGreen.add(Constants.NOTE_G_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
            listGreen.add(Constants.NOTE_C_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            // Blue
            listBlue.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
            listBlue.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
            listBlue.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
            // Green
            listGreen.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            // Blue
            listBlue.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
            listBlue.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_2) {
            // Blue
            listBlue.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            // Blue
            listBlue.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
            listBlue.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_6) {
            // Blue
            listBlue.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_6);
            // Green
            listGreen.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_DIM_7) {
            // Blue
            // Green
            listGreen.add(Constants.NOTE_C_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_DIM_7);
            listGreen.add(Constants.NOTE_D_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_DIM_7);
            listGreen.add(Constants.NOTE_G_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_DIM_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            // Blue
            listBlue.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
            // Green
            listGreen.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
            listGreen.add(Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
            listGreen.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
            listGreen.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
            listGreen.add(Constants.NOTE_A_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
            listGreen.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            // Blue
            listBlue.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
            listBlue.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_9) {
            // Blue
            listBlue.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_11) {
            // Blue
            listBlue.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_11);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_13) {
            // Blue
            listBlue.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_13);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_b9) {
            // Blue
            // Green
            listGreen.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
            listGreen.add(Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
            listGreen.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
            listGreen.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
            listGreen.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
            listGreen.add(Constants.NOTE_B_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7_b5) {
            // Blue
            // Green
            listGreen.add(Constants.NOTE_F_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7_b5);
            listGreen.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7_b5);
            listGreen.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7_b5);
            listGreen.add(Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7_b5);
        }
        if (Constants.ENABLE_CHORD_TYPE_SLASH_1) {
            // Blue
            listBlue.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_1);
            listBlue.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_1);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_SLASH_2) {
            // Blue
            // Green
            listGreen.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_2);
        }
        if (Constants.ENABLE_CHORD_TYPE_SLASH_3) {
            // Blue
            listBlue.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_3);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_SLASH_5) {
            // Blue
            listBlue.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_5);
            // Green
        }
        if (Constants.ENABLE_CHORD_TYPE_DIM_SLASH_b3) {
            // Blue
            // Green
            listGreen.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_DIM_SLASH_b3);
        }
        for (Integer l : listBlue) {
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.BLUE_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2][l] += Constants.BLUE_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6][l] += Constants.BLUE_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7][l] += Constants.BLUE_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9][l] += Constants.BLUE_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS][l] += Constants.BLUE_MULTIPLIER;
        }
        for (Integer l : listGreen) {
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS][l] += Constants.GREEN_MULTIPLIER;
        }

        //I_7_9_b9
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_6) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_6);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9][l] += Constants.PULL_MULTIPLIER;
        }

        //Im6
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_b9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
        }
        if (Constants.ENABLE_CHORD_TYPE_SLASH_2) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_2);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_6][l] += Constants.PULL_MULTIPLIER;
        }

        //IDimSlashb3
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_DIM_SLASH_b3][l] += Constants.PULL_MULTIPLIER;
        }

        //ISlash3
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_6) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_3][l] += Constants.PULL_MULTIPLIER;
        }
        for (Integer l : listGreen) {
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_3][l] += Constants.GREEN_MULTIPLIER;
        }

        //ISlash5
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_11) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_11);
        }
        if (Constants.ENABLE_CHORD_TYPE_13) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_13);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_5][l] += Constants.PULL_MULTIPLIER;
        }
        for (Integer l : listGreen) {
            mTransitionProbabilityMatrix[Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_5][l] += Constants.GREEN_MULTIPLIER;
        }

        //SharpIDim7
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_C_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_DIM_7][l] += Constants.PULL_MULTIPLIER;
        }

        //II_7_9_b9
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_11) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_11);
        }
        if (Constants.ENABLE_CHORD_TYPE_13) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_13);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_11][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_13][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS][l] += Constants.PULL_MULTIPLIER;
        }

        //iim_m7_m9
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
            listAllowedChords.add(Constants.NOTE_D_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_2) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_11) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_11);
        }
        if (Constants.ENABLE_CHORD_TYPE_13) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_13);
        }
        if (Constants.ENABLE_CHORD_TYPE_SLASH_3) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_3);
        }
        if (Constants.ENABLE_CHORD_TYPE_SLASH_5) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_5);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9][l] += Constants.PULL_MULTIPLIER;
        }
        for (Integer l : listGreen) {
            mTransitionProbabilityMatrix[Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9][l] += Constants.GREEN_MULTIPLIER;
        }

        //bII7
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_2) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_D_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7][l] += Constants.PULL_MULTIPLIER;
        }

        //SharpIIDim7
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_D_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_DIM_7][l] += Constants.PULL_MULTIPLIER;
        }

        //III_7_9_b9
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9][l] += Constants.PULL_MULTIPLIER;
        }

        //iiim_m7
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_2) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_6) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7][l] += Constants.PULL_MULTIPLIER;
        }
        for (Integer l : listGreen) {
            mTransitionProbabilityMatrix[Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7][l] += Constants.GREEN_MULTIPLIER;
        }

        //IIIm7b5
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_6) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_b9) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7_b5][l] += Constants.PULL_MULTIPLIER;
        }

        //IV_6_M7_m_m6
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_2) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_11) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_11);
        }
        if (Constants.ENABLE_CHORD_TYPE_13) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_13);
        }
        if (Constants.ENABLE_CHORD_TYPE_SLASH_3) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_3);
        }
        if (Constants.ENABLE_CHORD_TYPE_SLASH_5) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_5);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
        }
        for (Integer l : listGreen) {
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.GREEN_MULTIPLIER;
        }

        //IVm7
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_2) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7][l] += Constants.PULL_MULTIPLIER;
        }

        //IVSlash1
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_2) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_1][l] += Constants.PULL_MULTIPLIER;
        }
        for (Integer l : listGreen) {
            mTransitionProbabilityMatrix[Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_1][l] += Constants.GREEN_MULTIPLIER;
        }

        //SharpIVm7b5
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
            listAllowedChords.add(Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
            listAllowedChords.add(Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
            listAllowedChords.add(Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_11) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_11);
        }
        if (Constants.ENABLE_CHORD_TYPE_13) {
            listAllowedChords.add(Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_13);
        }
        if (Constants.ENABLE_CHORD_TYPE_b9) {
            listAllowedChords.add(Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
        }
        if (Constants.ENABLE_CHORD_TYPE_SLASH_5) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_5);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_F_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7_b5][l] += Constants.PULL_MULTIPLIER;
        }

        //V_7_9_11_13_sus
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_2) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_11][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_13][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS][l] += Constants.PULL_MULTIPLIER;
        }
        for (Integer l : listGreen) {
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_11][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_13][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS][l] += Constants.GREEN_MULTIPLIER;
        }

        //Vm_7
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_b9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7][l] += Constants.PULL_MULTIPLIER;
        }

        //VSlash1
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_2) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_1][l] += Constants.PULL_MULTIPLIER;
        }
        for (Integer l : listGreen) {
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_1][l] += Constants.GREEN_MULTIPLIER;
        }

        //VSlash2
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_b9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_G * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_2][l] += Constants.PULL_MULTIPLIER;
        }

        //SharpVDim7
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            listAllowedChords.add(Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_G_SHARP * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_DIM_7][l] += Constants.PULL_MULTIPLIER;
        }

        //vim_m7_m9
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_6) {
            listAllowedChords.add(Constants.NOTE_F * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9][l] += Constants.PULL_MULTIPLIER;
        }
        for (Integer l : listGreen) {
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7][l] += Constants.GREEN_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9][l] += Constants.GREEN_MULTIPLIER;
        }

        //VI_7_9_b9
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9][l] += Constants.PULL_MULTIPLIER;
        }

        //VIm7b5_b3
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_b9) {
            listAllowedChords.add(Constants.NOTE_D * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_A * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7_b5][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_E_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
        }

        //bVI
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_B_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_B_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_A_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
        }

        //bVI7
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_SLASH_5) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_5);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_A_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7][l] += Constants.PULL_MULTIPLIER;
        }

        //VII_7_9_b9
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MINOR) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_MINOR_7) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9][l] += Constants.PULL_MULTIPLIER;
        }

        //VIIm7b5
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_7) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_9) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9);
        }
        if (Constants.ENABLE_CHORD_TYPE_b9) {
            listAllowedChords.add(Constants.NOTE_E * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_b9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_B * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MINOR_7_b5][l] += Constants.PULL_MULTIPLIER;
        }

        //bVII_9
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_MAJOR) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR);
        }
        if (Constants.ENABLE_CHORD_TYPE_SUS) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SUS);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_7) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_7);
        }
        if (Constants.ENABLE_CHORD_TYPE_2) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_2);
        }
        if (Constants.ENABLE_CHORD_TYPE_6) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_6);
        }
        if (Constants.ENABLE_CHORD_TYPE_MAJ_9) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJ_9);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_B_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_MAJOR][l] += Constants.PULL_MULTIPLIER;
            mTransitionProbabilityMatrix[Constants.NOTE_B_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9][l] += Constants.PULL_MULTIPLIER;
        }

        //bVII9
        listAllowedChords = new ArrayList();
        if (Constants.ENABLE_CHORD_TYPE_SLASH_5) {
            listAllowedChords.add(Constants.NOTE_C * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_SLASH_5);
        }
        for (Integer l : listAllowedChords) {
            mTransitionProbabilityMatrix[Constants.NOTE_B_FLAT * Constants.NUMBER_OF_CHORD_TYPES + Constants.CHORD_TYPE_9][l] += Constants.PULL_MULTIPLIER;
        }

        // If a row is only zeroes, add 1 to N:th position
        for (int row=0; row<Constants.N; row++) {
            double rowSum = sumVector(mTransitionProbabilityMatrix[row]);
            if (rowSum == 0.) {
                mTransitionProbabilityMatrix[row][Constants.N - 1] = 1;
                rowSum = 1;
            }
            // Normalize each row
            mTransitionProbabilityMatrix[row] = divisionVector(mTransitionProbabilityMatrix[row], rowSum);
        }
        //printMatrix((mTransitionProbabilityMatrix));
    }
}
