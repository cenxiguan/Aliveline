package cajac.aliveline;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import java.util.Date;

/**
 * Created by Chungyuk Takahashi on 6/25/2015.
 */
public class CalendarDialogFragment extends DialogFragment {
    private MaterialCalendarView calendarView;
    private Date mDate;
    public static final int RESULT_DATE = 1;
    public static final String SELECTED_DATE = "SELECTED_DATE";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.calendar_dialog, null);

        mDate = CalendarDay.today().getDate();
        calendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(new OnDateClickListener());
        calendarView.setSelectedDate(mDate);

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
        }

        public void sameDate() {}
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

        if(isScreenLarge()) {
            calendarView.setTileSize((int) (dialogWidth * 0.4 / 9));
        } else {
            calendarView.setTileSize((int) (dialogWidth * 0.9 / 9));
        }
    }

    public boolean isScreenLarge() {
        final int screenSize = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
}
