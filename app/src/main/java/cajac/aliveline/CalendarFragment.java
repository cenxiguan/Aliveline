package cajac.aliveline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cajac.aliveline.decorators.DayOutOfMonth;
import cajac.aliveline.decorators.Deadline;
import cajac.aliveline.decorators.OneDayDecorator;

/**
 * Created by Chungyuk Takahashi on 6/5/2015.
 */
public class CalendarFragment extends Fragment {
    private View rootView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    protected FragmentActivity mActivity;

    private int screenWidth;
    private int screenHeight;

    private Fragment currentCal;
    private CalendarMonthFragment calMonthFrag;
    private CalendarDayFragment calDayFrag;
    private TodoListFragment todoListFrag;
    private FrameLayout calendarFrame;
    private FrameLayout listFrame;
    private Date selectedDate;
    final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
    private static final Date today = CalendarDay.today().getDate();
    private Switch dayOrMonth;
    private Button goToToday;

    private DatabaseHelper dbh;
    private RecyclerView todosRecyclerV;
    private RecyclerView.Adapter recAdapter;
    private List<Todo> recTodos;

    private int shortTransition = 300;
    private int normalTransition = 600;
    private int longTransition = 900;

    boolean something = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        calMonthFrag = new CalendarMonthFragment();
        calDayFrag = new CalendarDayFragment();
        todoListFrag = new TodoListFragment();
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.calendar_frame, calDayFrag, "DAY");
        fragmentTransaction.add(R.id.calendar_frame, calMonthFrag, "MONTH");
        fragmentTransaction.add(R.id.list_frame, todoListFrag, "LIST");
        fragmentTransaction.commit();

        calendarFrame = (FrameLayout) rootView.findViewById(R.id.calendar_frame);
        listFrame = (FrameLayout) rootView.findViewById(R.id.list_frame);
        calendarFrame.getLayoutParams().height = (int) (screenHeight * 0.43);
        calendarFrame.requestLayout();
        listFrame.getLayoutParams().height = (int) (screenHeight * 0.57);
        listFrame.requestLayout();

        selectedDate = CalendarDay.today().getDate();
        dayOrMonth = (Switch) rootView.findViewById(R.id.day_month_switch);
        dayOrMonth.setChecked(false);
        dayOrMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    switchFrame(false);
                else
                    switchFrame(true);
            }
        });

        goToToday = (Button) rootView.findViewById(R.id.today);
        goToToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCal == null)
                    switchFrame(true);
                else
                    ((CalendarResetter) currentCal).resetToToday();
            }
        });

        return rootView;
    }

    public void switchFrame(boolean switchToMonth) {
        CalendarMonthFragment cmf = (CalendarMonthFragment) fragmentManager.findFragmentByTag("MONTH");
        CalendarDayFragment cdf = (CalendarDayFragment) fragmentManager.findFragmentByTag("DAY");

        if (switchToMonth) {
            currentCal = calMonthFrag;
            Log.w("switchFrame", selectedDate.toString());
            ((CalendarMonthFragment) currentCal).setSelectedDate(selectedDate);
            calDayFrag.fadeOut();
            rootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    calMonthFrag.fadeIn();
                }
            }, shortTransition);
            animateSwitch(cdf, true);
        } else {

            currentCal = calDayFrag;
            calDayFrag.setSelectedDate(selectedDate);

            calMonthFrag.fadeOut();

            rootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    calDayFrag.fadeIn();
                }
            }, shortTransition);
            animateSwitch(cmf, false);
        }

    }

    private void animateSwitch(Fragment fragment, boolean switchToMonth) {
        Animation calendarAnim;
        Animation listAnim;
        if (switchToMonth) {
            calendarAnim = new ResizeAnimation(calendarFrame, (int) (screenHeight * 0.43));
            listAnim = new ResizeAnimation(listFrame, (int) (screenHeight * 0.57));
        } else {
            calendarAnim = new ResizeAnimation(calendarFrame, (int) (screenHeight * 0.23));
            listAnim = new ResizeAnimation(listFrame, (int) (screenHeight * 0.75));
        }
        calendarAnim.setDuration(normalTransition);
        calendarAnim.setFillAfter(true);
        listAnim.setDuration(normalTransition);
        listAnim.setFillAfter(true);
        calendarFrame.startAnimation(calendarAnim);
        listFrame.startAnimation(listAnim);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
        screenWidth = mActivity.getResources().getDisplayMetrics().widthPixels;
        screenHeight = mActivity.getResources().getDisplayMetrics().heightPixels;
    }

    public Fragment getCurrentCal() {
        return currentCal;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    private class CalendarMonthFragment extends Fragment implements OnMonthChangedListener, CalendarResetter {
        private View childView;
        private MaterialCalendarView calendarView;
        private TextView textView;
        private final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
        private final int REQUEST_DATE = 0;

        private Calendar cal = Calendar.getInstance();
        private OneDayDecorator oneDayDecorator = new OneDayDecorator();
        private DayOutOfMonth dayOutOfMonth = new DayOutOfMonth();

        public CalendarMonthFragment() {}

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            childView = inflater.inflate(R.layout.fragment_calendar_month, container, false);

            calendarView = (MaterialCalendarView) childView.findViewById(R.id.calendarView);
            calendarView.setOnDateChangedListener(new OnDateClickListener());
            calendarView.setOnMonthChangedListener(this);
            calendarView.clearSelection();
            calendarView.setSelectedDate(selectedDate);
            calendarView.setCurrentDate(selectedDate);

            calendarView.setSelectionColor(getActivity().getResources().getColor(R.color.selected));
            calendarView.setTitleOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    CalendarDatePicker cdp = new CalendarDatePicker(selectedDate);
                    cdp.setTargetFragment(CalendarMonthFragment.this, REQUEST_DATE);
                    cdp.show(ft, "CalendarDatePicker");
                }
            });
            int tileWidth = screenWidth / (28/3);
            int tileLength = (int)(screenHeight * (5.0/105));
            calendarView.setTileSize(tileWidth, tileLength);
            calendarView.removeDecorator(dayOutOfMonth);
            calendarView.addDecorators(
                    dayOutOfMonth,
                    oneDayDecorator,
                    new Deadline(getActivity(), tileWidth, tileLength)
            );
            oneDayDecorator.setDate(selectedDate);
            cal.setTime(selectedDate);
            dayOutOfMonth.setMonth(cal.get(Calendar.MONTH));
            calendarView.invalidateDecorators();

            return childView;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // Make sure fragment codes match up
            if (requestCode == CalendarDatePicker.RESULT_DATE) {
                Date date = (Date) data.getSerializableExtra(CalendarDatePicker.DATE);
                selectedDate = date;
                calendarView.setSelectedDate(selectedDate);
                oneDayDecorator.setDate(selectedDate);
                calendarView.invalidateDecorators();
            }
        }

        @Override
        public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
            dayOutOfMonth.setMonth(date.getMonth());
            widget.removeDecorator(oneDayDecorator);
            widget.addDecorator(oneDayDecorator);
            widget.invalidateDecorators();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            // TODO Auto-generated method stub
            super.onSaveInstanceState(outState);
        }

        private class OnDateClickListener implements OnDateChangedListener {
            @Override
            public void onDateChanged(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date) {
                if(date == null) {
                    textView.setText(null);
                }
                else {
                    Date convertDate = date.getDate();
                    selectedDate = convertDate;
                    oneDayDecorator.setDate(convertDate);
                    widget.invalidateDecorators();
                    todoListFrag.fadeOut(150);
                    rootView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            todoListFrag.updateRecyclerAdapter(selectedDate);
                            todoListFrag.fadeIn(150);
                        }
                    }, 150);
                }
            }

            public void sameDate() {
                switchFrame(false);
                dayOrMonth.setChecked(true);
            }
        }

        public MaterialCalendarView getCalendarView() { return calendarView; }

        public void setSelectedDate(Date date) {
            calendarView.setSelectedDate(date);
            oneDayDecorator.setDate(date);
            calendarView.invalidateDecorators();
        }

        public void resetToToday() {
            selectedDate = today;
            cal.setTime(selectedDate);
            oneDayDecorator.setDate(selectedDate);
            calendarView.invalidateDecorators();
            todoListFrag.fadeOut(150);
            rootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    todoListFrag.updateRecyclerAdapter(selectedDate);
                    todoListFrag.fadeIn(150);
                }
            }, 150);
            calendarView.setSelectedDate(selectedDate);
        }

        public void fadeIn() {
            fadeIn(shortTransition);
        }

        public void fadeIn(int milliseconds) {
            childView.animate().alpha(1f).setDuration(milliseconds).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    childView.setVisibility(View.VISIBLE);
                }
            });
        }

        public void fadeOut() {
            fadeOut(shortTransition);
        }

        public void fadeOut(int milliseconds) {
            childView.animate().alpha(0f).setDuration(milliseconds).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    childView.setVisibility(View.GONE);
                }
            });
        }

    }

    protected class CalendarDayFragment extends Fragment implements  CalendarResetter {
        private View dayView;
        private RecyclerView recyclerView;
        private RecyclerAdapter mAdapter;

        private static final String LOG_TAG = "CalendarDay";
        private final int dayWidth = (int) (screenWidth / 3.5);
        private final int dayCenter = screenWidth / 2 - dayWidth / 2;
        private final int listSize = 61;

        private final String[] DAYS_OF_WEEK = new String[]{"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        private final String[] MONTHS_OF_YEAR = new String[]{"January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October", "November", "December"};
        private Calendar past = Calendar.getInstance();
        private Calendar future = Calendar.getInstance();
        private int head;
        private int tail;
        private int todayPosition = Integer.MAX_VALUE / 2 + (listSize/2 + 2);
        private int selectedPosition;
        private View oldView;

        public CalendarDayFragment() {
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            dayView = inflater.inflate(R.layout.fragment_calendar_day, container, false);
            recyclerView = (RecyclerView) dayView.findViewById(R.id.hListView);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
            llm.offsetChildrenHorizontal(dayCenter);


            selectedPosition = todayPosition;
            recyclerView.setLayoutManager(llm);
            setSelectedDate(selectedDate);

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Date date = mAdapter.getItem(position);
                                    if (selectedDate.equals(date)) {
                                        switchFrame(true);
                                        dayOrMonth.setChecked(false);
                                        return;
                                    }
                                    selectedDate = date;
                                    oldView.findViewById(R.id.date_info).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hlv_item_unselected, null));
                                    todoListFrag.fadeOut(150);
                                    rootView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            todoListFrag.updateRecyclerAdapter(selectedDate);
                                            todoListFrag.fadeIn(150);
                                        }
                                    }, 150);
                                    setTextColors(oldView, getResources().getColor(R.color.primary_text));
                                    oldView = view;
                                    setTextColors(oldView, getResources().getColor(R.color.secondary_text));
                                    view.findViewById(R.id.date_info).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hlv_item_selected, null));
                                    selectedPosition = position;
                                    scrollToCenter(view);
                                }
                            })
            );
            recyclerView.addOnScrollListener(new EndlessScrollListener((LinearLayoutManager) recyclerView.getLayoutManager(), listSize) {
                @Override
                public void onLoadMore(boolean direction) {
                    if (direction) addFuture();
                    else addPast();
                }
            });
            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View p_v, MotionEvent p_event) {
                    // this will disallow the touch request for parent scroll on touch of child rootView
                    p_v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            return dayView;
        }

        private void addPast() {
            for (int i = 0; i < 7; i++) {
                past.add(Calendar.DAY_OF_YEAR, -1);
                future.add(Calendar.DAY_OF_YEAR, -1);
                mAdapter.addPast(past.getTime());
            }
            mAdapter.notifyDataSetChanged();
        }

        private void addFuture() {
            for (int i = 0; i < 7; i++) {
                past.add(Calendar.DAY_OF_YEAR, 1);
                future.add(Calendar.DAY_OF_YEAR, 1);
                mAdapter.addFuture(future.getTime());
            }
            mAdapter.notifyDataSetChanged();
        }

        private class ScrollRunnable implements Runnable {
            private int dX;

            public ScrollRunnable(int dX) {
                this.dX = dX;
            }

            public void run() {
                recyclerView.smoothScrollBy(dX - dayCenter, 0);
            }
        }

        public void resetToToday() {
            fadeOut(shortTransition);
            dayView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setSelectedDate(CalendarDay.today().getDate());
                    todoListFrag.fadeOut(150);
                    rootView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            todoListFrag.updateRecyclerAdapter(selectedDate);
                            todoListFrag.fadeIn(150);
                        }
                    }, 150);
                    fadeIn(shortTransition);
                }
            }, shortTransition);

        }

        public void setSelectedDate(Date date) {
            selectedDate = date;
            past.setTime(selectedDate);
            past.add(Calendar.DAY_OF_YEAR, -listSize / 2);
            future.setTime(selectedDate);
            future.add(Calendar.DAY_OF_YEAR, listSize / 2);

            List<Date> items = new ArrayList<>();

            for (int i = 1; i <= listSize; i++) {
                items.add(past.getTime());
                past.add(Calendar.DAY_OF_YEAR, 1);
            }
            head = 0;
            tail = items.size() - 1;
            past.setTime(selectedDate);
            past.add(Calendar.DAY_OF_YEAR, -listSize / 2);

            mAdapter = new RecyclerAdapter(mActivity, R.layout.hlv_item, items);
            recyclerView.setAdapter(mAdapter);
            selectedPosition = todayPosition;
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(selectedPosition, dayCenter);
        }

        protected void setTextColors(View dayItem, int color) {
            TextView itemMonth = (TextView) dayItem.findViewById(R.id.item_month);
            TextView dayOfMonth = (TextView) dayItem.findViewById(R.id.day_of_month);
            TextView year = (TextView) dayItem.findViewById(R.id.year);
            itemMonth.setTextColor(color);
            dayOfMonth.setTextColor(color);
            year.setTextColor(color);
        }

        public void scrollToCenter(View view) {
            int dX = (int) view.getX();
            recyclerView.post(new ScrollRunnable(dX));
        }

        public void fadeIn() {
            fadeIn(shortTransition);
        }

        public void fadeIn(int milliseconds) {
            dayView.animate().alpha(1f).setDuration(milliseconds).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    dayView.setVisibility(View.VISIBLE);
                }
            });
        }

        public void fadeOut() {
            fadeOut(shortTransition);
        }

        public void fadeOut(int milliseconds) {
            dayView.animate().alpha(0f).setDuration(milliseconds).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dayView.setVisibility(View.GONE);
                }
            });
        }

        protected class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
            private List<Date> mItems;
            private LayoutInflater mInflater;
            private Context mContext;
            private int mResource;

            public RecyclerAdapter(Context context, int resourceId, List<Date> objects) {
                mInflater = LayoutInflater.from(context);
                mContext = context;
                mResource = resourceId;
                mItems = objects;
            }

            @Override
            public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = mInflater.inflate(mResource, parent, false);

                return new RecyclerViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
                if (position == selectedPosition) {
                    oldView = viewHolder.getItemView();
                    viewHolder.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hlv_item_selected, null));
                    setTextColors(viewHolder.getItemView(), getResources().getColor(R.color.secondary_text));
                } else {
                    viewHolder.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hlv_item_unselected, null));
                    setTextColors(viewHolder.getItemView(), getResources().getColor(R.color.primary_text));
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(getItem(position));

                viewHolder.setDayOfWeek(DAYS_OF_WEEK[cal.get(Calendar.DAY_OF_WEEK) - 1]);
                viewHolder.setItemMonth(MONTHS_OF_YEAR[cal.get(Calendar.MONTH)]);
                viewHolder.setDayOfMonth(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                viewHolder.setYear(String.valueOf(cal.get(Calendar.YEAR)));


            }

            @Override
            public int getItemCount() {
                return Integer.MAX_VALUE;
            }

            public Date getItem(int position) {
                if (mItems.size() == 0 || position == 1) {
                    return null;
                }
                return mItems.get(position % mItems.size());
            }

            public void addFuture(Date date) {
                mItems.set(head, date);
                int size = mItems.size();
                head = (head + 1) % size;
                tail = (tail + 1) % size;
            }

            public void addPast(Date date) {
                mItems.set(tail, date);
                int size = mItems.size();
                head = (head + (size - 1)) % size;
                tail = (tail + (size - 1)) % size;
            }

            public class RecyclerViewHolder extends RecyclerView.ViewHolder {
                private View itemView;

                private TextView dayOfWeek;
                private TextView itemMonth;
                private TextView dayOfMonth;
                private TextView year;

                public RecyclerViewHolder(View itemView) {
                    super(itemView);
                    this.itemView = itemView;
                    dayOfWeek = (TextView) itemView.findViewById(R.id.day_of_week);
                    itemMonth = (TextView) itemView.findViewById(R.id.item_month);
                    dayOfMonth = (TextView) itemView.findViewById(R.id.day_of_month);
                    year = (TextView) itemView.findViewById(R.id.year);
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.width = dayWidth;
                    params.height = (int) (screenHeight * 0.20);
                }

                public void setBackgroundDrawable(Drawable drawable) {
                    itemView.findViewById(R.id.date_info).setBackgroundDrawable(drawable);
                }

                public View getItemView() {
                    return itemView;
                }

                public void setDayOfWeek(String dayOfWeek) {
                    this.dayOfWeek.setText(dayOfWeek);
                }

                public void setItemMonth(String itemMonth) {
                    this.itemMonth.setText(itemMonth);
                }

                public void setDayOfMonth(String dayOfMonth) {
                    this.dayOfMonth.setText(dayOfMonth);
                }

                public void setYear(String year) {
                    this.year.setText(year);
                }
            }

        }
    }
}
