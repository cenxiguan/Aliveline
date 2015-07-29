package cajac.aliveline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
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
import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

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

//        Calendar cal = Calendar.getInstance();
//        cal.clear(Calendar.HOUR_OF_DAY);
//        cal.clear(Calendar.HOUR);
//        cal.clear(Calendar.AM_PM);
//        cal.clear(Calendar.MINUTE);
//        cal.clear(Calendar.SECOND);
//        cal.clear(Calendar.MILLISECOND);
//        selectedDate = cal.getTime();
        calMonthFrag = new CalendarMonthFragment();
        calDayFrag = new CalendarDayFragment();
        todoListFrag = new TodoListFragment();
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.calendar_frame, calDayFrag, "DAY");
//        fragmentTransaction.hide(calDayFrag);
//        calDayFrag.hide();
        fragmentTransaction.add(R.id.calendar_frame, calMonthFrag, "MONTH");
        fragmentTransaction.add(R.id.list_frame, todoListFrag, "LIST");
        fragmentTransaction.commit();

        calendarFrame = (FrameLayout) rootView.findViewById(R.id.calendar_frame);
        listFrame = (FrameLayout) rootView.findViewById(R.id.list_frame);
        calendarFrame.getLayoutParams().height = (int) (screenHeight * 0.4);
        calendarFrame.requestLayout();
        listFrame.getLayoutParams().height = (int) (screenHeight * 0.6);
        listFrame.requestLayout();

//        if (currentCal == null)
//            switchFrame(new CalendarMonthFragment());

        selectedDate = CalendarDay.today().getDate();
        dayOrMonth = (Switch) rootView.findViewById(R.id.day_month_switch);
        dayOrMonth.setChecked(false);
        dayOrMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.w("Switch", selectedDate.toString());
                if (isChecked) {
//                    calMonthFrag = (CalendarMonthFragment) currentCal;
                    switchFrame(false);
//                    switchFrame(new CalendarDayFragment());
                } else {
//                    calDayFrag = (CalendarDayFragment) currentCal;
                    Log.w("Switch", calMonthFrag.getCalendarView().getSelectedDate().toString());
                    switchFrame(true);
//                    switchFrame(new CalendarMonthFragment());
                }
            }
        });

        goToToday = (Button) rootView.findViewById(R.id.today);
        goToToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCal == null)
//                    switchFrame(new CalendarMonthFragment());
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
            // Make method for animating in and out in the month and day fragments

            calDayFrag.fadeOut();
            rootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    calMonthFrag.fadeIn();
                }
            }, shortTransition);
            switchFragments(cdf, cmf, true);
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

            switchFragments(cmf, cdf, false);
        }

