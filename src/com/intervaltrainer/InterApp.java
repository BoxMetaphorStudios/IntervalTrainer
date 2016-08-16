package com.intervaltrainer;

// InterApp is a global class visible to all activities.
// It contains the global InterPlay instance, used by FlashCards and Quiz.

import android.app.Application;
import android.content.res.Configuration;

public class InterApp extends Application
{
	private static final String LOGTAG = "InterApp.java";
	private InterPlay m_InterPlay = null;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		m_InterPlay = new InterPlay(this);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}
	
	public InterPlay getInterPlay()
	{
		return m_InterPlay;
	}
}
