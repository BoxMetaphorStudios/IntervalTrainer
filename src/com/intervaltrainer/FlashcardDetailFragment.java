package com.intervaltrainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A fragment representing a single Flashcard detail screen. This fragment is
 * either contained in a {@link FlashcardListActivity} in two-pane mode (on
 * tablets) or a {@link FlashcardPagerActivity} on handsets.
 */
public class FlashcardDetailFragment extends Fragment {
	private static final String LOGTAG = "FlashcardDetailFragment.java";
	
	public static final String ARG_ITEM_ID = "item_id";

	private InterApp m_App;
	private int m_Pos;
	private Interval m_CurInterval = null;
	
	private Button m_PlayButton;
	private TextView m_TitleField;
	private TextView m_InfoField;
	private ImageView m_IntervalImage;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FlashcardDetailFragment() {
	}
	
	public FlashcardDetailFragment(int pos) {
		m_Pos = pos;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m_App = (InterApp)getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_flashcard_detail,
				container, false);

		//Log.d(LOGTAG, "onCreateView pos: " + m_Pos);
		InterInfo info = m_App.getInterPlay().getInterInfo(m_Pos);
		m_CurInterval = new Interval(Tone.C4, Tone.C4 + m_Pos);
		
		m_PlayButton = (Button)v.findViewById(R.id.play_button);

		m_TitleField = (TextView) v.findViewById(R.id.flashcard_title_label);
		m_TitleField.setText(info.m_Text);

		m_InfoField = (TextView) v.findViewById(R.id.flashcard_detail);
		m_InfoField.setText(info.m_Info);

		m_IntervalImage = (ImageView) v.findViewById(R.id.flashcard_image);
		
		m_IntervalImage.setImageResource(info.m_ImageId);		
		
		m_PlayButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				m_App.getInterPlay().playInterval(m_CurInterval);
			}
		});
		
		m_App.getInterPlay().shutUp();
		
		return v;
	}
}
