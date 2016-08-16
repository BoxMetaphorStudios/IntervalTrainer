package com.intervaltrainer;

// Prefs stores persistent preferences (currently quiz level, length and number of replays)

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs
{
	private SharedPreferences m_Prefs;
	
	// option keys:
	private static final String OPTIONS_NAME = "IntervalTrainerOptions";
	private static final String QUIZ_LEVEL_KEY = "QuizLevel";
	//
	private static final String QUIZ_STYLE_KEY = "QuizStyle";
	//
	private static final String QUIZ_COUNT_KEY = "QuizCount";
	private static final String REPLAY_LIMIT_KEY = "ReplayLimit";
	private static final String REPLAY_COUNT_KEY = "ReplayCount";
	
	
	// Default values - will be active on first run of the app.
	// Quiz level:
	// 0 - Easy
	// 1 - Standard
	// 2 - Advanced
	private static int QUIZ_LEVEL_DEFAULT = 1; 
	//
	// Quiz style:
	// 0 - Multiple choice
	// 1 - Comprehensive
	private static int QUIZ_STYLE_DEFAULT = 1; 
	//
	// Quiz length:
	private static int QUIZ_COUNT_DEFAULT = 10;
	// Replay limit:
	private static boolean REPLAY_LIMIT_DEFAULT = false;
	// Replay count:
	private static int REPLAY_COUNT_DEFAULT = 3;
		
	Prefs(Context context)
	{
		m_Prefs = context.getSharedPreferences(OPTIONS_NAME, Context.MODE_PRIVATE);
	}
	
	public int getQuizLevel()
	{
		return getOptionInt(QUIZ_LEVEL_KEY, QUIZ_LEVEL_DEFAULT);
	}
	
	public void setQuizLevel(int level)
	{
		setOptionInt(QUIZ_LEVEL_KEY, level);
	}
	
	public int getQuizStyle()
	{
		return getOptionInt(QUIZ_STYLE_KEY, QUIZ_STYLE_DEFAULT);
	}

	public void setQuizStyle(int style)
	{
		setOptionInt(QUIZ_STYLE_KEY, style);
	}
	
	public void setQuizCount(int count)
	{
		setOptionInt(QUIZ_COUNT_KEY, count);
	}
	
	public int getQuizCount()
	{
		return getOptionInt(QUIZ_COUNT_KEY, QUIZ_COUNT_DEFAULT);
	}
	
	public void setReplayLimit(boolean set)
	{
		setOptionBool(REPLAY_LIMIT_KEY, set);
	}
	
	public boolean getReplayLimit()
	{
		return getOptionBool(REPLAY_LIMIT_KEY, REPLAY_LIMIT_DEFAULT);
	}
	
	public void setReplayCount(int count)
	{
		setOptionInt(REPLAY_COUNT_KEY, count);
	}
	
	public int getReplayCount()
	{
		return getOptionInt(REPLAY_COUNT_KEY, REPLAY_COUNT_DEFAULT);
	}
	
	////////////////////
	// general:
	
	private void setOptionBool(String key, boolean set)
	{
		SharedPreferences.Editor ed = m_Prefs.edit();

		ed.putBoolean(key, set);
		ed.commit();
	}
	
	private boolean getOptionBool(String key, boolean defSet)
	{
		return m_Prefs.getBoolean(key, defSet);
	}
	
	private void setOptionInt(String key, int val)
	{
		SharedPreferences.Editor ed = m_Prefs.edit();

		ed.putInt(key, val);
		ed.commit();
	}
	
	private int getOptionInt(String key, int defVal)
	{
		return m_Prefs.getInt(key, defVal);
	}
	
	private void setOptionFloat(String key, float val)
	{
		SharedPreferences.Editor ed = m_Prefs.edit();

		ed.putFloat(key, val);
		ed.commit();
	}
	
	private float getOptionFloat(String key, float defVal)
	{
		return m_Prefs.getFloat(key, defVal);
	}
}

