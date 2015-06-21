package cajac.aliveline;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * Created by Jonathan Maeda on 6/21/2015.
 */
public class DateSettings implements DatePickerDialog.OnDateSetListener{
    Context context;
    public DateSettings(Context context){
        this.context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
        Toast.makeText(context, "Selected date: " + monthOfYear + " / " + dayOfMonth + " / " + year, Toast.LENGTH_LONG).show();
    }
}
