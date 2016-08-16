package com.intervaltrainer;

public class Tone {
	// Tone ids, also number of semitones from G3 and index into InterPlay
	// table.
	// Tone octave numbers comply to Scientific pitch notation.
	public static final int G3 = 0;
	public static final int Gs3 = 1;
	public static final int Ab3 = 1;
	public static final int A3 = 2;
	public static final int As3 = 3;
	public static final int Bb3 = 3;
	public static final int B3 = 4;
	public static final int C4 = 5;
	public static final int Cs4 = 6;
	public static final int Db4 = 6;
	public static final int D4 = 7;
	public static final int Ds4 = 8;
	public static final int Eb4 = 8;
	public static final int E4 = 9;
	public static final int F4 = 10;
	public static final int Fs4 = 11;
	public static final int Gb4 = 11;
	public static final int G4 = 12;
	public static final int Gs4 = 13;
	public static final int Ab4 = 13;
	public static final int A4 = 14;
	public static final int As4 = 15;
	public static final int Bb4 = 15;
	public static final int B4 = 16;
	public static final int C5 = 17;
	public static final int Cs5 = 18;
	public static final int Db5 = 18;
	public static final int D5 = 19;
	public static final int Ds5 = 20;
	public static final int Eb5 = 20;
	public static final int E5 = 21;
	public static final int F5 = 22;
	public static final int Fs5 = 23;
	public static final int Gb5 = 23;
	public static final int G5 = 24;

	public static final int MIN_TONE = G3;
	public static final int MAX_TONE = G5;
	public static final int TONE_COUNT = 25;

	public int m_Id;
	public int m_PoolId; // SoundPool index

	public Tone(int id, int poolId) {
		m_Id = id;
		m_PoolId = poolId;
	}
}
