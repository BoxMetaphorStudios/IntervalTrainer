package com.intervaltrainer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class QuizActivity extends FragmentActivity {
	private static final String LOGTAG = "QuizActivity.java";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// choose quiz style:
		Prefs prefs = new Prefs(this);
		setContentView(prefs.getQuizStyle() == 0 ?  R.layout.activity_quiz : R.layout.activity_quiz2);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
