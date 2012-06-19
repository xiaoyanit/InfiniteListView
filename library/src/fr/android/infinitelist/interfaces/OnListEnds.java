package fr.android.infinitelist.interfaces;

/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <longeaue@gmail.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return 
 * elongeau
 * ----------------------------------------------------------------------------
 */
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
