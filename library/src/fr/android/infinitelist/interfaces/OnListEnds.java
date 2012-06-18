package fr.android.infinitelist.interfaces;


/**
 * Interface to use with an InfiniteListView
 * 
 * @author elongeau
 * 
 */
public interface OnListEnds {
	/**
	 * define the rule used to load <i>onStartRefresh</i>
	 * 
	 * @param firstVisibleItem
	 *            the id of the first visible item in the list
	 * @param visibleItemCount
	 *            number of visible items
	 * @param totalItemCount
	 *            total number of items in the list
	 * @return
	 */
	boolean isEnd(int firstVisibleItem, int visibleItemCount, int totalItemCount);

	/**
	 * What to do when the list is near the end ?
	 */
	void onStartRefresh();
}
