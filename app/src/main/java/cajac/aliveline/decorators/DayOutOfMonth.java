package cajac.aliveline.decorators;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

/**
 * Created by Chungyuk Takahashi on 7/17/2015.
 */
public class DayOutOfMonth implements DayViewDecorator{
    private int month;

    public DayOutOfMonth() {
        month = CalendarDay.today().getMonth();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.getMonth() != this.month;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#727272")));
        view.addSpan(new RelativeSizeSpan(0.8f));
    }

    /**
     * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
     */
    public void setMonth(int month) {
        this.month = month;
    }
}
