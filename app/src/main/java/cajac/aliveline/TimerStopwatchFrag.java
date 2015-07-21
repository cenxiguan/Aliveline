package cajac.aliveline;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Christine on 6/4/2015.
 * The stopwatch portion of the Timer Fragment.
 */
public class TimerStopwatchFrag extends Fragment implements View.OnClickListener {

    Button start, reset, edit, submit;
    private Chronometer chronometer;
    private static final int REQUEST_INT = 2;
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
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();
                start.setText("START");

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                pickTime a = new pickTime();
                a.show(ft, "pickTime");
                a.setTargetFragment(this, REQUEST_INT);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == REQUEST_INT) {
            long timerTime = data.getLongExtra("TIMER_TIME", 0);
            Log.e("", "Timer time: " + timerTime);
            chronometer.setBase(SystemClock.elapsedRealtime() - timerTime);
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        }
    }
}
