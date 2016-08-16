package com.intervaltrainer;

public class QualInfo 
{
	// Qualifier types:
	public static final int MINOR = 0;
	public static final int MAJOR = 1;
	public static final int AUG = 2;
	public static final int DIM = 3;
	public static final int PERFECT = 4;
	
	public static final int MIN_TYPE = MINOR;
	public static final int MAX_TYPE = PERFECT;
	public static final int TYPE_COUNT = 5;
	
	public int m_Type;
	public int m_SType; // interval supertype the qualifier applies to (pure/impure)
	public int m_Mod;	// number of semitones to add/subtract from the diatonic (major or perfect)
	public String m_Text = null;	// text description of interval (from string values)
	
	public QualInfo(int type, int stype, int mod, String text)
	{
		m_Type = type;
		m_SType = stype;
		m_Mod = mod;
		m_Text = new String(text);
	}
	
	public String toString() 
	{
        return m_Text; 
    }
}
