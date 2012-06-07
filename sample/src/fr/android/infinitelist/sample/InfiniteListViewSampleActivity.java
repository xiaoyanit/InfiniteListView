package fr.android.infinitelist.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fr.android.infinitelist.InfiniteListView;
import fr.android.infinitelist.interfaces.OnListEnds;

public class InfiniteListViewSampleActivity extends Activity {

	private InfiniteListView mInfiniteListView;

	private ArrayList<HashMap<String, String>> mList;

	private SimpleAdapter mAdapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			loadSomeData();
		};
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mList = new ArrayList<HashMap<String, String>>();

		mInfiniteListView = (InfiniteListView) findViewById(R.id.infiniteListView1);

		updateList();
		mInfiniteListView.setOnListEnds(new OnListEnds() {

			public void onStartRefresh() {
				updateList();
			}

			public boolean isEnd(int pFirstVisibleItem, int pVisibleItemCount, int pTotalItemCount) {
				if (pFirstVisibleItem + pVisibleItemCount + 2 > pTotalItemCount) {
					// the end of the list is near when there's no more 2 items available
					return true;
				}
				return false;
			}

			public View getRefreshView() {
				TextView emptyView = new TextView(getBaseContext());
				emptyView.setText("loading data...");
				emptyView.setTextColor(Color.RED);
				emptyView.setTextSize(25);
				return emptyView;
			}
		});
	}

	protected void loadSomeData() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter = new SimpleAdapter(getBaseContext(), mList, R.layout.item, new String[] { "title" }, new int[] { R.id.text1 });
			mInfiniteListView.setAdapter(mAdapter);
		}

		mInfiniteListView.notifyRefreshIsFinished();
		Log.d("ACT", "notify adapter");

	}

	private void updateList() {
		new Thread(new Runnable() {

			public void run() {

				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("ACT", "update list");
				int lastArticle = 0;
				if (mInfiniteListView.getAdapter() != null) {
					lastArticle = mInfiniteListView.getAdapter().getCount() - 1;
				}

				for (int i = lastArticle; i <= lastArticle + 15; i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("title", "Item N°" + i);
					mList.add(map);
				}

				mHandler.sendEmptyMessage(-1);
			}
		}).start();

	}
}