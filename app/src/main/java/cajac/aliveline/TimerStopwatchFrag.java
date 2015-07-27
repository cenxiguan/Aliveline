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
import android.widget.ImageButton;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Christine on 6/4/2015.
 * The stopwatch portion of the Timer Fragment.
 */
public class TimerStopwatchFrag extends Fragment implements View.OnClickListener {

    Boolean running;
    Button edit;
    private Chronometer chronometer;
    ImageButton start, reset;
    private static final int REQUEST_INT = 2;
    long timeWhenStopped = 0, startTimeBetweenEditTimeCancels, timeBetweenEditTimeCancels, timeUntilEditCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_timer_stopwatch, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chronometer = (Chronometer) getView().findViewById(R.id.chronometer);
        start = ((ImageButton) getView().findViewById(R.id.start_button));
        reset =  ((ImageButton) getView().findViewById(R.id.reset_button));
        edit =  ((Button) getView().findViewById(R.id.edit_button));

        chronometer.setBase(SystemClock.elapsedRealtime());
        start.setOnClickListener(this);
        reset.setOnClickListener(this);
        edit.setOnClickListener(this);

        running = false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.start_button:
                if (!running) {
                    chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    chronometer.start();
                    running = true;
                    startTimeBetweenEditTimeCancels = 0;
                    timeUntilEditCancel = System.currentTimeMillis();
                    start.setBackgroundResource(R.drawable.pause);

                } else {
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();
                    running = false;
                    startTimeBetweenEditTimeCancels = 0;
                    timeUntilEditCancel = System.currentTimeMillis();
                    start.setBackgroundResource(R.drawable.play);

                }
                break;
            case R.id.reset_button:
                if (running){
                    timeUntilEditCancel = System.currentTimeMillis();
                    chronometer.stop();
                    running = false;
                    start.setBackgroundResource(R.drawable.play);
                }

                startTimeBetweenEditTimeCancels = 0;
                timeUntilEditCancel = System.currentTimeMillis();
                chronometer.setBase(SystemClock.elapsedRealtime());
                timeWhenStopped = 0;
                break;

            case R.id.edit_button:
                if(running) {
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();
                    running = false;
                    start.setBackgroundResource(R.drawable.play);
                    timeUntilEditCancel = System.currentTimeMillis();
                }

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                pickTime a = new pickTime();
                a.show(ft, "pickTime");
                a.setTargetFragment(this, REQUEST_INT);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (resultCode == REQUEST_INT && requestCode == REQUEST_INT) {
            long timerTime = data.getLongExtra("TIMER_TIME", 0);
            chronometer.setBase(SystemClock.elapsedRealtime() - timerTime);
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
            timeUntilEditCancel = System.currentTimeMillis();
        } else {
            long timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();

            if(startTimeBetweenEditTimeCancels == 0){
                timeBetweenEditTimeCancels = System.currentTimeMillis() - timeUntilEditCancel;
            } else {
                timeBetweenEditTimeCancels = System.currentTimeMillis() - startTimeBetweenEditTimeCancels;
            }

            long timerTime = timeElapsed - timeBetweenEditTimeCancels;

            chronometer.setBase(SystemClock.elapsedRealtime() - timerTime);
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
            startTimeBetweenEditTimeCancels = System.currentTimeMillis();
        }
    }
}
