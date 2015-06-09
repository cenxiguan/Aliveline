package cajac.aliveline;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

/**
 * Created by Christine on 6/4/2015.
 * The stopwatch portion of the Timer Fragment.
 */
public class TimerStopwatchFrag extends Fragment implements View.OnClickListener {

    private Chronometer chronometer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_timer_stopwatch, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chronometer = (Chronometer) getView().findViewById(R.id.chronometer);
        ((Button) getView().findViewById(R.id.start_button)).setOnClickListener(this);
        ((Button) getView().findViewById(R.id.stop_button)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.start_button:
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;
            case R.id.stop_button:
                chronometer.stop();
                break;
        }
    }
}
