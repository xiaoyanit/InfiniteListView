package fr.android.infinitelist.sample;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import fr.android.infinitelist.InfiniteListView;
import fr.android.infinitelist.interfaces.OnListEnds;

/**
 * sample activity to show how works the InfiniteListView
 * 
 * @author elongeau
 */
public class InfiniteListViewWithRefreshTextActivity extends Activity {

	/**
	 * THE InfiniteListView
	 */
	private InfiniteListView mInfiniteListView;

	/**
	 * a thread-safe List to use with a SimpleAdapter
	 */
	private CopyOnWriteArrayList<HashMap<String, String>> mList;

	/**
	 * the list adapter's
	 */
	private SimpleAdapter mAdapter;

	/**
	 * the task used to fill the list with data
	 */
	private FillTheList mTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refresh_with_textview);

		mList = new CopyOnWriteArrayList<HashMap<String, String>>();

		mInfiniteListView = (InfiniteListView) findViewById(R.id.infiniteListView1);

		if (getLastNonConfigurationInstance() != null) { // if the configuration change, we restore some data : the source List and the task
			Retainer retainer = (Retainer) getLastNonConfigurationInstance();
			mList = retainer.retainedList;
			mTask = retainer.mTask;
			mTask.setToNotify(this);
			refreshList(null);
		} else {
			updateList();
		}

		mInfiniteListView.setOnListEnds(new OnListEnds() {

			public void onStartRefresh() {
				updateList();
			}

			public boolean isEnd(int pFirstVisibleItem, int pVisibleItemCount, int pTotalItemCount) {
				if (pFirstVisibleItem + pVisibleItemCount + 5 > pTotalItemCount) {
					// the end of the list is near when there's no more 2 items available
					return true;
				}
				return false;
			}
		});
	}

	/** {@inheritDoc} */
	public Object onRetainNonConfigurationInstance() {
		Retainer retainer = new Retainer();
		retainer.retainedList = mList;
		retainer.mTask = mTask;
		return retainer;
	}

	/**
	 * load the task to feed the list
	 */
	private void updateList() {
		mTask = new FillTheList(this);
		mTask.execute();
	}

	/**
	 * notify the list adapter's with the new list of objects
	 * 
	 * @param pNewListToAdd
	 */
	protected void refreshList(CopyOnWriteArrayList<HashMap<String, String>> pNewListToAdd) {
		if (pNewListToAdd != null) {
			mList.addAll(pNewListToAdd);
		}

		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter = new SimpleAdapter(getBaseContext(), mList, R.layout.item, new String[] { "title" }, new int[] { R.id.text1 });
			mInfiniteListView.setAdapter(mAdapter);
		}

		mInfiniteListView.notifyRefreshIsFinished();
	}

	/**
	 * An AsyncTask used to feed the list with data
	 * 
	 * @author elongeau
	 * 
	 */
	public class FillTheList extends AsyncTask<Void, Integer, CopyOnWriteArrayList<HashMap<String, String>>> {

		/**
		 * when the configuration change, we should notify to the AsyncTask the new Activity to update<br/>
		 * It's not the better way but in this case it's enough
		 */
		private InfiniteListViewWithRefreshTextActivity mToNotify;

		public FillTheList(InfiniteListViewWithRefreshTextActivity pToNotify) {
			mToNotify = pToNotify;
		}

		/** {@inheritDoc} */
		@Override
		protected CopyOnWriteArrayList<HashMap<String, String>> doInBackground(Void... pParams) {
			try {
				Thread.sleep(3000); // wait few seconds to simulate a long data loading
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			int lastArticle = mList.size();
			CopyOnWriteArrayList<HashMap<String, String>> tmpList = new CopyOnWriteArrayList<HashMap<String, String>>();

			// adding data to a list
			for (int i = lastArticle; i <= lastArticle + 15; i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("title", "Item N°" + i);
				tmpList.add(map);
			}

			return tmpList;
		}

		@Override
		protected void onPostExecute(CopyOnWriteArrayList<HashMap<String, String>> pResult) {
			// notify the activity with the new data
			mToNotify.refreshList(pResult);
		}

		/**
		 * set the activity to notify.
		 * 
		 * @param pToNotify
		 *            Activity
		 */
		public void setToNotify(InfiniteListViewWithRefreshTextActivity pToNotify) {
			mToNotify = pToNotify;
		}

	}

}