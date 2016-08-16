package com.intervaltrainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;

/**
 * An activity representing a list of Flashcards. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link FlashcardPagerActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.

 * This activity also implements the required
 * {@link FlashcardListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class FlashcardListActivity extends FragmentActivity 
	implements FlashcardListFragment.Callbacks, 
	FlashcardPagerFragment.PagerCallbacks
{
	private static final String LOGTAG = "FlashcardsListActivity.java";
	private static final String KEY_INDEX = "index";
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private FlashcardListFragment m_ListFragment;
	private FlashcardPagerFragment m_PagerFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_flashcard_list);

		m_ListFragment = ((FlashcardListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.flashcard_list));
		m_PagerFragment = ((FlashcardPagerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.flashcard_pager_fragment));
	
		// Force one-pane:
		// m_PagerFragment = null;
				
		if(m_PagerFragment != null){
			// Pager fragment will be present only in the
			// large-screen layouts. If fragment is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;
			int pos = 0;
					
			if(savedInstanceState != null){
				pos = savedInstanceState.getInt(KEY_INDEX, 0);
				//Log.d(LOGTAG, "onCreate - saved pos: " + pos);
			}
			
			m_PagerFragment.setPos(pos);

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			m_ListFragment.setActivateOnItemClick(true);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		// ViewPager killer:
		// super.onSaveInstanceState(savedInstanceState);
		
		if(m_PagerFragment != null){
			int pos = m_PagerFragment.getPos();
			//Log.d(LOGTAG, "onSaveInstanceState pos: " + pos);
			savedInstanceState.putInt(KEY_INDEX, pos);
		}
	}

	@Override
	public void onPause() 
	{
		((InterApp)getApplication()).getInterPlay().shutUp();

		super.onPause();
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();
	}
	
	/**
	 * Callback method from {@link FlashcardListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int pos) 
	{
		if (mTwoPane) 
			m_PagerFragment.setPos(pos);
		else 
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent i = new Intent(this,
					FlashcardPagerActivity.class);
			i.putExtra(FlashcardDetailFragment.ARG_ITEM_ID, pos);
			startActivity(i);
		}
	}

	@Override
	public void onPageSelected(int pos) {
		if(mTwoPane)
			m_ListFragment.getListView().setItemChecked(pos, true); //   .setSelection(pos);
	}
}
