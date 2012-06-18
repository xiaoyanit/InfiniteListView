package fr.android.infinitelist.sample;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.android.infinitelist.sample.InfiniteListViewWithRefreshTextActivity.FillTheList;

public class Retainer {

	public CopyOnWriteArrayList<HashMap<String, String>> retainedList;
	public FillTheList mTask;

}
