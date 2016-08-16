package com.intervaltrainer;

// Interval class - just the low and high tone index

public class Interval 
{
	public int m_LoToneId;
	public int m_HiToneId;

	public Interval(int loToneId, int hiToneId)
	{
		if(loToneId <= hiToneId){
			m_LoToneId = loToneId;
			m_HiToneId = hiToneId;
		}
		else{
			m_LoToneId = hiToneId;
			m_HiToneId = loToneId;
		}
	}
}
