package fr.android.infinitelist.sample;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.SimpleAdapter;
import fr.android.infinitelist.InfiniteListView;
import fr.android.infinitelist.interfaces.OnListEnds;

public class InfiniteListViewWithProgressActivity extends Activity {

	private InfiniteListView mInfiniteListView;

	private CopyOnWriteArrayList<HashMap<String, String>> mList;

	private SimpleAdapter mAdapter;

	private Handler mHandler;

	private AtomicBoolean mIsRunning = new AtomicBoolean(false);

	private class Retainer {
		public CopyOnWriteArrayList<HashMap<String, String>> mRetainList;
		public int lastPosition;
		public AtomicBoolean mIsRunning;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refresh_with_textview);
		mHandler = new Handler();

		mInfiniteListView = (InfiniteListView) findViewById(R.id.infiniteListView1);
		mInfiniteListView.setRefreshType(InfiniteListView.REFRESH_TYPE_BAR);

		Object config = getLastNonConfigurationInstance();
		if (config != null) {
			Retainer retain = (Retainer) config;
			mIsRunning = retain.mIsRunning;
			mList = retain.mRetainList;
			refreshList();
			mInfiniteListView.setSelection(retain.lastPosition);
		} else {
			mList = new CopyOnWriteArrayList<HashMap<String, String>>();
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

	@Override
	public Object onRetainNonConfigurationInstance() {
		Retainer retainer = new Retainer();
		retainer.mRetainList = mList;
		retainer.lastPosition = mInfiniteListView.getFirstVisiblePosition();
		retainer.mIsRunning = mIsRunning;
		return retainer;
	}

	private void refreshList(CopyOnWriteArrayList<HashMap<String, String>> pTmpList) {
		mList.addAll(pTmpList);
		refreshList();
	}

	protected void refreshList() {

		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter = new SimpleAdapter(getBaseContext(), mList, R.layout.item, new String[] { "title" }, new int[] { R.id.text1 });
			mInfiniteListView.setAdapter(mAdapter);
		}
		mInfiniteListView.notifyRefreshIsFinished();

	}

	private void updateList() {
		if (!mIsRunning.get()) {
			new Thread(new Runnable() {

				public void run() {
					mIsRunning.set(true);
					final CopyOnWriteArrayList<HashMap<String, String>> tmpList = new CopyOnWriteArrayList<HashMap<String, String>>();
					for (int i = mList.size(); i < mList.size() + 15; i++) {

						waitFor(300);
						if (getListView() != null) {
							getListView().publishProgress(i - mList.size(), 15);
						}
						tmpList.add(newValue(i));
					}

					// FIXME when orientation change the mHandler change too, and in this thread we don't get the new reference to the correct handler
					getHandler().post(new Runnable() {

						public void run() {
							refreshList(tmpList);
						}
					});
					mIsRunning.set(false);
				}
			}).start();
		}

	}

	private void waitFor(int millisecond) {
		try {
			Thread.sleep(millisecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private HashMap<String, String> newValue(int i) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Item N°" + (i + 1));
		Log.i("LOG", map.get("title").toString());
		return map;
	}

	/**
	 * @return the handler
	 */
	public Handler getHandler() {
		Log.d("LOG", mHandler.toString());
		return mHandler;
	}

	public InfiniteListView getListView() {
		return mInfiniteListView;
	}
}