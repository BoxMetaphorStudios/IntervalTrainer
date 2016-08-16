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
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Quiz2Fragment extends Fragment {
	private static final String LOGTAG = "Quiz2Fragment.java";
	// saved instance bundle keys:
	private static final String BKEY_QUESTIONCOUNT = "com.intervaltrainer.questioncount";
	private static final String BKEY_CORRECTCOUNT = "com.intervaltrainer.correctcount";
	private static final String BKEY_CORRECTTYPE = "com.intervaltrainer.correcttype";
	private static final String BKEY_QUALSELECT = "com.intervaltrainer.qualselect";
	private static final String BKEY_DINTERSELECT = "com.intervaltrainer.dinterselect";
	private static final String BKEY_REPLAYCOUNT = "com.intervaltrainer.replaycount";
	private static final String BKEY_INFOOPEN = "com.intervaltrainer.infoopen";
	private static final String BKEY_RESULTSOPEN = "com.intervaltrainer.resultsopen";
	// current interval:
	private static final String BKEY_INTERLO = "com.intervaltrainer.interlo";
	private static final String BKEY_INTERHI = "com.intervaltrainer.interhi";

	// checkAnswer returns:
	private static final int ANSWER_CORRECT = 0;
	private static final int ANSWER_ENHARMONIC = 1;
	private static final int ANSWER_WRONGINTERVAL = 2;
	private static final int ANSWER_WRONGQUAL = 3;
	private static final int ANSWER_INCOMPLETE = 4;

	public class AnswerEval {
		int m_Result;
		String m_QualText = "";
		String m_DInterText = "";

		public AnswerEval(int result, String qualText, String dinterText) {
			m_Result = result;
			m_QualText = qualText;
			m_DInterText = dinterText;
		}

		public AnswerEval(int result) {
			m_Result = result;
		}
	}

	//
	private Context m_Context;
	private InterApp m_App;
	private InterPlay m_InterPlay;
	private Prefs m_Prefs;
	// UI:
	private Button m_ButPlay;
	private Button m_ButSubmit;
	private RadioGroup m_RGQual;
	private RadioButton[] m_RButQual = new RadioButton[QualInfo.TYPE_COUNT];
	private RadioGroup m_RGDInter;
	private RadioGroup m_RGDInter2;
	private RadioButton[] m_RButDInter = new RadioButton[DInterInfo.TYPE_COUNT];
	//
	private TextView m_TextCount;
	private TextView m_TextSelection;
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
	private int m_CorrectType = 0; // correct interval type
	private Interval m_CurInterval = null; // current playable interval

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public Quiz2Fragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m_App = (InterApp) getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_quiz2, container, false);

		m_Context = getActivity();
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
		m_TextSelection = (TextView) v.findViewById(R.id.textSelection);

		m_RGQual = (RadioGroup) v.findViewById(R.id.rg_qualifiers);
		m_RGQual.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				setSelectionText();
			}
		});

		m_RButQual[QualInfo.MAJOR] = (RadioButton) v
				.findViewById(R.id.rbutMajor);
		m_RButQual[QualInfo.MINOR] = (RadioButton) v
				.findViewById(R.id.rbutMinor);
		m_RButQual[QualInfo.AUG] = (RadioButton) v.findViewById(R.id.rbutAug);
		m_RButQual[QualInfo.DIM] = (RadioButton) v.findViewById(R.id.rbutDim);
		m_RButQual[QualInfo.PERFECT] = (RadioButton) v
				.findViewById(R.id.rbutPerfect);

		m_RGDInter = (RadioGroup) v.findViewById(R.id.rg_intervals);
		m_RGDInter.setOnCheckedChangeListener(m_RGListener);

		m_RGDInter2 = (RadioGroup) v.findViewById(R.id.rg_intervals2);
		m_RGDInter2.setOnCheckedChangeListener(m_RGListener2);

		m_RButDInter[DInterInfo.DUNISON] = (RadioButton) v
				.findViewById(R.id.rbutDU);
		m_RButDInter[DInterInfo.D2ND] = (RadioButton) v
				.findViewById(R.id.rbutD2);
		m_RButDInter[DInterInfo.D3RD] = (RadioButton) v
				.findViewById(R.id.rbutD3);
		m_RButDInter[DInterInfo.D4TH] = (RadioButton) v
				.findViewById(R.id.rbutD4);
		m_RButDInter[DInterInfo.D5TH] = (RadioButton) v
				.findViewById(R.id.rbutD5);
		m_RButDInter[DInterInfo.D6TH] = (RadioButton) v
				.findViewById(R.id.rbutD6);
		m_RButDInter[DInterInfo.D7TH] = (RadioButton) v
				.findViewById(R.id.rbutD7);
		m_RButDInter[DInterInfo.DOCTAVE] = (RadioButton) v
				.findViewById(R.id.rbutDO);
		m_RButDInter[DInterInfo.D9TH] = (RadioButton) v
				.findViewById(R.id.rbutD9);
		m_RButDInter[DInterInfo.D10TH] = (RadioButton) v
				.findViewById(R.id.rbutD10);
		m_RButDInter[DInterInfo.D11TH] = (RadioButton) v
				.findViewById(R.id.rbutD11);
		m_RButDInter[DInterInfo.D12TH] = (RadioButton) v
				.findViewById(R.id.rbutD12);

		hideIntervals();

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
				submitAnswer(true);
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
		savedInstanceState.putInt(BKEY_CORRECTTYPE, m_CorrectType);
		savedInstanceState.putInt(BKEY_QUALSELECT, getQualCheckedIndex());
		savedInstanceState.putInt(BKEY_DINTERSELECT, getDInterCheckedIndex());
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
		int qualSelect, dinterSelect, interLo, interHi;
		boolean infoOpen, resultsOpen;

		// Log.d(LOGTAG, "restoreInstanceState()");

		m_QuestionCount = savedInstanceState.getInt(BKEY_QUESTIONCOUNT);
		m_CorrectCount = savedInstanceState.getInt(BKEY_CORRECTCOUNT);
		m_CorrectType = savedInstanceState.getInt(BKEY_CORRECTTYPE);
		m_ReplayCount = savedInstanceState.getInt(BKEY_REPLAYCOUNT);
		qualSelect = savedInstanceState.getInt(BKEY_QUALSELECT);
		dinterSelect = savedInstanceState.getInt(BKEY_DINTERSELECT);
		infoOpen = savedInstanceState.getBoolean(BKEY_INFOOPEN);
		resultsOpen = savedInstanceState.getBoolean(BKEY_RESULTSOPEN);
		interLo = savedInstanceState.getInt(BKEY_INTERLO);
		interHi = savedInstanceState.getInt(BKEY_INTERHI);

		m_CurInterval = (interLo != -1) ? new Interval(interLo, interHi) : null;

		// if(m_CurInterval != null)
		// Log.d(LOGTAG, "lo:" + m_CurInterval.m_LoToneId + " hi:" +
		// m_CurInterval.m_HiToneId);

		updateUI();

		if (qualSelect != -1)
			m_RButQual[qualSelect].setChecked(true); // restore check

		if (dinterSelect != -1)
			m_RButDInter[dinterSelect].setChecked(true); // restore check

		setSelectionText();

		if ((qualSelect != -1 || dinterSelect != -1) && infoOpen == true)
			submitAnswer(false);

		if (resultsOpen == true || m_QuestionCount == m_MaxQuestions)
			resultsDialog(); // open results dialog
	}

	private int getQualCheckedIndex() {
		for (int k = 0; k < QualInfo.TYPE_COUNT; ++k) {
			if (m_RButQual[k].isChecked() == true)
				return k;
		}

		return -1;
	}

	private int getDInterCheckedIndex() {
		for (int k = 0; k < DInterInfo.TYPE_COUNT; ++k) {
			if (m_RButDInter[k].isChecked() == true)
				return k;
		}

		return -1;
	}

	private AnswerEval checkAnswer() {
		int qual, dinter;
		QualInfo qualInfo;
		DInterInfo dinterInfo;
		int interType, interSType, qualType, qualSType;
		int mod, chromaType;

		qual = getQualCheckedIndex();
		dinter = getDInterCheckedIndex();

		if (qual == -1 || dinter == -1)
			return new AnswerEval(ANSWER_INCOMPLETE);

		qualInfo = m_InterPlay.getQualInfo(qual);
		dinterInfo = m_InterPlay.getDInterInfo(dinter);

		interType = dinterInfo.m_DType;
		interSType = dinterInfo.m_SType;
		qualType = qualInfo.m_Type;
		qualSType = qualInfo.m_SType;

		// Log.d(LOGTAG, "checkAnswer - interType:" + interType + "interSType:"
		// + interSType);
		// Log.d(LOGTAG, "qualType:" + qualType + "qualSType:" + qualSType);

		if (interSType == DInterInfo.PURE) {
			if (qualSType == DInterInfo.IMPURE)
				return new AnswerEval(ANSWER_WRONGQUAL, qualInfo.m_Text,
						dinterInfo.m_Text);
			else
				mod = qualInfo.m_Mod;
		} else {
			if (qualType == QualInfo.PERFECT)
				return new AnswerEval(ANSWER_WRONGQUAL, qualInfo.m_Text,
						dinterInfo.m_Text);
			else if (qualType == QualInfo.DIM)
				mod = -2;
			else
				mod = qualInfo.m_Mod;
		}

		chromaType = dinterInfo.m_Type + mod;

		if (chromaType == m_CorrectType) {
			if ((qualType == QualInfo.AUG && interType != DInterInfo.D4TH && interType != DInterInfo.D11TH)
					|| (qualType == QualInfo.DIM
							&& interType != DInterInfo.D5TH && interType != DInterInfo.D12TH))
				return new AnswerEval(ANSWER_ENHARMONIC, qualInfo.m_Text + " "
						+ dinterInfo.m_Text,
						m_InterPlay.getInterInfo(chromaType).m_Text);
			else
				return new AnswerEval(ANSWER_CORRECT);
		} else {
			return new AnswerEval(ANSWER_WRONGINTERVAL);
		}
	}

	private void submitAnswer(boolean warnIncomplete) {
		boolean success = false;
		String text = "";
		String correctText = m_InterPlay.getInterInfo(m_CorrectType).m_Text;
		AnswerEval eval = checkAnswer();

		switch (eval.m_Result) {
		case ANSWER_INCOMPLETE:
			if (warnIncomplete == true) {
				Toast.makeText(
						m_Context,
						m_Context.getResources().getText(
								R.string.select_prompt2), Toast.LENGTH_LONG)
						.show();
			}
			return;
		case ANSWER_WRONGQUAL:
			success = false;
			text = correctText + "\n(" + eval.m_DInterText + " can't be "
					+ eval.m_QualText + "!)";
			break;
		case ANSWER_WRONGINTERVAL:
			success = false;
			text = correctText;
			break;
		case ANSWER_CORRECT:
			++m_CorrectCount;
			success = true;
			text = correctText;
			break;
		case ANSWER_ENHARMONIC:
			++m_CorrectCount;
			success = true;
			text = eval.m_QualText + " \u2192 " + eval.m_DInterText;
			break;
		}

		++m_QuestionCount;

		infoDialog(success, text);
	}

	private OnCheckedChangeListener m_RGListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (getView() != null && checkedId != -1) {
				m_RGDInter2.setOnCheckedChangeListener(null);
				m_RGDInter2.clearCheck();
				setSelectionText();
				m_RGDInter2.setOnCheckedChangeListener(m_RGListener2);
			}
		}
	};

	private OnCheckedChangeListener m_RGListener2 = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (getView() != null && checkedId != -1) {
				m_RGDInter.setOnCheckedChangeListener(null);
				m_RGDInter.clearCheck();
				setSelectionText();
				m_RGDInter.setOnCheckedChangeListener(m_RGListener);
			}
		}
	};

	private void setSelectionText() {
		int qual, dinter;
		String text = "";

		if ((qual = getQualCheckedIndex()) != -1)
			text = text + m_InterPlay.getQualInfo(qual).m_Text + " ";

		if ((dinter = getDInterCheckedIndex()) != -1)
			text = text + m_InterPlay.getDInterInfo(dinter).m_Text;

		m_TextSelection.setText(text);
	}

	private void hideIntervals() {
		int k, start = 0;

		switch (m_Level) {
		case 0:
			start = DInterInfo.D6TH;
			break;
		case 1:
			start = DInterInfo.D9TH;
			break;
		case 2:
			return;
		}

		for (k = start; k < DInterInfo.TYPE_COUNT; ++k)
			m_RButDInter[k].setVisibility(View.GONE);
	}

	private void startNewQuiz() {
		m_QuestionCount = 0;
		m_CorrectCount = 0;
	}

	private void setNewState() {
		m_CorrectType = m_InterPlay.getRandomType(m_Level);
		m_CurInterval = m_InterPlay.getRandomInterval(m_CorrectType);
		m_ReplayCount = 0;

		// Log.d(LOGTAG, "setNewState() correct type: " + m_CorrectType);
	}

	private void updateUI() {
		m_RGQual.clearCheck();
		m_RGDInter.clearCheck();
		// 2014/12/19 D.Slamnig added:
		m_RGDInter2.clearCheck();
		m_TextCount.setText((m_QuestionCount + 1) + "/" + m_MaxQuestions);
		m_TextSelection.setText("");

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
