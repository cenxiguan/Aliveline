package cajac.aliveline;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Christine on 6/1/2015.
 * Fragment for the third tab: TIMER
 * layout file in fragment_timer.xml
 */
public class TimerFragment extends Fragment implements View.OnClickListener {

    AlarmManager mgr;
    Boolean running;
    Button edit;
    Calendar calendar;
    private Chronometer chronometer;
    DatabaseHelper dbh;
    ImageButton start, reset;
    private static final int REQUEST_INT = 2;
    Intent i;
    List<Todo> todaysList;
    ListView list;
    long timeWhenStopped = 0, startTimeBetweenEditTimeCancels = 0, timeBetweenEditTimeCancels, timeUntilEditCancel;
    PendingIntent pi;
    TextView theTodoTitle;
    Todo clickedTodo, previousTodo;
    View view;

    private HoloCircularProgressBar mProgressBar;
    private ObjectAnimator progressBarAnimator = ObjectAnimator.ofFloat(mProgressBar, "progress", 0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        timeUntilEditCancel = System.currentTimeMillis();
        view = inflater.inflate(R.layout.frag_timer_stopwatch, container, false);
        mProgressBar = (HoloCircularProgressBar) view.findViewById(R.id.timer_circle);
//        mProgressBar.setProgressBackgroundColor(R.color.white);
        mProgressBar.setProgressColor(R.color.selected);
        mProgressBar.setProgress(0);
        populateListView();
        registerClickCallback();
        setUpAlarmManager();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chronometer = (Chronometer) getView().findViewById(R.id.chronometer);
        start = ((ImageButton) getView().findViewById(R.id.start_button));
        reset =  ((ImageButton) getView().findViewById(R.id.reset_button));
        edit =  ((Button) getView().findViewById(R.id.edit_button));
        theTodoTitle = ((TextView) getView().findViewById(R.id.title_of_todo));

        if(todaysList.size() > 0) {
            setUpTimer();
            start.setOnClickListener(this);
            reset.setOnClickListener(this);
            edit.setOnClickListener(this);
        } else {
            start.setOnClickListener(this);
            start.setEnabled(false);
            reset.setOnClickListener(this);
            reset.setEnabled(false);
            edit.setOnClickListener(this);
            edit.setEnabled(false);
        }

        running = false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.start_button:
                if (!running) {
                    chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    chronometer.start();
                    animate(mProgressBar, new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationCancel(final Animator animation) {}

                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            if (running)
                                animate(mProgressBar, this);
                        }

                        @Override
                        public void onAnimationRepeat(final Animator animation) {}

                        @Override
                        public void onAnimationStart(final Animator animation) {}
                    });
                    running = true;
                    startTimeBetweenEditTimeCancels = 0;
                    timeUntilEditCancel = System.currentTimeMillis();
                    start.setBackgroundResource(R.drawable.pause);