//        currentCal = fragment;
//        fragmentTransaction.replace(R.id.calendar_frame, fragment).commit();

    }

    private void switchFragments(Fragment frag1, Fragment frag2, boolean switchToMonth) {
//        if (frag1 != null) {
//            fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.hide(frag1);
//            fragmentTransaction.commit();
//        }
        // Animation
        animateSwitch(frag1, switchToMonth);
        final Fragment fragTwo = frag2;
//        if (frag2 != null) {
//            fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.show(frag2);
//            fragmentTransaction.commit();
//        }
//        rootView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (fragTwo != null) {
//                    fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.show(fragTwo);
//                    fragmentTransaction.commit();
//                }
//            }
//        }, 800);

    }

    private void animateSwitch(Fragment fragment, boolean switchToMonth) {
        Animation calendarAnim;
        Animation listAnim;
//        v.getLayoutParams().height = 50;
//        v.getLayoutParams().width = 200;
//        v.requestLayout();
//        Log.w("animate", "Helloo");
        if (switchToMonth) {
            calendarAnim = new ResizeAnimation(calendarFrame, (int) (screenHeight * 0.4));
            listAnim = new ResizeAnimation(listFrame, (int) (screenHeight * 0.6));
        } else {
            calendarAnim = new ResizeAnimation(calendarFrame, (int) (screenHeight * 0.25));
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

    private void createRecyclerView(View view){
        dbh = new DatabaseHelper(getActivity());
        //String selectedDateStr = dbh.dateToStringFormat(selectedDate);
        recTodos = dbh.getAllToDosByDay(selectedDate);
        todosRecyclerV = (RecyclerView) view.findViewById(R.id.toDoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        todosRecyclerV.setLayoutManager(layoutManager);
        recAdapter = new CardAdapter(recTodos);
        todosRecyclerV.setAdapter(recAdapter);
        dbh.close();
    }

    public void updateRecyclerAdapter(){
        //String selectedDateStr = dbh.dateToStringFormat(selectedDate);
        recTodos.clear();
        recAdapter.notifyDataSetChanged();
        recTodos.addAll(dbh.getAllToDosByDay(selectedDate));
        recAdapter.notifyItemRangeChanged(0, recTodos.size());
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

            textView = (TextView) childView.findViewById(R.id.textview);
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
                    CalendarDatePicker cdp = new CalendarDatePicker();
                    cdp.setTargetFragment(CalendarMonthFragment.this, REQUEST_DATE);
                    cdp.show(ft, "CalendarDatePicker");
                }
            });
            int tileWidth = screenWidth / (28/3);
            int tileLength = (int)(screenHeight * (5.0/105));
            calendarView.setTileSize(tileWidth, tileLength);

//            DatabaseHelper dbh = new DatabaseHelper(getActivity());
//            List<Todo> test = dbh.getAllToDos();
//            String dates = "";
//            for(Todo td : test) {
//                dates += td.getTitle() + ": " + td.getDueDateString() + "\n";
//            }
//            TextView textView = (TextView) rootView.findViewById(R.id.textview);
//            textView.setText(dates);
//            int tileLength = (screenHeight / 2) * (5 / 6) / 9;

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

//            createRecyclerView(rootView);

            return childView;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // Make sure fragment codes match up
            if (requestCode == CalendarDatePicker.RESULT_DATE) {
                Date date = (Date) data.getSerializableExtra(CalendarDatePicker.DATE);
                selectedDate = date;
                calendarView.setSelectedDate(selectedDate);
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
                    todoListFrag.updateRecyclerAdapter(selectedDate);
//                    updateRecyclerAdapter();
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
            todoListFrag.updateRecyclerAdapter(selectedDate);
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

    private class CalendarDayFragment extends Fragment implements AdapterView.OnItemClickListener, CalendarResetter {
        private View dayView;
        private HListView listView;
        private InfiniteAdapter mAdapter;

        private static final String LOG_TAG = "CalendarDay";
        private final int dayWidth = (int) (screenWidth / 3.5);
        private final int dayCenter = screenWidth / 2 - dayWidth / 2;

        private final String[] DAYS_OF_WEEK = new String[] {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        private final String[] MONTHS_OF_YEAR = new String[] {"January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October", "November", "December" };
        private Calendar past = Calendar.getInstance();
        private Calendar future = Calendar.getInstance();
        private int head;
        private int tail;
        private int todayPosition = Integer.MAX_VALUE / 2 + 32;
        private int selectedPosition;
        private View oldView;
        private ViewGroup container;

        public CalendarDayFragment() {}

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            this.container = container;
            dayView = inflater.inflate(R.layout.fragment_calendar_day, container, false);
            listView = (HListView) dayView.findViewById( R.id.hListView1 );
            past.setTime(selectedDate);
            past.add(Calendar.DAY_OF_YEAR, -30);
            future.setTime(selectedDate);
            future.add(Calendar.DAY_OF_YEAR, 30);

            List<Date> items = new ArrayList<Date>();

            for(int i = 1; i <= 61; i++) {
                items.add(past.getTime());
                past.add(Calendar.DAY_OF_YEAR, 1);
            }
            head = 0;
            tail = items.size() - 1;
            past.setTime(selectedDate);
            past.add(Calendar.DAY_OF_YEAR, -30);

            mAdapter = new InfiniteAdapter( mActivity, R.layout.hlv_item, items );
            listView.setHeaderDividersEnabled(true);
            listView.setFooterDividersEnabled(true);
            listView.setOnItemClickListener(this);
            listView.setAdapter(mAdapter);

            selectedPosition = todayPosition;
            listView.setSelectionFromLeft(selectedPosition, dayCenter); //Zooms and aligns center
            oldView = listView.getAdapter().getView(selectedPosition, null, container);

            listView.setOnScrollListener(new EndlessScrollListener(items.size()) {
                @Override
                public void onLoadMore(boolean direction) {
                    if (direction)  addFuture();
                    else            addPast();
                }

                public void onScrollStateChanged(AbsHListView View, int scrollState) {
                }
            });

            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View p_v, MotionEvent p_event) {
                    // this will disallow the touch request for parent scroll on touch of child rootView
                    p_v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

//            createRecyclerView(dayView);
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

        // Append more data into the adapter
        public void customLoadMoreDataFromApi(int offset) {
            // This method probably sends out a network request and appends new data items to your adapter.
            // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
            // Deserialize API response and then construct new objects to append to the adapter
        }

        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
            Date date = mAdapter.getItem(position);
            if (selectedDate.equals(date)) {
                CalendarMonthFragment monthView = new CalendarMonthFragment();
//                switchFrame(monthView);
                switchFrame(true);
                dayOrMonth.setChecked(false);
                return;
            }
            selectedDate = date;
            oldView.findViewById(R.id.date_info).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.hlv_item_unselected, null));
            todoListFrag.updateRecyclerAdapter(selectedDate);
//            updateRecyclerAdapter();
            setTextColors(oldView, getResources().getColor(R.color.primary_text));
            oldView = view;
            setTextColors(oldView, getResources().getColor(R.color.secondary_text));
            view.findViewById(R.id.date_info).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.hlv_item_selected, null));
            selectedPosition = position;
            listView.post(new ScrollRunnable(position));
        }

        private class ScrollRunnable implements Runnable{
            private int position;
            private int milliseconds;

            public ScrollRunnable(int position) {
                this.position = position;
                this.milliseconds = 750;
            }

            public ScrollRunnable(int position, int milliseconds) {
                this.position = position;
                this.milliseconds = milliseconds;
            }

            public void run() {
                listView.smoothScrollToPositionFromLeft(position, dayCenter, milliseconds);
            }
        }

        public View getOldView() { return oldView; }

        public void resetToToday() {
            fadeOut(shortTransition);
            dayView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setSelectedDate(CalendarDay.today().getDate());
                    fadeIn(shortTransition);
                    Log.w("resetToToday", "GOOD");
                }
            }, shortTransition);
