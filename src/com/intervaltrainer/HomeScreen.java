package com.intervaltrainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class HomeScreen extends Activity implements OnClickListener {

	Button fc;
	Button quiz;
	Button options;
	private AlertDialog m_FirstTimeDialog = null;
	private Context m_Context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		m_Context = this;
		initialize();
		if (isFirstTime()) {
			firstDialog();
		}
	}

	private void firstDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.dialog_first_time, null);

		final Button butLetsGo = (Button) layout.findViewById(R.id.butFirst);

		TextView tutorText = (TextView) layout.findViewById(R.id.tv_first);
		tutorText.setText(R.string.first_time_text);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setMessage(m_Context.getResources().getText(
				R.string.first_time_title));

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				m_FirstTimeDialog = null;
			}
		});
		butLetsGo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_FirstTimeDialog.dismiss();
				m_FirstTimeDialog = null;
			}
		});

		m_FirstTimeDialog = builder.show();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void initialize() {
		fc = (Button) findViewById(R.id.fc_button);
		quiz = (Button) findViewById(R.id.quiz_button);
		options = (Button) findViewById(R.id.options_button);
		fc.setOnClickListener(this);
		quiz.setOnClickListener(this);
		options.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fc_button:
			Intent intent_fc = new Intent(this, FlashcardListActivity.class);
			// Intent intent_fc = new Intent(this, FlashCards.class);
			this.startActivity(intent_fc);
			break;
		case R.id.quiz_button:
			Intent intent_quiz = new Intent(this, QuizActivity.class);
			// Intent intent_quiz = new Intent(this, Quiz.class);
			this.startActivity(intent_quiz);
			break;
		case R.id.options_button:
			Intent intent_options = new Intent(this, Options.class);
			this.startActivity(intent_options);
			break;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	private boolean isFirstTime() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		boolean ranBefore = preferences.getBoolean("RanBefore", false);
		if (!ranBefore) {
			// first time
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("RanBefore", true);
			editor.commit();
		}
		return !ranBefore;
	}
}