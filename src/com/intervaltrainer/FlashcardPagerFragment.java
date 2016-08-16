package com.intervaltrainer;

import com.intervaltrainer.FlashcardListFragment.Callbacks;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Flashcard detail screen. This fragment is
 * either contained in a {@link FlashcardListActivity} in two-pane mode (on
 * tablets) or a {@link FlashcardPagerActivity} on handsets.
 */
public class FlashcardPagerFragment extends Fragment {
	private static final String LOGTAG = "FlashcardPagerFragment.java";
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	
	public interface PagerCallbacks {
		/**
		 * Callback for when a page has been selected.
		 */
		public void onPageSelected(int pos);
	}
	
	private PagerCallbacks mCallbacks = sDummyCallbacks;
	
	private static PagerCallbacks sDummyCallbacks = new PagerCallbacks() {
		@Override
		public void onPageSelected(int pos) {
		}
	};
	
	private class FlashcardPagerAdapter extends FragmentStatePagerAdapter {
        public FlashcardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new FlashcardDetailFragment(position);
        }

        @Override
        public int getCount() {
        	return m_App.getInterPlay().getInterInfoArray().length;
        }
    }
	
	private InterApp m_App;
	private ViewPager m_ViewPager = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FlashcardPagerFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		m_App = (InterApp)getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_flashcard_pager,
				container, false);
		
		m_ViewPager = ((ViewPager) rootView.findViewById(R.id.flashcard_pager));
		
		m_ViewPager.setAdapter(new FlashcardPagerAdapter(getActivity().getSupportFragmentManager()));
		
		m_ViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				mCallbacks.onPageSelected(position);
				m_App.getInterPlay().shutUp();
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		
		return rootView;
	}

	/*
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
    public void onViewCreated(View view, Bundle saved) 
	{
        super.onViewCreated(view, saved);
    }
	*/
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if(activity instanceof PagerCallbacks) {
			mCallbacks = (PagerCallbacks) activity;
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}
	
	public void setPos(int pos)
	{
		if(m_ViewPager != null){
			m_ViewPager.setCurrentItem(pos);
			//Log.d(LOGTAG, "setPos pos: " + pos);
		}
		//else
			//Log.d(LOGTAG, "setPos ViewPager null ");
	}
	
	public int getPos()
	{
		int pos = 0;
		
		if(m_ViewPager != null)
			pos = m_ViewPager.getCurrentItem();
		
		return pos;
	}
}
