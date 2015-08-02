package cajac.aliveline.decorators;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;
import java.util.Set;

import cajac.aliveline.DatabaseHelper;
import cajac.aliveline.R;

/**
 * Highlight Saturdays and Sundays with a background
 */
public class Deadline implements DayViewDecorator {

    Set<Date> dateSet;
    DatabaseHelper dbh;

    private final Drawable highlightDrawable;
    private Context context;
    private int width;
    private int length;

    public Deadline(Context context, int width, int length) {
        this.context = context;
        dbh = new DatabaseHelper(context);
        dateSet = dbh.getAllDeadlines();
        this.width = width;
        this.length = length;
        highlightDrawable = generateBackgroundDrawable();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dateSet.contains(day.getDate());
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(highlightDrawable);
    }

    private Drawable generateBackgroundDrawable() {
        final int color = context.getResources().getColor(R.color.deadline);
        final int selectedColor = context.getResources().getColor(R.color.selected);
        final int unselectedColor = context.getResources().getColor(R.color.calendar_background);

        CurvedTriangle selected = new CurvedTriangle(width, length, color, selectedColor);
        CurvedTriangle unselected = new CurvedTriangle(width, length, color, unselectedColor);

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] {android.R.attr.state_checked}, selected);
        drawable.addState(new int[] {android.R.attr.state_pressed}, selected);
        drawable.addState(new int[]{-android.R.attr.state_enabled}, unselected);

        return drawable;
    }

    public void updateDeadlines() {
        dateSet = dbh.getAllDeadlines();
    }
}
