package cajac.aliveline;

import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * Created by Chungyuk Takahashi on 6/4/2015.
 */
public abstract class CalendarDate extends Fragment{

    private Date selectedDate;

    public Date getSelectedDate(){
        return selectedDate;
    }

    public void setSelectedDate(Date date){
        selectedDate = date;
    }

}
