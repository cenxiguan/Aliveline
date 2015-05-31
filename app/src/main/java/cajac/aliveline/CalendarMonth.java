package cajac.aliveline;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chungyuk Takahashi on 5/30/2015.
 */
public class CalendarMonth extends FragmentActivity {
    private boolean undo = false;
    private CaldroidFragment caldroidFragment;

    private Date selectedDate = Calendar.getInstance().getTime();
    private String formattedSelectDate;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_month);


    }


}
