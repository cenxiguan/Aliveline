package cajac.aliveline;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Christine on 6/1/2015.
 * Fragment for the third tab: TIMER
 * layout file in fragment_timer.xml
 */
public class TimerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // add nested fragments dynamically
        Fragment timerStopwatchFrag = new TimerStopwatchFrag();
        FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
        transaction1.add(R.id.child_timer, timerStopwatchFrag).commit();
        Fragment timerTodoFrag = new TimerTodoFrag();
        FragmentTransaction transaction2 = getChildFragmentManager().beginTransaction();
        transaction2.add(R.id.child_todo, timerTodoFrag).commit();
    }
}
