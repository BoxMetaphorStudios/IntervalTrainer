package com.intervaltrainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuizFragment extends Fragment {
	private static final String LOGTAG = "QuizFragment.java";
	// saved instance bundle keys:
	private static final String BKEY_QUESTIONCOUNT = "com.intervaltrainer.questioncount";
	private static final String BKEY_CORRECTCOUNT = "com.intervaltrainer.correctcount";
	private static final String BKEY_TYPES = "com.intervaltrainer.types";
	private static final String BKEY_CORRECTINDEX = "com.intervaltrainer.correctindex";
	private static final String BKEY_USERSELECT = "com.intervaltrainer.userselect";
	private static final String BKEY_REPLAYCOUNT = "com.intervaltrainer.replaycount";
	private static final String BKEY_INFOOPEN = "com.intervaltrainer.infoopen";
	private static final String BKEY_RESULTSOPEN = "com.intervaltrainer.resultsopen";
	// current interval:
	private static final String BKEY_INTERLO = "com.intervaltrainer.interlo";
	private static final String BKEY_INTERHI = "com.intervaltrainer.interhi";

	//
	private Context m_Context;
	private InterApp m_App;
	private InterPlay m_InterPlay;
	private Prefs m_Prefs;
	// UI:
	private Button m_ButPlay;
	private Button m_ButSubmit;
	private RadioGroup m_GroupAnswers;
	private RadioButton[] m_RadioBut = new RadioButton[4];
	private TextView m_TextCount;
	private AlertDialog m_InfoDialog = null;
	private AlertDialog m_ResultsDialog = null;
	// Quiz values:
	private int m_Level = 1; // 0, 1 or 2 - will be set by Prefs
	private int m_MaxQuestions = 4; // length of quiz - will be set by Prefs
	private boolean m_ReplayLimit = false; // will be set by Prefs
	private int m_MaxReplays = 3; // will be set by Prefs
	// Counters
	private int m_QuestionCount = 0;
	private int m_CorrectCount = 0;
	private int m_ReplayCount = 0;
	// Current state:
	private int[] m_Types = new int[4]; // holds answer interval types
	private int m_CorrectIndex = 0; // correct answer index
	private int m_CorrectType = 0; // correct interval type
	private Interval m_CurInterval = null; // current playable interval

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public QuizFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m_App = (InterApp) getActivity().getApplication();

		// if(savedInstanceState != null)
		// restoreInstanceState(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.activity_quiz);

		View v = inflater.inflate(R.layout.fragment_quiz, container, false);

		m_Context = getActivity(); // this;
		// m_App = (InterApp) getApplication();
		m_InterPlay = m_App.getInterPlay();
		m_Prefs = new Prefs(m_Context);

		// get preferences:
		m_Level = m_Prefs.getQuizLevel();
		m_MaxQuestions = m_Prefs.getQuizCount();
		m_ReplayLimit = m_Prefs.getReplayLimit();
		m_MaxReplays = m_Prefs.getReplayCount();

		m_ButPlay = (Button) v.findViewById(R.id.quiz_play_button);
		m_ButSubmit = (Button) v.findViewById(R.id.quiz_submit_button);
		m_TextCount = (TextView) v.findViewById(R.id.textCount);
		m_GroupAnswers = (RadioGroup) v.findViewById(R.id.rg_answers);

		m_RadioBut[0] = (RadioButton) v.findViewById(R.id.rb1);
		m_RadioBut[1] = (RadioButton) v.findViewById(R.id.rb2);
		m_RadioBut[2] = (RadioButton) v.findViewById(R.id.rb3);
		m_RadioBut[3] = (RadioButton) v.findViewById(R.id.rb4);

		if (savedInstanceState == null) {
			startNewQuiz();
			setNewState();
			updateUI();
			m_InterPlay.playInterval(m_CurInterval);
		} else
			restoreInstanceState(savedInstanceState);

		m_ButPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_ReplayLimit == false || m_ReplayCount < m_MaxReplays) {
					m_InterPlay.playInterval(m_CurInterval);
					++m_ReplayCount;
				} else
					Toast.makeText(
							m_Context,
							m_Context.getResources().getText(
									R.string.replay_prompt), Toast.LENGTH_LONG)
							.show();
			}
		});

		m_ButSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int checked = getCheckedIndex();
				// nothing checked:
				if (checked == -1) { // warn
					Toast.makeText(
							m_Context,
							m_Context.getResources().getText(
									R.string.select_prompt), Toast.LENGTH_LONG)
							.show();
				} else { // update quiz:
					++m_QuestionCount;
					if (checked == m_CorrectIndex) // if the user got the right
													// answer
						++m_CorrectCount;
					InterInfo info = m_InterPlay
							.getInterInfo(m_Types[m_CorrectIndex]);
					infoDialog(checked == m_CorrectIndex, info.m_Text);
				}
			}
		});

		return v;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onPause() {
		m_InterPlay.shutUp();
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Log.d(LOGTAG, "onSaveInstanceState()");

		savedInstanceState.putInt(BKEY_QUESTIONCOUNT, m_QuestionCount);
		savedInstanceState.putInt(BKEY_CORRECTCOUNT, m_CorrectCount);
		savedInstanceState.putIntArray(BKEY_TYPES, m_Types);
		savedInstanceState.putInt(BKEY_CORRECTINDEX, m_CorrectIndex);
		savedInstanceState.putInt(BKEY_USERSELECT, getCheckedIndex());
		savedInstanceState.putBoolean(BKEY_INFOOPEN,
				m_InfoDialog == null ? false : true);
		savedInstanceState.putBoolean(BKEY_RESULTSOPEN,
				m_ResultsDialog == null ? false : true);
		savedInstanceState.putInt(BKEY_REPLAYCOUNT, m_ReplayCount);
		if (m_CurInterval != null) {
			savedInstanceState.putInt(BKEY_INTERLO, m_CurInterval.m_LoToneId);
			savedInstanceState.putInt(BKEY_INTERHI, m_CurInterval.m_HiToneId);
			// Log.d(LOGTAG, "lo:" + m_CurInterval.m_LoToneId + " hi:" +
			// m_CurInterval.m_HiToneId);
		} else {
			savedInstanceState.putInt(BKEY_INTERLO, -1);
			savedInstanceState.putInt(BKEY_INTERHI, -1);
		}

		if (m_InfoDialog != null) {
			m_InfoDialog.dismiss();
			m_InfoDialog = null;
		}

		if (m_ResultsDialog != null) {
			m_ResultsDialog.dismiss();
			m_ResultsDialog = null;
		}

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	private void restoreInstanceState(Bundle savedInstanceState) {
		int userSelect, interLo, interHi;
		boolean infoOpen, resultsOpen;

		// Log.d(LOGTAG, "restoreInstanceState()");

		m_QuestionCount = savedInstanceState.getInt(BKEY_QUESTIONCOUNT);
		m_CorrectCount = savedInstanceState.getInt(BKEY_CORRECTCOUNT);
		m_Types = savedInstanceState.getIntArray(BKEY_TYPES);
		m_CorrectIndex = savedInstanceState.getInt(BKEY_CORRECTINDEX);
		m_ReplayCount = savedInstanceState.getInt(BKEY_REPLAYCOUNT);
		userSelect = savedInstanceState.getInt(BKEY_USERSELECT);
		infoOpen = savedInstanceState.getBoolean(BKEY_INFOOPEN);
		resultsOpen = savedInstanceState.getBoolean(BKEY_RESULTSOPEN);
		interLo = savedInstanceState.getInt(BKEY_INTERLO);
		interHi = savedInstanceState.getInt(BKEY_INTERHI);

		m_CorrectType = m_Types[m_CorrectIndex];
		m_CurInterval = (interLo != -1) ? new Interval(interLo, interHi) : null;

		// if(m_CurInterval != null)
		// Log.d(LOGTAG, "lo:" + m_CurInterval.m_LoToneId + " hi:" +
		// m_CurInterval.m_HiToneId);

		updateUI();

		if (userSelect != -1) {
			m_RadioBut[userSelect].setChecked(true); // restore check
			if (infoOpen == true) {
				InterInfo info = m_InterPlay
						.getInterInfo(m_Types[m_CorrectIndex]);
				infoDialog(userSelect == m_CorrectIndex, info.m_Text); // open
																		// info
																		// dialog
			}
		}

		if (resultsOpen == true || m_QuestionCount == m_MaxQuestions)
			resultsDialog(); // open results dialog
	}

	private int getCheckedIndex() {
		for (int k = 0; k < 4; ++k) {
			if (m_RadioBut[k].isChecked() == true)
				return k;
		}

		return -1;
	}

	private void startNewQuiz() {
		m_QuestionCount = 0;
		m_CorrectCount = 0;
	}

	private void setNewState() {
		m_CorrectIndex = m_InterPlay.getRandomTypes(m_Types, m_Level);
		m_CorrectType = m_Types[m_CorrectIndex];
		m_CurInterval = m_InterPlay.getRandomInterval(m_CorrectType);
		m_ReplayCount = 0;

		// Log.d(LOGTAG, "setNewState() correct type: " + m_CorrectType);
	}

	private void updateUI() {
		int k;
		InterInfo info;

		m_GroupAnswers.clearCheck();

		for (k = 0; k < 4; ++k) {
			info = m_InterPlay.getInterInfo(m_Types[k]);
			m_RadioBut[k].setText(info.m_Text);
		}

		m_TextCount.setText((m_QuestionCount + 1) + "/" + m_MaxQuestions);

	}

	private void finishInfo() {
		if (m_QuestionCount < m_MaxQuestions) {
			setNewState();
			updateUI();
			m_InterPlay.playInterval(m_CurInterval);
		} else
			resultsDialog();
	}

	private void infoDialog(boolean success, String text) {
		// Log.d(LOGTAG, "infoDialog");

		LayoutInflater inflater = LayoutInflater.from(m_Context); // this);
		View layout = inflater.inflate(R.layout.dialog_info, null);

		final ImageView successIcon = (ImageView) layout
				.findViewById(R.id.infoSuccessIcon);
		final TextView intervalText = (TextView) layout
				.findViewById(R.id.infoIntervalText);
		final Button butNext = (Button) layout.findViewById(R.id.infoNextBut);
		final Button butReplay = (Button) layout
				.findViewById(R.id.infoReplayBut);

		AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);
		builder.setView(layout);

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				m_InfoDialog = null;
				finishInfo();
			}
		});

		butNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_InfoDialog != null) {
					m_InfoDialog.dismiss();
					m_InfoDialog = null;
				}
				finishInfo();
			}
		});

		butReplay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_InterPlay.playInterval(m_CurInterval);
			}
		});

		successIcon
				.setImageResource(success == true ? R.drawable.question_right
						: R.drawable.question_wrong);
		intervalText.setText(text);
		// show replay button if wrong answer:
		butReplay.setVisibility(success == false ? View.VISIBLE : View.GONE);
		if (success == true) {
			m_InterPlay.shutUp();
			MediaPlayer mp = MediaPlayer.create(m_Context, R.raw.right_ding);
			mp.start();
		} else {
			m_InterPlay.shutUp();

		}
		m_InfoDialog = builder.show();
	}

	private void resultsDialog() {
		// Log.d(LOGTAG, "resultsDialog");

		if (m_InfoDialog != null) {
			m_InfoDialog.dismiss();
		}
		LayoutInflater inflater = LayoutInflater.from(m_Context);
		View layout = inflater.inflate(R.layout.dialog_results, null);

		final ImageView allRight = (ImageView) layout
				.findViewById(R.id.allRight);
		final TextView score = (TextView) layout
				.findViewById(R.id.resultsScore);
		final Button butNew = (Button) layout.findViewById(R.id.resultsNewBut);
		final Button butHome = (Button) layout.findViewById(R.id.resultsHome);
		allRight.setVisibility(View.GONE);

		AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);
		builder.setView(layout);
		builder.setTitle(m_Context.getResources().getText(R.string.score_title));

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				m_ResultsDialog = null;
				getActivity().finish();
			}
		});

		butNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_ResultsDialog.dismiss();
				m_ResultsDialog = null;
				startNewQuiz();
				setNewState();
				updateUI();
				m_InterPlay.playInterval(m_CurInterval);
			}
		});
		butHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(m_Context, HomeScreen.class);
				startActivityForResult(i, 0);
			}
		});

		score.setText(m_CorrectCount + "/" + m_MaxQuestions);
		if (m_CorrectCount == m_MaxQuestions) {
			allRight.setVisibility(View.VISIBLE);
		} else
			allRight.setVisibility(View.GONE);

		m_ResultsDialog = builder.show();

	}
}
