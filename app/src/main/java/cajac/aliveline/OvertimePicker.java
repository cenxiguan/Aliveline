package cajac.aliveline;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cajac.aliveline.decorators.DayOutOfMonth;
import cajac.aliveline.decorators.SelectedOvertime;

/**
 * Created by Chungyuk Takahashi on 8/21/2015.
 */

public class OvertimePicker extends DialogFragment {
    private double[] overtime;
    private TextView overtimeText;

    Intent data;

    private MaterialCalendarView calendarView;
    private Date dueDate;
    private Date mDate;
    public static final int RESULT_DATE = 1;
    public static final String SELECTED_DATE = "SELECTED_DATE";
    private DayOutOfMonth dayOutOfMonth = new DayOutOfMonth();
    private SelectedOvertime selectedOvertime;
    private List<Date> overtimeDates;
    private final int REQUEST_DATE = 0;

    public OvertimePicker(double[] overtime, Intent data) {
        this.overtime = overtime;
        this.data = data;
        dueDate = CalendarDay.from(Integer.parseInt(data.getStringExtra("YEAR")),
                Integer.parseInt(data.getStringExtra("MONTH")),
                Integer.parseInt(data.getStringExtra("DAY"))).getDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);
        cal.add(Calendar.MONTH, -1);
        this.dueDate = cal.getTime();
        this.overtimeDates = new LinkedList<Date>();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.overtime_picker, null);

        overtimeText = (TextView) view.findViewById(R.id.overtime);

        mDate = CalendarDay.today().getDate();
        calendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(new OnDateClickListener());
        calendarView.setOnMonthChangedListener(new OnMonthClickListener());
        calendarView.setTitleOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                CalendarDatePicker cdp = new CalendarDatePicker(mDate);
                cdp.setTargetFragment(OvertimePicker.this, REQUEST_DATE);
                cdp.show(ft, "CalendarDatePicker");
            }
        });
        calendarView.setSelectedDate(mDate);
        calendarView.setSelectionColor(getActivity().getResources().getColor(R.color.calendar_background));
        int screenWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getActivity().getResources().getDisplayMetrics().heightPixels;
        int tileWidth = (int) (screenWidth / (28 / 3.0));
        int tileLength = (int) (screenHeight * (5.0/100));
        selectedOvertime = new SelectedOvertime(getActivity(), tileWidth, tileLength);
        calendarView.setTileSize(tileWidth, tileLength);
        calendarView.setMinimumDate(CalendarDay.today());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);
        cal.add(Calendar.DATE, -1);
        Date maxDate = cal.getTime();
        calendarView.setMaximumDate(maxDate);
        calendarView.addDecorators(
                dayOutOfMonth,
                selectedOvertime
        );

        builder.setView(view);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                sendResult(RESULT_DATE);
                addOvertime();
                double[] copy = new double[overtime.length - 1];
                for(int i = 0; i < copy.length; i++) { copy[i] = overtime[i]; }
                Log.w("OP_copy", Arrays.toString(copy));
                DatabaseHelper dh = new DatabaseHelper(getActivity());
                dh.createToDo(createToDo(), copy);
            }
        });

        Dialog dialog = builder.create();
        dialog.setTitle("Select Overtime");
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private class OnDateClickListener implements OnDateChangedListener {
        @Override
        public void onDateChanged(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date) {
            mDate = date.getDate();
            sameDate();
//            if (selectedOvertime.contains(mDate))
//                selectedOvertime.removeDate(mDate);
//            else
//                selectedOvertime.addDate(mDate);
//            widget.invalidateDecorators();
//            Log.w("OverPicker", "over: " + Arrays.toString(overtime));
//            if (selectedOvertime.size() > 0)
//                overtimeText.setText(String.format("%.2f", overtime[overtime.length-1] / selectedOvertime.size()));
////                overtimeText.setText(String.valueOf((int) (overtime / selectedOvertime.size() * 100) / 100) );
//            else
//                overtimeText.setText("___");
        }

        public void sameDate() {
            if (selectedOvertime.contains(mDate)) {
                selectedOvertime.removeDate(mDate);
                overtimeDates.remove(mDate);
            } else {
                selectedOvertime.addDate(mDate);
                overtimeDates.add(mDate);
            }
            calendarView.invalidateDecorators();

            if (selectedOvertime.size() > 0)
//                overtimeText.setText(String.valueOf((int) (overtime / selectedOvertime.size() * 100) / 100) );
                overtimeText.setText(String.format("%.2f", overtime[overtime.length-1] / selectedOvertime.size()));
            else
                overtimeText.setText("___");
        }
    }

    private class OnMonthClickListener implements OnMonthChangedListener {
        @Override
        public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
            dayOutOfMonth.setMonth(date.getMonth());
            widget.removeDecorator(dayOutOfMonth);
            widget.addDecorator(dayOutOfMonth);
            widget.invalidateDecorators();
        }
    }
    
    private void addOvertime() {
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DATE, -1);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        Calendar cal2 = Calendar.getInstance();
        for (Date d : overtimeDates) {
            cal2.setTime(d);
            cal2.set(Calendar.HOUR_OF_DAY, 0);
            cal2.set(Calendar.MINUTE, 0);
            cal2.set(Calendar.SECOND, 0);
            Log.w("OP_addOver", d.toString() + "t: " + (int) (cal2.getTimeInMillis() - cal1.getTimeInMillis()) + ", " + ((int) (cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (24 * 60 * 60 * 1000)));
            overtime[(int)(cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (24 * 60 * 60 * 1000) ] += overtime[overtime.length-1] / overtimeDates.size();
        }
    }

    private Todo createToDo() {
        Todo todo = new Todo();
        Log.w("OP-title", data.getStringExtra("TITLE"));
        todo.setTitle(data.getStringExtra("TITLE"));
        todo.setDueDate(dueDate);
        Log.w("OP-estTime", data.getStringExtra("EST_TIME"));
        todo.setEstimatedTime(data.getStringExtra("EST_TIME"));
        todo.setStartTime(data.getStringExtra("EST_TIME"));
        todo.setRemainingTime(data.getStringExtra("EST_TIME"));
        Log.w("OP-timeUsage", "" + data.getIntExtra("TIME_USAGE", 2));
        todo.setTimeUsage(data.getIntExtra("TIME_USAGE", 2));
        Log.w("OP-workdays", data.getStringExtra("WORK_DAYS"));
        todo.setLocks(data.getStringExtra("WORK_DAYS"));
        return todo;
    }

    private final void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            Log.e("OvertimePicker", "TargetFragment is null");
            return;
        }
        Intent i = new Intent();

//        i.putExtra(SELECTED_DATE, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == CalendarDatePicker.RESULT_DATE) {
            Date date = (Date) data.getSerializableExtra(CalendarDatePicker.DATE);
            mDate = date;
            calendarView.setSelectedDate(mDate);
            calendarView.invalidateDecorators();
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // safety check
//        if (getDialog() == null) {
//            return;
//        }
//
//        int dialogWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
//        int dialogHeight = getActivity().getResources().getDisplayMetrics().heightPixels;
//
//        getDialog().getWindow().setLayout((int) (dialogWidth * .9), (int) (dialogHeight * .7));
//
//        if(isScreenLarge())
//            calendarView.setTileSize((int) (dialogWidth * 0.4 / 9));
//    }

    public boolean isScreenLarge() {
        final int screenSize = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
}

