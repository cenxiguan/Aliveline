package cajac.aliveline;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
    Button start, reset, edit;
    long timeWhenStopped = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_timer_stopwatch, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chronometer = (Chronometer) getView().findViewById(R.id.chronometer);
        start = ((Button) getView().findViewById(R.id.start_button));
        reset =  ((Button) getView().findViewById(R.id.reset_button));
        edit =  ((Button) getView().findViewById(R.id.edit_button));

        start.setOnClickListener(this);
        reset.setOnClickListener(this);
        edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button b = (Button)v;
        String buttonText = b.getText().toString();

        switch(v.getId()) {
            case R.id.start_button:
                if(buttonText.equals("START")){
                    chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    chronometer.start();
                    b.setText("STOP");
                } else {
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();
                    b.setText("START");
                }
                break;
            case R.id.reset_button:
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                timeWhenStopped = 0;

                start.setText("START");
                break;
            case R.id.edit_button:
                chronometer.stop();
                start.setText("START");

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                pickTime a = new pickTime();
                a.show(ft, "pickTime");
                //chronometer.setBase(SystemClock.elapsedRealtime());
                break;
        }
    }
}
