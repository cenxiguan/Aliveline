package cajac.aliveline.decorators;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cajac.aliveline.R;

/**
 * Created by Chungyuk Takahashi on 8/21/2015.
 */
public class SelectedOvertime implements DayViewDecorator {

    private Set<Date> dateSet;
    private final Drawable highlightDrawable;
    private Context context;
    private int width;
    private int length;

    public SelectedOvertime(Context context, int width, int length) {
        this.context = context;
        dateSet = new HashSet<>();
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
        final int selectedColor = context.getResources().getColor(R.color.calendar_background);
        final int unselectedColor = context.getResources().getColor(R.color.calendar_background);

        CircleOutline selected = new CircleOutline(width, length, color, selectedColor);
        CircleOutline unselected = new CircleOutline(width, length, color, unselectedColor);

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] {android.R.attr.state_checked}, selected);
        drawable.addState(new int[] {android.R.attr.state_pressed}, selected);
        drawable.addState(new int[]{-android.R.attr.state_enabled}, unselected);

        return drawable;
    }

    public void addDate(Date date) {
        dateSet.add(date);
    }

    public void removeDate(Date date) {
        dateSet.remove(date);
    }

    public boolean contains(Date date) {
        return dateSet.contains(date);
    }

    public int size() { return dateSet.size(); }

}