//            int fadeInDuration = 500; // Configure time values here
//            int timeBetween = 300;
//            int fadeOutDuration = 300;
//            Animation fadeIn = new AlphaAnimation(0, 1);
//            fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
//            fadeIn.setDuration(fadeInDuration);
//
//            Animation fadeOut = new AlphaAnimation(1, 0);
//            fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
//            fadeOut.setStartOffset(fadeInDuration + timeBetween);
//            fadeOut.setDuration(fadeOutDuration);
//
//            AnimationSet animation = new AnimationSet(false); // change to false
//            animation.addAnimation(fadeIn);
////            animation.addAnimation(fadeOut);
//            animation.setRepeatCount(1);
//            dayView.setAnimation(animation);
//            dayView.startAnimation(animation);
//            setSelectedDate(CalendarDay.today().getDate());

        }

        public void setSelectedDate(Date date) {
            selectedDate = date;
            past.setTime(selectedDate);
            past.add(Calendar.DAY_OF_YEAR, -30);
            future.setTime(selectedDate);
            future.add(Calendar.DAY_OF_YEAR, 30);

            List<Date> items = new ArrayList<>();

            for(int i = 1; i <= 61; i++) {
                items.add(past.getTime());
                past.add(Calendar.DAY_OF_YEAR, 1);
            }
            head = 0;
            tail = items.size() - 1;
            past.setTime(selectedDate);
            past.add(Calendar.DAY_OF_YEAR, -30);

            mAdapter = new InfiniteAdapter( mActivity, R.layout.hlv_item, items );
            listView.setAdapter(mAdapter);
            selectedPosition = todayPosition;
            todoListFrag.updateRecyclerAdapter(selectedDate);
//            updateRecyclerAdapter();
            setTextColors(oldView, getResources().getColor(R.color.primary_text));
            oldView = listView.getAdapter().getView(selectedPosition, null, container);
            setTextColors(oldView, getResources().getColor(R.color.secondary_text));

            listView.setSelectionFromLeft(selectedPosition, dayCenter); //Zooms and aligns center
        }

        protected void setTextColors(View dayItem, int color) {
            TextView itemMonth = (TextView) dayItem.findViewById(R.id.item_month);
            TextView dayOfMonth = (TextView) dayItem.findViewById(R.id.day_of_month);
            TextView year = (TextView) dayItem.findViewById(R.id.year);
            itemMonth.setTextColor(color);
            dayOfMonth.setTextColor(color);
            year.setTextColor(color);
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

        private class InfiniteAdapter extends ArrayAdapter<Date> {

            private List<Date> mItems;
            private LayoutInflater mInflater;
            private Context mContext;
            private int mResource;
            private Animation animation;

            public InfiniteAdapter( Context context, int resourceId, List<Date> objects ) {
                super( context, resourceId, objects );
                mInflater = LayoutInflater.from( context );
                mContext = context;
                animation = AnimationUtils.loadAnimation(mContext, R.anim.push_up_in);
                mResource = resourceId;
                mItems = objects;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public long getItemId( int position ) {
                return getItem( position ).hashCode();
            }

            @Override
            public View getView( int position, View convertView, ViewGroup parent ) {
                if( null == convertView ) {
                    convertView = mInflater.inflate( mResource, parent, false );
                }

                if ( position == selectedPosition) {
                    oldView = convertView;
                    convertView.findViewById(R.id.date_info).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hlv_item_selected, null));
                } else {
                    convertView.findViewById(R.id.date_info).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hlv_item_unselected, null));
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(getItem(position % mItems.size()));

                TextView dayOfWeek = (TextView) convertView.findViewById(R.id.day_of_week);
                dayOfWeek.setText(DAYS_OF_WEEK[cal.get(Calendar.DAY_OF_WEEK) - 1]);

                TextView itemMonth = (TextView) convertView.findViewById(R.id.item_month);
                itemMonth.setText( MONTHS_OF_YEAR[cal.get(Calendar.MONTH)] );

                TextView dayOfMonth = (TextView) convertView.findViewById(R.id.day_of_month);
                dayOfMonth.setText( String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) );

                TextView year = (TextView) convertView.findViewById(R.id.year);
                year.setText( String.valueOf(cal.get(Calendar.YEAR)) );

                ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.width = dayWidth;
                params.height = (int) (screenHeight * 0.22);

                if ( position == selectedPosition) {
                    setTextColors(convertView, getResources().getColor(R.color.secondary_text));
                } else {
                    setTextColors(convertView, getResources().getColor(R.color.primary_text));
                }
//                convertView.findViewById(R.id.date_info).startAnimation(animation);

                return convertView;
            }

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Date getItem(int position) {
                if ( mItems.size()==0 ) {
                    return null;
                }
                return mItems.get( position%mItems.size() );
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
        }

    }



}
