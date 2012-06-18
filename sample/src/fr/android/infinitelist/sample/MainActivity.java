package fr.android.infinitelist.sample;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends ListActivity {
	private SimpleAdapter mAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ArrayList<HashMap<String, String>> vList = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Refresh with icon and text");
		vList.add(map);

		map = new HashMap<String, String>();
		map.put("title", "Refresh with progress bar");
		vList.add(map);

		mAdapter = new SimpleAdapter(getBaseContext(), vList, R.layout.item, new String[] { "title" }, new int[] { R.id.text1 });
		setListAdapter(mAdapter);
	}

	@Override
	protected void onListItemClick(ListView pL, View pV, int pPosition, long pId) {
		super.onListItemClick(pL, pV, pPosition, pId);
		Intent intent = null;
		switch (pPosition) {
		case 0:
			intent = new Intent(this, InfiniteListViewWithRefreshTextActivity.class);
			break;
		case 1:
			intent = new Intent(this, InfiniteListViewWithProgressActivity.class);
			break;
		default:
			break;
		}
		
		if(intent != null){
			startActivityForResult(intent, -1);
		}
	}
}