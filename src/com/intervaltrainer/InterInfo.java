package com.intervaltrainer;

// InterInfo class holds all information for an interval type.

public class InterInfo {
	// Interval types - also number of semitones and index into InterPlay table:
	public static final int UNISON = 0;
	public static final int MIN2ND = 1;
	public static final int MAJ2ND = 2;
	public static final int MIN3RD = 3;
	public static final int MAJ3RD = 4;
	public static final int PERF4TH = 5;
	public static final int AUG4TH = 6;
	public static final int DIM5TH = 6;
	public static final int PERF5TH = 7;
	public static final int MIN6TH = 8;
	public static final int MAJ6TH = 9;
	public static final int MIN7TH = 10;
	public static final int MAJ7TH = 11;
	public static final int OCTAVE = 12;
	public static final int MIN9TH = 13;
	public static final int MAJ9TH = 14;
	public static final int MIN10TH = 15;
	public static final int MAJ10TH = 16;
	public static final int PERF11TH = 17;
	public static final int AUG11TH = 18;
	public static final int DIM12TH = 18;
	public static final int PERF12TH = 19;

	public static final int MIN_TYPE = UNISON;
	public static final int MAX_TYPE = PERF12TH;
	public static final int TYPE_COUNT = 20;

	public int m_Type;
	public int m_ImageId; // flashcard image resource id
	public String m_Text = null; // text description of interval (from string
									// values)
	public String m_Info = null; // interval info text
	public int[] m_Weights; // 3 values used as random selection weights for
							// different levels
	public boolean m_Used = false; // internal, used for random selection

	public InterInfo(int type, String text, String info, int imageId,
			int[] weights) {
		m_Type = type;
		m_ImageId = imageId;
		m_Text = new String(text);
		m_Info = new String(info);
		m_Weights = weights;
		m_Used = false;
	}

	public String toString() {
		return m_Text;
	}
}
