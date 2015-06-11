package cajac.aliveline;

import android.widget.AbsListView;

import it.sephiroth.android.library.widget.AbsHListView;

/**
 * Created by Chungyuk Takahashi on 6/8/2015.
 */
public abstract class EndlessScrollListener implements AbsHListView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 7;
    private int loopPoint = 60;
    private int listSize;

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(int listSize) {
        this.listSize = listSize;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        firstVisibleItem %= listSize;
        if(loopPoint < firstVisibleItem) {
            loopPoint += listSize;
            if ( (loopPoint - (firstVisibleItem + visibleItemCount)) <= visibleThreshold) {
                addFuture(listSize);
            }else if ( firstVisibleItem - (loopPoint - listSize) <= visibleThreshold ) {
                addPast(listSize);
            }
        }else if (loopPoint > firstVisibleItem) {
            if ( (loopPoint - (firstVisibleItem + visibleItemCount)) <= visibleThreshold) {
                addFuture(totalItemCount);
            }else if ( (firstVisibleItem + listSize) - loopPoint <= visibleThreshold) {
                addPast(listSize);
            }
        }

    }

    private void addFuture(int totalItemCount) {
        loopPoint += visibleThreshold;
        loopPoint %= totalItemCount;
        onLoadMore(true);
    }

    private void addPast(int totalItemCount) {
        loopPoint += totalItemCount - visibleThreshold;
        loopPoint %= totalItemCount;
        onLoadMore(false);
    }

    // Defines the process for actually loading more data based on page
    // false for left or down and true for right or up
    public abstract void onLoadMore(boolean direction);


    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }
}
