package cajac.aliveline;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by Jonathan Maeda on 6/21/2015.
 */
public class CalendarDatePicker extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DateSettings dateSettings = new DateSettings(getActivity());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), dateSettings, year, month, day);
        dialog.setTitle("Pick Date");
        dialog.getDatePicker().setCalendarViewShown(false);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
