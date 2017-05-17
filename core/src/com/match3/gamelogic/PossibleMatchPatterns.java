package com.match3.gamelogic;

public class PossibleMatchPatterns {
    public PossibleMatchPattern[] patterns = new PossibleMatchPattern[52];
    public int[] left3 = new int[4];
    public int[] right3 = new int[4];
    public int[] up3 = new int[4];
    public int[] down3 = new int[4];
    public int[] left4 = new int[2];
    public int[] right4 = new int[2];
    public int[] up4 = new int[2];
    public int[] down4 = new int[2];
    public int[] left5 = new int[4];
    public int[] right5 = new int[4];
    public int[] up5 = new int[4];
    public int[] down5 = new int[4];
    public int[] left6 = new int[2];
    public int[] right6 = new int[2];
    public int[] up6 = new int[2];
    public int[] down6 = new int[2];
    public int left7;
    public int right7;
    public int up7;
    public int down7;

    public PossibleMatchPatterns() {
        for (int i = 0; i < 52; i++)
            patterns[i] = new PossibleMatchPattern();
    }
}