                    //record the time and update the visuals
                    sendTimeToDatabase();
                    populateListView();

                } else {
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();
                    progressBarAnimator.cancel();
                    running = false;
                    startTimeBetweenEditTimeCancels = 0;
                    timeUntilEditCancel = System.currentTimeMillis();
                    start.setBackgroundResource(R.drawable.play);

                    //record the time and update the visuals
                    sendTimeToDatabase();
                    populateListView();
                }
                break;
            case R.id.reset_button:
                if (running){
                    timeUntilEditCancel = System.currentTimeMillis();
                    chronometer.stop();
                    progressBarAnimator.cancel();
                    running = false;
                    start.setBackgroundResource(R.drawable.play);
                }

                startTimeBetweenEditTimeCancels = 0;
                timeUntilEditCancel = System.currentTimeMillis();
                chronometer.setBase(SystemClock.elapsedRealtime());
                animate(mProgressBar, null);
                timeWhenStopped = 0;

                //record the time and update the visuals
                sendTimeToDatabase();
                populateListView();
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
            startTimeBetweenEditTimeCancels = 0;
            timeUntilEditCancel = System.currentTimeMillis();

            //record the time and update the visuals
            sendTimeToDatabase();
            populateListView();
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

            //record the time and update the visuals
            sendTimeToDatabase();
            populateListView();
        }
    }

    private List<Todo> getListTodos(){
        dbh = new DatabaseHelper(getActivity());
        todaysList = new ArrayList<Todo>();
        todaysList = dbh.getAllToDosByDay(new Date());
        return todaysList;
    }

    private void populateListView(){
        //get list of todos
        todaysList = new ArrayList<Todo>();
        todaysList = getListTodos();

        //Build Adapter
        ArrayAdapter<Todo> adapter = new TodoListAdapter();

        list = (ListView) view.findViewById(R.id.listView1);
        list.setAdapter(adapter);
    }

    private void registerClickCallback(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                clickedTodo = todaysList.get(position);
                String todoTitle = clickedTodo.getTitle();
                theTodoTitle.setText(todoTitle);

                //Stop timer running
                chronometer.stop();
                running = false;
                start.setBackgroundResource(R.drawable.play);

                //send time in timer to database
                sendTimeToDatabase();

                //set timer base to 2do's completed time
                long timerTime = (long) (clickedTodo.getTimeCompleted()) * 60000;
                chronometer.setBase(SystemClock.elapsedRealtime() - timerTime);
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                startTimeBetweenEditTimeCancels = 0;
                timeUntilEditCancel = System.currentTimeMillis();

                //repopulate list view
                populateListView();

                //change previous 2do for next time
                previousTodo = clickedTodo;
            }
        });
    }

    private void setUpAlarmManager(){
        calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        mgr=(AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        i=new Intent(getActivity(), AlarmReceiver.class);
        pi=PendingIntent.getActivity(getActivity(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
        mgr.cancel(pi);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    private void setUpTimer(){
        chronometer.setBase(SystemClock.elapsedRealtime() - todaysList.get(0).getTimeCompleted());
        theTodoTitle.setText(todaysList.get(0).getTitle());
        previousTodo = todaysList.get(0);
    }

    private void animate(final HoloCircularProgressBar progressBar, final Animator.AnimatorListener listener) {
        final float progress = ( Math.max(SystemClock.elapsedRealtime() - chronometer.getBase() - 700, 0)
                / 60000f) % 1f;

        progressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
        progressBarAnimator.setDuration(1);

        progressBarAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(final Animator animation) {}

            @Override
            public void onAnimationEnd(final Animator animation) {
                progressBar.setProgress(progress);
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {}

            @Override
            public void onAnimationStart(final Animator animation) {}
        });
        if (listener != null)
            progressBarAnimator.addListener(listener);
        progressBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                progressBar.setProgress((Float) animation.getAnimatedValue());
            }
        });
        progressBarAnimator.start();
    }

    public int convertToInt(String s){
        int i;
        try{
            i = Integer.parseInt(s);
        } catch(NumberFormatException nfe) {
            return 0;
        }

        return i;
    }

    public String convertToHoursAndMinutes(long timeInMin){
        int hours = (int) timeInMin / 60;
        int minutes = (int) timeInMin % 60;
        if(minutes < 10){
            return "" + hours + ":0" + minutes;
        } else {
            return "" + hours + ":" + minutes;
        }
    }

    public void sendTimeToDatabase(){
        //send time in timer to database
        String todoDate = dbh.getTodoDate(previousTodo, new Date());
        String[] toUpdate = todoDate.split("\\s+");
        long todoDateId = convertToInt(toUpdate[1]);
        long todoId = (long) previousTodo.getId();
        long dateId = dbh.getDateID(new Date());
        int lock = convertToInt(toUpdate[13]);
        String theRequiredTime = toUpdate[8];
        String theCompletedTime = convertToHoursAndMinutes((SystemClock.elapsedRealtime() - chronometer.getBase())/ (60000));
        dbh.updateTodoDate(todoDateId, todoId, dateId, lock, theRequiredTime, theCompletedTime);
    }

    public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent){
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
            running = false;
            startTimeBetweenEditTimeCancels = 0;
            timeUntilEditCancel = System.currentTimeMillis();
            start.setBackgroundResource(R.drawable.play);

            //record the time and update the visuals
            sendTimeToDatabase();
            populateListView();
        }
    }

    private class TodoListAdapter extends ArrayAdapter<Todo> {
        public TodoListAdapter(){
            super(getActivity(),R.layout.todo_list_item,todaysList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            //make sure working with view
            View itemView = convertView;
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.todo_list_item, parent, false);
            }

            //the 2do we're working with
            Todo currentTodo = todaysList.get(position);

            //fill the view
            TextView title = (TextView) itemView.findViewById(R.id.todo_title);
            TextView dueDate = (TextView) itemView.findViewById(R.id.todo_due_date);
            TextView timeDone = (TextView) itemView.findViewById(R.id.todo_time_done);

            title.setText(currentTodo.getTitle());
            dueDate.setText("Due: " + currentTodo.getDueDateString());
            timeDone.setText("Time: " + convertToHoursAndMinutes(currentTodo.getTimeCompleted()) + "/" + convertToHoursAndMinutes(currentTodo.getTimeRequired()));

            return itemView;
        }

        public String convertToHoursAndMinutes(int timeInMin){
            int hours = timeInMin / 60;
            int minutes = timeInMin % 60;
            if(minutes < 10){
                return "" + hours + ":0" + minutes;
            } else {
                return "" + hours + ":" + minutes;
            }
        }
    }
}
