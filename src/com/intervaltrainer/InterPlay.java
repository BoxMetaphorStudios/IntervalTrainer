package com.intervaltrainer;

// InterPlay holds all tone and interval information, and plays intervals.

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class InterPlay 
{
	private static final String LOGTAG = "InterPlay.java";
	private static final int MAX_STREAMS = 2; 	// SoundPool streams
	private static final int TONE_DURATION = 800; // Interval playback tone length in ms
	
	private Context m_Context;
	private Resources m_Res;
	private Tone[] m_Tone;
	private InterInfo[] m_InterInfo;
	//
	private QualInfo[] m_QualInfo;
	private DInterInfo[] m_DInterInfo;
	//
	private SoundPool m_SoundPool = null;
	private Interval m_CurInterval = null;
	private int m_PlayState = 0;
	private Timer m_PlayTimer = null;
	private Random m_Random = new Random();
	
	public InterPlay(Context context)
	{
		m_Context = context;
		m_Res = context.getResources();
		m_SoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 100);
		
		initTones();
		initInterInfo();
		//
		initQualInfo();
		initDInterInfo();
	}
	
	// Build the tone information array:
	private void initTones() {
		m_Tone = new Tone[Tone.TONE_COUNT];
		
		m_Tone[Tone.G3] = new Tone(Tone.G3, m_SoundPool.load(m_Context, R.raw.g3, 1));
		m_Tone[Tone.Ab3] = new Tone(Tone.Ab3, m_SoundPool.load(m_Context, R.raw.a_flat3, 1));
		m_Tone[Tone.A3] = new Tone(Tone.A3, m_SoundPool.load(m_Context, R.raw.a3, 1));
		m_Tone[Tone.Bb3] = new Tone(Tone.Bb3, m_SoundPool.load(m_Context, R.raw.b_flat3, 1));
		m_Tone[Tone.B3] = new Tone(Tone.B3, m_SoundPool.load(m_Context, R.raw.b3, 1));
		m_Tone[Tone.C4] = new Tone(Tone.C4, m_SoundPool.load(m_Context, R.raw.c4, 1));
		m_Tone[Tone.Db4] = new Tone(Tone.Db4, m_SoundPool.load(m_Context, R.raw.d_flat4, 1));
		m_Tone[Tone.D4] = new Tone(Tone.D4, m_SoundPool.load(m_Context, R.raw.d4, 1));
		m_Tone[Tone.Eb4] = new Tone(Tone.Eb4, m_SoundPool.load(m_Context, R.raw.e_flat4, 1));
		m_Tone[Tone.E4] = new Tone(Tone.E4, m_SoundPool.load(m_Context, R.raw.e4, 1));
		m_Tone[Tone.F4] = new Tone(Tone.F4, m_SoundPool.load(m_Context, R.raw.f4, 1));
		m_Tone[Tone.Fs4] = new Tone(Tone.Fs4, m_SoundPool.load(m_Context, R.raw.f_sharp4, 1));
		m_Tone[Tone.G4] = new Tone(Tone.G4, m_SoundPool.load(m_Context, R.raw.g4, 1));
		m_Tone[Tone.Ab4] = new Tone(Tone.Ab4, m_SoundPool.load(m_Context, R.raw.a_flat4, 1));
		m_Tone[Tone.A4] = new Tone(Tone.A4, m_SoundPool.load(m_Context, R.raw.a4, 1));
		m_Tone[Tone.Bb4] = new Tone(Tone.Bb4, m_SoundPool.load(m_Context, R.raw.b_flat4, 1));
		m_Tone[Tone.B4] = new Tone(Tone.B4, m_SoundPool.load(m_Context, R.raw.b4, 1));
		m_Tone[Tone.C5] = new Tone(Tone.C5, m_SoundPool.load(m_Context, R.raw.c5, 1));
		m_Tone[Tone.Db5] = new Tone(Tone.Db4, m_SoundPool.load(m_Context, R.raw.d_flat5, 1));
		m_Tone[Tone.D5] = new Tone(Tone.D5, m_SoundPool.load(m_Context, R.raw.d5, 1));
		m_Tone[Tone.Eb5] = new Tone(Tone.Eb5, m_SoundPool.load(m_Context, R.raw.e_flat5, 1));
		m_Tone[Tone.E5] = new Tone(Tone.E5, m_SoundPool.load(m_Context, R.raw.e5, 1));
		m_Tone[Tone.F5] = new Tone(Tone.F5, m_SoundPool.load(m_Context, R.raw.f5, 1));
		m_Tone[Tone.Fs5] = new Tone(Tone.Fs5, m_SoundPool.load(m_Context, R.raw.f_sharp5, 1));
		m_Tone[Tone.G5] = new Tone(Tone.G5, m_SoundPool.load(m_Context, R.raw.g5, 1));
	}
	
	// Build the interval information array.
	// Note about the weights array (the last parameter):
	// Index 0 holds weights for Easy quiz level, index 1 for Standard level and index 2 for Advanced.
	// The sum of all weights of the same index must be 1000. This ensures proper random selection for each level.
	private void initInterInfo() {
		m_InterInfo = new InterInfo[InterInfo.TYPE_COUNT];
		
		m_InterInfo[InterInfo.UNISON] = new InterInfo(InterInfo.UNISON, m_Res.getString(R.string.ppU), m_Res.getString(R.string.ppU_info), R.drawable.ip1_0, new int[]{62, 28, 0});
		m_InterInfo[InterInfo.MIN2ND] = new InterInfo(InterInfo.MIN2ND, m_Res.getString(R.string.m2), m_Res.getString(R.string.m2_info), R.drawable.ip1_1, new int[]{134, 81, 60});
		m_InterInfo[InterInfo.MAJ2ND] = new InterInfo(InterInfo.MAJ2ND, m_Res.getString(R.string.mm2), m_Res.getString(R.string.mm2_info), R.drawable.ip1_2, new int[]{134, 81, 60});
		m_InterInfo[InterInfo.MIN3RD] = new InterInfo(InterInfo.MIN3RD, m_Res.getString(R.string.m3), m_Res.getString(R.string.m3_info), R.drawable.ip1_3, new int[]{134, 81, 60});
		m_InterInfo[InterInfo.MAJ3RD] = new InterInfo(InterInfo.MAJ3RD, m_Res.getString(R.string.mm3), m_Res.getString(R.string.mm3_info), R.drawable.ip1_4, new int[]{134, 81, 60});
		m_InterInfo[InterInfo.PERF4TH] = new InterInfo(InterInfo.PERF4TH, m_Res.getString(R.string.p4), m_Res.getString(R.string.p4_info), R.drawable.ip1_5, new int[]{134, 81, 60});
		m_InterInfo[InterInfo.AUG4TH] = new InterInfo(InterInfo.AUG4TH, m_Res.getString(R.string.a4_05),  m_Res.getString(R.string.a4_05_info), R.drawable.ip1_6, new int[]{134, 81, 60});
		m_InterInfo[InterInfo.PERF5TH] = new InterInfo(InterInfo.PERF5TH, m_Res.getString(R.string.p5), m_Res.getString(R.string.p5_info), R.drawable.ip1_7, new int[]{134, 81, 60});
		m_InterInfo[InterInfo.MIN6TH] = new InterInfo(InterInfo.MIN6TH, m_Res.getString(R.string.m6), m_Res.getString(R.string.m6_info), R.drawable.ip1_8, new int[]{0, 81, 60});
		m_InterInfo[InterInfo.MAJ6TH] = new InterInfo(InterInfo.MAJ6TH, m_Res.getString(R.string.mm6), m_Res.getString(R.string.mm6_info), R.drawable.ip1_9, new int[]{0, 81, 60});
		m_InterInfo[InterInfo.MIN7TH] = new InterInfo(InterInfo.MIN7TH, m_Res.getString(R.string.m7), m_Res.getString(R.string.m7_info), R.drawable.ip1_10, new int[]{0, 81, 60});
		m_InterInfo[InterInfo.MAJ7TH] = new InterInfo(InterInfo.MAJ7TH, m_Res.getString(R.string.mm7), m_Res.getString(R.string.mm7_info), R.drawable.ip1_11, new int[]{0, 81, 60});
		m_InterInfo[InterInfo.OCTAVE] = new InterInfo(InterInfo.OCTAVE, m_Res.getString(R.string.ppO), m_Res.getString(R.string.ppO_info), R.drawable.ip1_12, new int[]{0, 81, 60});
		m_InterInfo[InterInfo.MIN9TH] = new InterInfo(InterInfo.MIN9TH, m_Res.getString(R.string.m9), m_Res.getString(R.string.m9_info), R.drawable.ip1_13, new int[]{0, 0, 40});
		m_InterInfo[InterInfo.MAJ9TH] = new InterInfo(InterInfo.MAJ9TH, m_Res.getString(R.string.mm9), m_Res.getString(R.string.mm9_info), R.drawable.ip1_14, new int[]{0, 0, 40});
		m_InterInfo[InterInfo.MIN10TH] = new InterInfo(InterInfo.MIN10TH, m_Res.getString(R.string.m10), m_Res.getString(R.string.m10_info), R.drawable.ip1_15, new int[]{0, 0, 40});
		m_InterInfo[InterInfo.MAJ10TH] = new InterInfo(InterInfo.MAJ10TH, m_Res.getString(R.string.mm10), m_Res.getString(R.string.mm10_info), R.drawable.ip1_16, new int[]{0, 0, 40});
		m_InterInfo[InterInfo.PERF11TH] = new InterInfo(InterInfo.PERF11TH, m_Res.getString(R.string.p11), m_Res.getString(R.string.p11_info), R.drawable.ip1_17, new int[]{0, 0, 40});
		m_InterInfo[InterInfo.AUG11TH] = new InterInfo(InterInfo.AUG11TH, m_Res.getString(R.string.a11_012), m_Res.getString(R.string.a11_012_info), R.drawable.ip1_18, new int[]{0, 0, 40});
		m_InterInfo[InterInfo.PERF12TH] = new InterInfo(InterInfo.PERF12TH, m_Res.getString(R.string.p12), m_Res.getString(R.string.p12_info), R.drawable.ip1_19, new int[]{0, 0, 40});
	}
	
	//
	// build qualifier info array:
	private void initQualInfo() {
		m_QualInfo = new QualInfo[QualInfo.TYPE_COUNT];
		
		m_QualInfo[QualInfo.MAJOR] = new QualInfo(QualInfo.MAJOR, DInterInfo.IMPURE, 0, m_Res.getString(R.string.major));
		m_QualInfo[QualInfo.MINOR] = new QualInfo(QualInfo.MINOR, DInterInfo.IMPURE, -1, m_Res.getString(R.string.minor));
		m_QualInfo[QualInfo.AUG] = new QualInfo(QualInfo.AUG, DInterInfo.PURE, +1, m_Res.getString(R.string.aug));
		m_QualInfo[QualInfo.DIM] = new QualInfo(QualInfo.DIM, DInterInfo.PURE, -1, m_Res.getString(R.string.dim));
		m_QualInfo[QualInfo.PERFECT] = new QualInfo(QualInfo.PERFECT, DInterInfo.PURE, 0, m_Res.getString(R.string.perfect));
	}
	
	// build diatonic interval info array:
	private void initDInterInfo() {
		m_DInterInfo = new DInterInfo[DInterInfo.TYPE_COUNT];
		
		m_DInterInfo[DInterInfo.DUNISON] = new DInterInfo(DInterInfo.DUNISON, DInterInfo.PURE, InterInfo.UNISON, m_Res.getString(R.string.dU));
		m_DInterInfo[DInterInfo.D2ND] = new DInterInfo(DInterInfo.D2ND, DInterInfo.IMPURE, InterInfo.MAJ2ND, m_Res.getString(R.string.d2));
		m_DInterInfo[DInterInfo.D3RD] = new DInterInfo(DInterInfo.D3RD, DInterInfo.IMPURE, InterInfo.MAJ3RD, m_Res.getString(R.string.d3));
		m_DInterInfo[DInterInfo.D4TH] = new DInterInfo(DInterInfo.D4TH, DInterInfo.PURE, InterInfo.PERF4TH, m_Res.getString(R.string.d4));
		m_DInterInfo[DInterInfo.D5TH] = new DInterInfo(DInterInfo.D5TH, DInterInfo.PURE, InterInfo.PERF5TH, m_Res.getString(R.string.d5));
		m_DInterInfo[DInterInfo.D6TH] = new DInterInfo(DInterInfo.D6TH, DInterInfo.IMPURE, InterInfo.MAJ6TH, m_Res.getString(R.string.d6));
		m_DInterInfo[DInterInfo.D7TH] = new DInterInfo(DInterInfo.D7TH, DInterInfo.IMPURE, InterInfo.MAJ7TH, m_Res.getString(R.string.d7));
		m_DInterInfo[DInterInfo.DOCTAVE] = new DInterInfo(DInterInfo.DOCTAVE, DInterInfo.PURE, InterInfo.OCTAVE, m_Res.getString(R.string.dO));
		m_DInterInfo[DInterInfo.D9TH] = new DInterInfo(DInterInfo.D9TH, DInterInfo.IMPURE, InterInfo.MAJ9TH, m_Res.getString(R.string.d9));
		m_DInterInfo[DInterInfo.D10TH] = new DInterInfo(DInterInfo.D10TH, DInterInfo.IMPURE, InterInfo.MAJ10TH, m_Res.getString(R.string.d10));
		m_DInterInfo[DInterInfo.D11TH] = new DInterInfo(DInterInfo.D11TH, DInterInfo.PURE, InterInfo.PERF11TH, m_Res.getString(R.string.d11));
		m_DInterInfo[DInterInfo.D12TH] = new DInterInfo(DInterInfo.D12TH, DInterInfo.PURE, InterInfo.PERF12TH, m_Res.getString(R.string.d12));
	}	
	//
		
	public InterInfo[] getInterInfoArray()
	{
		return m_InterInfo;
	}
	
	public InterInfo getInterInfo(int type)
	{
		if(type >= 0 && type < InterInfo.TYPE_COUNT)
			return m_InterInfo[type];
		else
			return null;
	}
	
	public QualInfo[] getQualInfoArray()
	{
		return m_QualInfo;
	}
	
	public QualInfo getQualInfo(int type)
	{
		if(type >= 0 && type < QualInfo.TYPE_COUNT)
			return m_QualInfo[type];
		else
			return null;
	}
	
	public DInterInfo[] getDInterInfoArray()
	{
		return m_DInterInfo;
	}
	
	public DInterInfo getDInterInfo(int type)
	{
		if(type >= 0 && type < DInterInfo.TYPE_COUNT)
			return m_DInterInfo[type];
		else
			return null;
	}
	
	// Plays out the interval melody.
	// The playback speed can be modified through the TONE_DURATION value.
	public boolean playInterval(Interval interval)
	{
		if(interval == null
			|| m_SoundPool == null 
			|| interval.m_LoToneId < Tone.MIN_TONE
			|| interval.m_LoToneId > Tone.MAX_TONE
			|| interval.m_HiToneId < Tone.MIN_TONE
			|| interval.m_HiToneId > Tone.MAX_TONE)
		{
			m_CurInterval = null;
			return false;
		}
		
		m_SoundPool.autoPause();
				
		if(m_PlayTimer != null){
			m_PlayTimer.cancel();
			m_PlayTimer = null;
		}
		
		m_CurInterval = interval;
		m_PlayState = 0;
		
		m_PlayTimer = new Timer();
		m_PlayTimer.schedule(new TimerTask(){
			@Override
			public void run() {
				if(m_PlayTimer != null && m_CurInterval != null && m_PlayState < 7){
					switch(m_PlayState){
					case 0:
					case 4:
						m_SoundPool.play(m_Tone[m_CurInterval.m_LoToneId].m_PoolId, 1.0f, 1.0f, 10, 0, 1.0f);
						++m_PlayState;
						break;
					case 1:
					case 3:
						m_SoundPool.play(m_Tone[m_CurInterval.m_HiToneId].m_PoolId, 1.0f, 1.0f, 10, 0, 1.0f);
						++m_PlayState;
						break;
					case 2:
					case 5:
						// rest:
						++m_PlayState;
						break;
					case 6:
						m_SoundPool.play(m_Tone[m_CurInterval.m_LoToneId].m_PoolId, 1.0f, 1.0f, 10, 0, 1.0f);
						m_SoundPool.play(m_Tone[m_CurInterval.m_HiToneId].m_PoolId, 1.0f, 1.0f, 10, 0, 1.0f);
						++m_PlayState;
						break;
					}
				}
				else{
					cancel();
					m_CurInterval = null;
					m_PlayState = 0;
					m_PlayTimer = null;
				}	
			}
		}, 0, TONE_DURATION);
		
		return true;
	}
	
	// Stops any SoundPool playback:
	public void shutUp()
	{
		if(m_SoundPool != null) 
			m_SoundPool.autoPause();
				
		if(m_PlayTimer != null){
			m_PlayTimer.cancel();
			m_PlayTimer = null;
		}
	}
	
	private void clearUsedInfo()
	{
		int k;
		
		for(k = 0; k < InterInfo.TYPE_COUNT; ++k)
			m_InterInfo[k].m_Used = false;
	}
	
	private int getRandomType(int mass, int level)
	{
		int k, sum;
		int val = m_Random.nextInt(mass);
		
		for(k = sum = 0; k < InterInfo.TYPE_COUNT; ++k){
			if(m_InterInfo[k].m_Used == false){
				sum += m_InterInfo[k].m_Weights[level];
				if(sum >= val){
					Log.w(LOGTAG, "getRandomType() - type: " + k);
					return k;
				}
			}
		}
		
		Log.w(LOGTAG, "getRandomType - type not found");
		return 0; // should not happen
	}
	
	// Fills the types array with random interval types. The array can have variable length.
	// Returns a random index into the types array, which can be used to set the "right interval" in the quiz.
	public int getRandomTypes(int[] types, int level)
	{
		int k, type;
		int len = types.length;
		int mass = 1000;
		
		clearUsedInfo();
		
		for(k = 0; k < len; ++k){
			type = getRandomType(mass, level);
			types[k] = type;
			m_InterInfo[type].m_Used = true;
			mass -= m_InterInfo[type].m_Weights[level];
		}
		
		return m_Random.nextInt(len); 
	}
	
	public int getRandomType(int level)
	{
		return getRandomType(1000, level);
	}
	
	// Returns a randomly positioned interval of a type:
	public Interval getRandomInterval(int type)
	{
		int range = Tone.TONE_COUNT - type;
		int lo = m_Random.nextInt(range);
		return new Interval(lo, lo + type);
	}
}
