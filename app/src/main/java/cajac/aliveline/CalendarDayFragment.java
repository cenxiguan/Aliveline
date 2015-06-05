package cajac.aliveline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chungyuk Takahashi on 6/1/2015.
 */
public class CalendarDayFragment extends CalendarDate {
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

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate() {
        this.selectedDate = selectedDate;
    }

}
