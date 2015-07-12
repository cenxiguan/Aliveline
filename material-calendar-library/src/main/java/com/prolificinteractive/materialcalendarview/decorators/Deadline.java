package com.prolificinteractive.materialcalendarview.decorators;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Highlight Saturdays and Sundays with a background
 */
public class Deadline implements DayViewDecorator {

    Date[] deadlines = new Date[] {
            CalendarDay.from(2015, 6, 2).getDate(),
            CalendarDay.from(2015, 6, 8).getDate(),
            CalendarDay.from(2015, 6, 24).getDate(),
            CalendarDay.from(2015, 6, 25).getDate(),
            CalendarDay.from(2015, 6, 28).getDate(),
    };
    Set<Date> dateSet = new HashSet<>(Arrays.asList(deadlines));

    private final Calendar calendar = Calendar.getInstance();
    private final Drawable highlightDrawable;
    private static int width;
    private static int length;

    public Deadline(int width, int length) {
        this.width = width;
        this.length = length;
        highlightDrawable = generateBackgroundDrawable();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
//        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
//        return weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY;
        return dateSet.contains(day.getDate());
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(highlightDrawable);
    }

    private static Drawable generateBackgroundDrawable() {
        final int color = Color.parseColor("#F44336");
        final int selectedColor = Color.parseColor("#ff33b5e5");
        final int unselectedColor = Color.parseColor("#FFFFFF");

        CurvedTriangle selected = new CurvedTriangle(width, length, color, selectedColor);
        CurvedTriangle unselected = new CurvedTriangle(width, length, color, unselectedColor);

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] {android.R.attr.state_checked, android.R.attr.state_enabled}, selected);
        drawable.addState(new int[] {android.R.attr.state_pressed, android.R.attr.state_selected}, selected);
        drawable.addState(new int[] {-android.R.attr.state_enabled}, unselected);

        return drawable;
    }
}
