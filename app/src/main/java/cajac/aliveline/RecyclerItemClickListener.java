package cajac.aliveline;

/**
 * Created by Jonathan Maeda on 7/2/2015.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            if (view.getChildAdapterPosition(childView) == -1) {
                view.scrollBy(1, 0);
                onInterceptTouchEvent(view, e);
            } else {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
        }
        return false;
    }

    @Override public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){ }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }
}
