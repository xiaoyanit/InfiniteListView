package fr.android.infinitelist;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.android.infinitelist.interfaces.OnListEnds;

public class InfiniteListView extends ListView implements OnScrollListener {

	/**
	 * <b>by default</b>, if your theme is dark
	 */
	public static final int REFRESH_ICON_DARK = 1;

	/**
	 * if your theme is light
	 */
	public static final int REFRESH_ICON_LIGHT = 2;

	/**
	 * used when the configuration change to save the status of the refresh view
	 */
	private static final String IS_LOADING = "fr.android.infinitelist.InfiniteListView.IS_LOADING";

	/**
	 * used when the configuration change to save the super-state
	 */
	private static final String SUPER_STATE = "fr.android.infinitelist.InfiniteListView.SUPER_STATE";

	/**
	 * mScrollListener OnScrollListener define by developer
	 */
	private OnScrollListener mScrollListener;

	/**
	 * OnListEnds define by developer
	 */
	private OnListEnds mOnListEnds;

	/**
	 * thread-safe flag to know if the refresh view is visible
	 */
	private AtomicBoolean mIsLoading;

	/**
	 * THE refresh View
	 */
	// TODO evolution : allow the developer to define a custom view => in this case disable the animation
	private RelativeLayout mRefreshView;

	/**
	 * the text of the refresh view
	 */
	private TextView mRefreshText;

	/**
	 * an inflater to find the XML of the refresh view
	 */
	private LayoutInflater mInflater;

	/**
	 * the loading icon of the refresh view
	 */
	private ImageView mRefreshIcon;

	/**
	 * the animation used to animate the loading icon
	 */
	private RotateAnimation mFlipAnimation;

	/**
	 * InfiniteListView constructor
	 * 
	 * @param pContext
	 */
	public InfiniteListView(Context pContext) {
		this(pContext, null);
	}

	/**
	 * InfiniteListView constructor
	 * 
	 * @param pContext
	 * @param pAttrs
	 */
	public InfiniteListView(Context pContext, AttributeSet pAttrs) {
		this(pContext, pAttrs, -1);
	}

	/**
	 * InfiniteListView constructor
	 * 
	 * @param pContext
	 * @param pAttrs
	 * @param pDefStyle
	 */
	public InfiniteListView(Context pContext, AttributeSet pAttrs, int pDefStyle) {
		super(pContext, pAttrs, pDefStyle);
		init(pContext);
	}

	/**
	 * init some views and animations
	 * 
	 * @param pContext
	 */
	private void init(Context pContext) {
		// init step, so no loading
		mIsLoading = new AtomicBoolean(false);
		
		// define this ListView as the scroll listener
		setOnScrollListener(this);
		
		// init the views
		mInflater = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRefreshView = (RelativeLayout) mInflater.inflate(R.layout.refresh_view, this, false);
		mRefreshText = (TextView) mRefreshView.findViewById(R.id.refresh_label);
		mRefreshIcon = (ImageView) mRefreshView.findViewById(R.id.refresh_image);

		addFooterView(mRefreshView);
		mRefreshView.setVisibility(View.GONE);

		mFlipAnimation = (RotateAnimation) AnimationUtils.loadAnimation(pContext, R.animator.anim_refresh);
		mFlipAnimation.setRepeatCount(Animation.INFINITE);
	}

	/** {@inheritDoc} */
	@Override
	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();

		bundle.putBoolean(IS_LOADING, mIsLoading.get()); // save the state of the refresh view
		bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState()); // save the super state
		return bundle;
	}

	/** {@inheritDoc} */
	@Override
	public void onRestoreInstanceState(Parcelable pState) {
		if (pState instanceof Bundle) {
			Bundle bundle = (Bundle) pState;
			pState = bundle.getParcelable(SUPER_STATE); // if we receive a superstate, we should restore it

			mIsLoading.set(bundle.getBoolean(IS_LOADING));
			if (mIsLoading.get()) { // if the refresh view was displayed before config change, it should be displayed again
				showRefreshView();
			}
		}

		super.onRestoreInstanceState(pState); // restore the super state
	}

	/** {@inheritDoc} */
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// The user must provide an OnListEnds object AND the list should not be empty
		if (mOnListEnds != null && getAdapter() != null && !getAdapter().isEmpty()) {

			if (!mIsLoading.get() && mOnListEnds.isEnd(firstVisibleItem, visibleItemCount, totalItemCount)) {
				// show the refresh view IF it's not the case AND the end is near
				showRefreshView();

				// start the user action
				mOnListEnds.onStartRefresh();
			}
		}

		// if a scrollListener is provided, use it!
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}

	}

	/**
	 * show the refresh view and start the animation of the icon
	 */
	private void showRefreshView() {
		mIsLoading.set(true);

		addFooterView(mRefreshView);
		mRefreshView.setVisibility(View.VISIBLE);
		mRefreshIcon.clearAnimation();
		mRefreshIcon.startAnimation(mFlipAnimation);
	}

	/**
	 * notify the list that the loading is finished
	 */
	public void notifyRefreshIsFinished() {
		mRefreshView.setVisibility(GONE);
		mRefreshIcon.clearAnimation();
		removeFooterView(mRefreshView);
		mIsLoading.set(false);
	}

	/** {@inheritDoc} */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}

	}

	/**
	 * get the TextView of the refresh view
	 * 
	 * @return
	 */
	public TextView getRefreshText() {
		return mRefreshText;
	}

	/**
	 * set the the style of the icon of the refresh view : DARK or LIGHT
	 * 
	 * @param pStyle
	 */
	public void setRefreshIconStyle(int pStyle) {
		switch (pStyle) {
			case REFRESH_ICON_DARK:
				mRefreshIcon.setImageResource(R.drawable.ic_refresh_dark);
				break;

			case REFRESH_ICON_LIGHT:
				mRefreshIcon.setImageResource(R.drawable.ic_refresh_light);

			default:
				break;
		}
	}

	/**
	 * get the OnScrollListener define by user
	 * 
	 * @return OnScrollListener
	 */
	public OnScrollListener getScrollListener() {
		return mScrollListener;
	}

	/**
	 * set the OnScrollListener
	 * 
	 * @param scrollListener OnScrollListener - the new value
	 */
	public void setScrollListener(OnScrollListener scrollListener) {
		mScrollListener = scrollListener;
	}

	/**
	 * get the OnListEnds define by user
	 * 
	 * @return OnListEnds
	 */
	public OnListEnds getOnListEnds() {
		return mOnListEnds;
	}

	/**
	 * set the value for the OnListEnds object
	 * 
	 * @param listEnds OnListEnds - the new value
	 */
	public void setOnListEnds(OnListEnds listEnds) {
		mOnListEnds = listEnds;
	}

}
