package fr.android.infinitelist;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.opengl.Visibility;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import fr.android.infinitelist.interfaces.OnListEnds;

public class InfiniteListView extends ListView implements OnScrollListener {

	/**
	 * mScrollListener OnScrollListener défini par l'utilisateur
	 */
	private OnScrollListener mScrollListener;

	private OnListEnds mOnListEnds;

	private AtomicBoolean mIsLoading;

	private View mFooter;

	/**
	 * Constructeur d'une instance de InfiniteListView
	 * 
	 * @param pContext
	 */
	public InfiniteListView(Context pContext) {
		this(pContext, null);
	}

	/**
	 * Constructeur d'une instance de InfiniteListView
	 * 
	 * @param pContext
	 * @param pAttrs
	 */
	public InfiniteListView(Context pContext, AttributeSet pAttrs) {
		this(pContext, pAttrs, -1);
	}

	/**
	 * Constructeur d'une instance de InfiniteListView
	 * 
	 * @param pContext
	 * @param pAttrs
	 * @param pDefStyle
	 */
	public InfiniteListView(Context pContext, AttributeSet pAttrs, int pDefStyle) {
		super(pContext, pAttrs, pDefStyle);
		mIsLoading = new AtomicBoolean(false);
		setOnScrollListener(this);
	}

	/** {@inheritDoc} */
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// The user must provide an OnListEnds object AND the list should not be empty
		if (mOnListEnds != null && getAdapter() != null && !getAdapter().isEmpty()) {
			if (!mIsLoading.get() && mOnListEnds.isEnd(firstVisibleItem, visibleItemCount, totalItemCount)) {
				Log.d("ACT", "load refresh");
				mIsLoading.set(true);
				mOnListEnds.onStartRefresh();

				if (mFooter != null) {
					// show the footer
					mFooter.setVisibility(View.VISIBLE);
				}
			}
		}

		// if a scrollListener is provider, use it!
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}

	}

	/**
	 * notify the list that the loading is finished
	 */
	public void notifyRefreshIsFinished() {
		if(mFooter != null){
			mFooter.setVisibility(View.GONE);
		}
		mIsLoading.set(false);
	}
	
	@Override
	public void setAdapter(ListAdapter pAdapter) {
		// the footer is not initialized AND a refresh view is provided
		if (mFooter == null && mOnListEnds.getRefreshView() != null) {
			mFooter = mOnListEnds.getRefreshView();
			addFooterView(mFooter);
			mFooter.setVisibility(View.GONE);
		}
		super.setAdapter(pAdapter);
	}

	/** {@inheritDoc} */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}

	}

	/**
	 * Retourne la valeur du champ scrollListener de InfiniteListView.
	 * 
	 * @return OnScrollListener - la valeur du champ
	 */
	public OnScrollListener getScrollListener() {
		return mScrollListener;
	}

	/**
	 * Positionne la valeur du champ scrollListener de InfiniteListView.
	 * 
	 * @param scrollListener
	 *            OnScrollListener - la valeur à positionner
	 */
	public void setScrollListener(OnScrollListener scrollListener) {
		mScrollListener = scrollListener;
	}

	/**
	 * Retourne la valeur du champ listEnds de InfiniteListView.
	 * 
	 * @return OnListEnds - la valeur du champ
	 */
	public OnListEnds getOnListEnds() {
		return mOnListEnds;
	}

	/**
	 * Positionne la valeur du champ listEnds de InfiniteListView.
	 * 
	 * @param listEnds
	 *            OnListEnds - la valeur à positionner
	 */
	public void setOnListEnds(OnListEnds listEnds) {
		mOnListEnds = listEnds;
	}

}
