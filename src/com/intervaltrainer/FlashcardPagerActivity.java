package com.intervaltrainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

/**
 * An activity representing a single Flashcard detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a
 * {@link FlashcardListActivity}.
 */
public class FlashcardPagerActivity extends FragmentActivity
{ 
// ActionBarActivity {

	private static final String LOGTAG = "FlashcardsPagerActivity.java";
	private static final String KEY_INDEX = "index";
	
	private FlashcardPagerFragment m_PagerFragment = null;
	private int m_StartPos = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_flashcard_pager);

		// Create the detail fragment and add it to the activity
		m_StartPos = 0;
		Bundle extras = getIntent().getExtras();

		if(extras != null)
			m_StartPos = extras.getInt(FlashcardDetailFragment.ARG_ITEM_ID, 0);
		if(savedInstanceState != null)
			m_StartPos = savedInstanceState.getInt(KEY_INDEX, 0);

		m_PagerFragment = new FlashcardPagerFragment();

		getSupportFragmentManager().beginTransaction()
		.add(R.id.flashcard_pager_container, m_PagerFragment).commit();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if(m_StartPos > 0){
			m_PagerFragment.setPos(m_StartPos);
			m_StartPos = 0;
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		// ViewPager killer:
		// super.onSaveInstanceState(savedInstanceState);
		
		//Log.i(LOGTAG, "onSaveInstanceState");
		savedInstanceState.putInt(KEY_INDEX, m_PagerFragment.getPos());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this, new Intent(this,
					FlashcardListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
