package cajac.aliveline;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Chungyuk Takahashi on 6/8/2015.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private LinearLayoutManager mLinearLayoutManager;
    private int visibleThreshold = 7;
    private int loopPoint = 60;
    private int listSize;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    public EndlessScrollListener(LinearLayoutManager linearLayoutManager, int listSize) {
        this.listSize = listSize;
        mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition() % listSize;
        totalItemCount = mLinearLayoutManager.getItemCount();
        visibleItemCount = mLinearLayoutManager.getChildCount();

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

}
