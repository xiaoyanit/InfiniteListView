#InfiniteListView
The **InfiniteListView** is a custom **ListView** for **Android** app.
It's allow to automatically load an action when the user scrolls to the end of the list.

#How to use it ?
Just read the sample provide in this repository

## Add an InfiniteListView in your layout

       <fr.android.infinitelist.InfiniteListView
        android:id="@+id/infiniteListView1"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="vertical" />


## Use it in your code
#### Init your InfiniteListView 
	mInfiniteListView = (InfiniteListView) findViewById(R.id.infiniteListView1);


#### Define an OnListEnds listener 
	mInfiniteListView.setOnListEnds(new OnListEnds() {

		/**
		 * Fires when the isEnd method return true
		 */
		public void onStartRefresh() {
			updateList();
		}

		/**
		 * Define the rule to use to fire onStartRefresh
		 */
		public boolean isEnd(int pFirstVisibleItem, int pVisibleItemCount, int pTotalItemCount) {
			if (pFirstVisibleItem + pVisibleItemCount + 5 > pTotalItemCount) {
				// the end of the list is near when there's no more 2 items available
				return true;
			}
			return false;
		}
	});

#### To notify the InfiniteListView that the loading is finished
	mInfiniteListView.notifyRefreshIsFinished();
