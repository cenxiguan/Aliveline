package cajac.aliveline;

import android.util.Log;
import android.widget.AbsListView;

import it.sephiroth.android.library.widget.AbsHListView;

/**
 * Created by Chungyuk Takahashi on 6/8/2015.
 */
public abstract class EndlessScrollListener implements AbsHListView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 30;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        Log.w("Endless", "first " + String.valueOf(firstVisibleItem));
        Log.w("Endless", "visible " + String.valueOf(visibleItemCount));
        Log.w("Endless", "total " + String.valueOf(totalItemCount));
        Log.w("Endless", "loading " + loading);
        Log.w("Endless", "current "+ currentPage);
        Log.w("Endless", "previous "+ previousTotalItemCount);
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            Log.w("Endless", "less than");
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.loading = true; }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            Log.w("Endless", "greater than");

            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + visibleThreshold)) {
            onLoadMore(currentPage + 1, totalItemCount);
            loading = true;
        }
    }



    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);


    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }
}
