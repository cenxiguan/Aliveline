package cajac.aliveline;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Jonathan Maeda on 6/21/2015.
 */
public class CalendarDatePicker extends DialogFragment {
    private Date mDate;
    public static final int RESULT_DATE = 0;
    public static final String DATE = "DATE";
    private AlertDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a Calendar to get the year, month, and day
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDate = CalendarDay.today().getDate();

        DateSettings dateSettings = new DateSettings(getActivity()) {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                sendResult(RESULT_DATE);
            }
        };
        //DatePickerDialog dialog = new DatePickerDialog(getActivity(), dateSettings, year, month, day);
        //dialog.setTitle("Pick Date");
        //dialog.getDatePicker().setCalendarViewShown(false);
        //dialog.getDatePicker().setSpinnersShown(true);
        //dialog.setCanceledOnTouchOutside(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.date_picker, null);
        builder.setView(view);

        DatePicker datePicker = (DatePicker)view.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            public void onDateChanged(DatePicker view, int year, int month,
                                      int day) {
                // Translate year, month, day into a Date object using a calendar
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                mDate = new GregorianCalendar(year, month, day, hour, minute)
                        .getTime();
                // Update argument to preserve selected value on rotation
            }
        });
        dialog = builder.create();
        dialog.setTitle("Pick Date");
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    private final void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            Log.e("CalDatePicker", "TargetFragment is null");
            return;
        }
        Intent i = new Intent();
        i.putExtra("DATE", mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(),
                resultCode, i);
    }
}
