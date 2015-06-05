package cajac.aliveline;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chungyuk Takahashi on 6/1/2015.
 */
public class CalendarDayFragment extends CalendarDate {
    static CalendarDate fragmentChangeListener;
    private FragmentManager fragmentManager;

    private Date selectedDate;

    private View view;

    public CalendarDayFragment() {
        selectedDate = Calendar.getInstance().getTime();
    }

    public CalendarDayFragment(Date date) {
        selectedDate = date;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_calendar_day, container, false);

        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(selectedDate.toString());


        return view;
    }

    public void backPressed() {
        CalendarMonthFragment dayView = new CalendarMonthFragment(selectedDate);
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.calendar_frame, dayView).commit();
        Switch dayOrMonth = (Switch) getParentFragment().getView().findViewById(R.id.day_month_switch);
        dayOrMonth.setChecked(true);
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate() {
        this.selectedDate = selectedDate;
    }

}
