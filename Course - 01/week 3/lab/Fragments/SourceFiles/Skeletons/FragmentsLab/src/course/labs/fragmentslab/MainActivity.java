package course.labs.fragmentslab;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements
		FriendsFragment.SelectionListener {

	private static final String TAG = "Lab-Fragments";
	private static final String LAST_POS_RETAINED = "LAST_POS";

	private FriendsFragment mFriendsFragment;
	private FeedFragment mFeedFragment;
	private int mLastPosSelected = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		// If the layout is single-pane, create the FriendsFragment
		// and add it to the Activity

		if (!isInTwoPaneMode()) {

			if (mFriendsFragment == null)
				mFriendsFragment = new FriendsFragment();

			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.fragment_container,
					mFriendsFragment);
			fragmentTransaction.commit();

		} else {

			// Otherwise, save a reference to the FeedFragment for later use
			mFriendsFragment = (FriendsFragment) getFragmentManager()
					.findFragmentById(R.id.friends_frag);
			mFeedFragment = (FeedFragment) getFragmentManager()
					.findFragmentById(R.id.feed_frag);

			int previousSelectedFriend = getLastSelectedPositionFromSavedInstance(savedInstanceState);
			if (previousSelectedFriend > -1)
				onItemSelected(previousSelectedFriend);
		}

	}

	private int getLastSelectedPositionFromSavedInstance(
			Bundle savedInstanceState) {
		if (savedInstanceState == null)
			return -1;

		int lastSelectedFriend = savedInstanceState.getInt(LAST_POS_RETAINED);
		if (lastSelectedFriend > -1)
			return lastSelectedFriend;

		return -1;
	}

	// If there is no fragment_container ID, then the application is in
	// two-pane mode

	private boolean isInTwoPaneMode() {

		return findViewById(R.id.fragment_container) == null;

	}

	// Display selected Twitter feed

	public void onItemSelected(int position) {

		Log.i(TAG, "Entered onItemSelected(" + position + ")");

		// If there is no FeedFragment instance, then create one

		if (mFeedFragment == null)
			mFeedFragment = new FeedFragment();

		// If in single-pane mode, replace single visible Fragment

		if (!isInTwoPaneMode()) {

			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.fragment_container, mFeedFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();

			// execute transaction now
			getFragmentManager().executePendingTransactions();

		}

		// Update Twitter feed display on FriendFragment
		mFeedFragment.updateFeedDisplay(position);
		
		mLastPosSelected = position;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, getClass().getSimpleName()
				+ ":entered onSaveInstanceState()");
		outState.putInt(LAST_POS_RETAINED, mLastPosSelected);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onPause()");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onRestart()");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onResume()");
		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onStart()");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onStop()");
		super.onStop();
	}

}
