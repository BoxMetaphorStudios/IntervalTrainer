package com.intervaltrainer;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class Options extends ListActivity {

	private static final String LOGTAG = "Options.java";

	String prefs[] = { "Quiz Settings", "Tutor", "Info/Credits" };

	private Context m_Context;
	private Prefs m_Prefs;
	private AlertDialog m_QuizSettingsDialog = null;
	private AlertDialog m_TutorDialog = null;
	private AlertDialog m_CreditsDialog = null;

	/*
	 * These are some ideas I had to make the app more adaptable. I would only
	 * deem the tutor and help necessary, the rest would be nice to have. Here's
	 * what I mean by each of these: -Quiz length: How many questions the user
	 * wants in their quiz -Number of plays: How many times the user gets to
	 * listen to the sound -Interval Selection: Which intervals will be quizzed
	 * -Tutor: Advice about how to listen to intervals -High Scores: I think
	 * users would like to see lifetime stats tracked. Percentage right of each
	 * interval, points earned in the quiz mode, etc. -Help: UI assistance,
	 * contact info for bugs and suggestions, etc.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setListAdapter(new ArrayAdapter<String>(Options.this,
				android.R.layout.simple_list_item_1, prefs));

		m_Context = this;
		m_Prefs = new Prefs(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		switch (position) {
		case 0:
			quizSettingDialog();
			break;
		case 1:
			tutorDialog();
			break;
		case 2:
			creditsDialog();
			break;
		}
	}

	// Sets quiz level, length and number of replays:
	private void quizSettingDialog() {
		// Log.d(LOGTAG, "quizSettingsDialog");
		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.dialog_quiz_settings, null);
		
		final RadioButton[] radioBut = new RadioButton[3];
		radioBut[0] = (RadioButton) layout.findViewById(R.id.butQuizLevel1);
		radioBut[1] = (RadioButton) layout.findViewById(R.id.butQuizLevel2);
		radioBut[2] = (RadioButton) layout.findViewById(R.id.butQuizLevel3);
		
		final RadioButton[] styleBut = new RadioButton[2];
		styleBut[0] = (RadioButton) layout.findViewById(R.id.butQuizStyle1);
		styleBut[1] = (RadioButton) layout.findViewById(R.id.butQuizStyle2);
		
		final EditText editCount = (EditText) layout
				.findViewById(R.id.editQuizCount);
		final CheckBox checkReplay = (CheckBox) layout
				.findViewById(R.id.checkPlayLimit);
		final EditText replayCount = (EditText) layout
				.findViewById(R.id.editPlayCount);
		final Button butOk = (Button) layout
				.findViewById(R.id.butQuizSettingsOk);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle(m_Context.getResources().getText(
				R.string.quizSettingsTitle));

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				m_QuizSettingsDialog = null;
			}
		});

		checkReplay.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				replayCount.setEnabled(isChecked);
			}
		});

		butOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int k, count;
				String countString;

				m_QuizSettingsDialog.dismiss();
				m_QuizSettingsDialog = null;
				// set level:
				for (k = 0; k < 3; ++k) {
					if (radioBut[k].isChecked()) {
						m_Prefs.setQuizLevel(k);
						break;
					}
				}
				// set style:
				for (k = 0; k < 2; ++k) {
					if (styleBut[k].isChecked()) {
						m_Prefs.setQuizStyle(k);
						break;
					}
				}
				// set no. questions:
				countString = editCount.getText().toString();
				count = Integer.parseInt(countString);
				m_Prefs.setQuizCount(count);
				// set replay limit:
				m_Prefs.setReplayLimit(checkReplay.isChecked());
				countString = replayCount.getText().toString();
				count = Integer.parseInt(countString);
				m_Prefs.setReplayCount(count);
			}
		});

		// init level:
		int level = m_Prefs.getQuizLevel();
		radioBut[level].setChecked(true);
		// init style:
		int style = m_Prefs.getQuizStyle();
		styleBut[style].setChecked(true);
		// init no. questions:
		editCount.setText(m_Prefs.getQuizCount() + "");
		checkReplay.setChecked(m_Prefs.getReplayLimit());
		// init replay limit:
		replayCount.setText(m_Prefs.getReplayCount() + "");
		if (m_Prefs.getReplayLimit() == false)
			replayCount.setEnabled(false);

		m_QuizSettingsDialog = builder.show();

	}

	private void tutorDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.dialog_tutor, null);
		
		TextView tutorText = (TextView) layout.findViewById(R.id.tutor_text);
		tutorText.setText(R.string.tutor);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle(m_Context.getResources().getText(
				R.string.tutorTitle));
		

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				m_TutorDialog = null;
			}
		});
		m_TutorDialog = builder.show();
	}

	private void creditsDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.dialog_credits, null);

		TextView tutorText = (TextView) layout.findViewById(R.id.credits_text);
		tutorText.setText(R.string.credits);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle(m_Context.getResources().getText(
				R.string.creditsTitle));
		
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				m_CreditsDialog = null;
			}
		});
		m_CreditsDialog = builder.show();
	}
}
