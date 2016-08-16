package com.intervaltrainer;

public class DInterInfo 
{
	// Diatonic interval types
	public static final int DUNISON = 0;
	public static final int D2ND = 1;
	public static final int D3RD = 2;
	public static final int D4TH = 3;
	public static final int D5TH = 4;
	public static final int D6TH = 5;
	public static final int D7TH = 6;
	public static final int DOCTAVE = 7;
	public static final int D9TH = 8;
	public static final int D10TH = 9;
	public static final int D11TH = 10;
	public static final int D12TH = 11;
	
	// interval supertype
	public static final int PURE = 0;
	public static final int IMPURE = 1;
		
	public static final int MIN_TYPE = DUNISON;
	public static final int MAX_TYPE = D12TH;
	public static final int TYPE_COUNT = 12;
	
	public int m_DType;
	public int m_SType;
	public int m_Type;
	
	public String m_Text = null;	// text description of interval (from string values)
	
	public DInterInfo(int dtype, int stype, int type, String text)
	{
		m_DType = dtype;
		m_SType = stype;
		m_Type = type;
		m_Text = new String(text);
	}
	
	public String toString() 
	{
        return m_Text; 
    }
}
