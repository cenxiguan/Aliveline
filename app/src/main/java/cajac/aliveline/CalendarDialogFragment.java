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

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Date;

import cajac.aliveline.decorators.DayOutOfMonth;
import cajac.aliveline.decorators.OneDayDecorator;

/**
 * Created by Chungyuk Takahashi on 6/25/2015.
 */
public class CalendarDialogFragment extends DialogFragment {
    private MaterialCalendarView calendarView;
    private Date mDate;
    public static final int RESULT_DATE = 1;
    public static final String SELECTED_DATE = "SELECTED_DATE";
    private OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private DayOutOfMonth dayOutOfMonth = new DayOutOfMonth();
    private final int REQUEST_DATE = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.calendar_dialog, null);

        mDate = CalendarDay.today().getDate();
        calendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(new OnDateClickListener());
        calendarView.setOnMonthChangedListener(new OnMonthClickListener());
        calendarView.setTitleOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                CalendarDatePicker cdp = new CalendarDatePicker(mDate);
                cdp.setTargetFragment(CalendarDialogFragment.this, REQUEST_DATE);
                cdp.show(ft, "CalendarDatePicker");
            }
        });
        calendarView.setSelectedDate(mDate);
        calendarView.setSelectionColor(getActivity().getResources().getColor(R.color.selected));
        int screenWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getActivity().getResources().getDisplayMetrics().heightPixels;
        int tileWidth = screenWidth / (28 / 3);
        int tileLength = (int)(screenHeight * (5.0/105));
        calendarView.setTileSize(tileWidth, tileLength);
        oneDayDecorator.setDate(mDate);
        calendarView.addDecorators(
                dayOutOfMonth,
                oneDayDecorator
        );

        builder.setView(view);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(RESULT_DATE);
            }
        });

        Dialog dialog = builder.create();
        dialog.setTitle("Due Date");
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    private class OnDateClickListener implements OnDateChangedListener {
        @Override
        public void onDateChanged(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date) {
            mDate = date.getDate();
            oneDayDecorator.setDate(mDate);
            widget.invalidateDecorators();
        }

        public void sameDate() {}
    }

    private class OnMonthClickListener implements OnMonthChangedListener {
        @Override
        public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
            dayOutOfMonth.setMonth(date.getMonth());
            widget.removeDecorator(oneDayDecorator);
            widget.addDecorator(oneDayDecorator);
            widget.invalidateDecorators();
        }
    }

    private final void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            Log.e("CalDialogFrag", "TargetFragment is null");
            return;
        }
        Intent i = new Intent();
        i.putExtra(SELECTED_DATE, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == CalendarDatePicker.RESULT_DATE) {
            Date date = (Date) data.getSerializableExtra(CalendarDatePicker.DATE);
            mDate = date;
            calendarView.setSelectedDate(mDate);
            oneDayDecorator.setDate(mDate);
            calendarView.invalidateDecorators();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        int dialogWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        int dialogHeight = getActivity().getResources().getDisplayMetrics().heightPixels;

        getDialog().getWindow().setLayout((int) (dialogWidth * .9), (int) (dialogHeight * .7));

        if(isScreenLarge())
            calendarView.setTileSize((int) (dialogWidth * 0.4 / 9));
    }

    public boolean isScreenLarge() {
        final int screenSize = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
}
