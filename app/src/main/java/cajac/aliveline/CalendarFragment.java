package cajac.aliveline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.decorators.Deadline;
import com.prolificinteractive.materialcalendarview.decorators.OneDayDecorator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

/**
 * Created by Chungyuk Takahashi on 6/5/2015.
 */
public class CalendarFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;
    protected FragmentActivity mActivity;

    private int screenWidth;
    private int screenHeight;

    private Fragment currentCal;
    private CalendarMonthFragment calMonthFrag;
    private CalendarDayFragment calDayFrag;
    private Date selectedDate;
    final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
    private static final Date today = CalendarDay.today().getDate();
    private Switch dayOrMonth;
    private Button goToToday;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calendar, container, false);

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
        selectedDate = CalendarDay.today().getDate();

        if (currentCal == null)
            switchFrame(new CalendarMonthFragment());
        // Needed in case the fragment disappears
        if(currentCal instanceof CalendarDayFragment) {
            switchFrame(new CalendarDayFragment());
        }else {
            switchFrame(new CalendarMonthFragment());
        }

        dayOrMonth = (Switch) view.findViewById(R.id.day_month_switch);
        dayOrMonth.setChecked(false);
        dayOrMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.w("Switch", selectedDate.toString());
                if (isChecked) {
//                    calMonthFrag = (CalendarMonthFragment) currentCal;
//                    switchFrame(calDayFrag);
                    switchFrame(new CalendarDayFragment());
                } else {
//                    calDayFrag = (CalendarDayFragment) currentCal;
//                    calMonthFrag.getCalendarView().setSelectedDate(selectedDate);
//                    Log.w("Switch", calMonthFrag.getCalendarView().getSelectedDate().toString());
                    switchFrame(new CalendarMonthFragment());
//                    switchFrame(calMonthFrag);
                }
            }
        });

        goToToday = (Button) view.findViewById(R.id.today);
        goToToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCal == null)
                    switchFrame(new CalendarMonthFragment());
                else
                    ((CalendarResetter) currentCal).resetToToday();
            }
        });

        return view;
    }

    public void switchFrame(Fragment fragment) {
        currentCal = fragment;
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.calendar_frame, fragment).commit();
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
        private View view;
        private MaterialCalendarView calendarView;
        private TextView textView;
        private final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
        private final int REQUEST_DATE = 0;
        private OneDayDecorator oneDayDecorator = new OneDayDecorator();

        public CalendarMonthFragment() {}

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_calendar_month, container, false);

            textView = (TextView) view.findViewById(R.id.textview);
            calendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
            calendarView.setOnDateChangedListener(new OnDateClickListener());
            calendarView.setOnMonthChangedListener(this);
            calendarView.clearSelection();
            calendarView.setSelectedDate(selectedDate);
            calendarView.setCurrentDate(selectedDate);
            Log.w("OnCreateView", calendarView.getSelectedDate().toString());

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
            int tileLength = (int)(screenHeight * (5.0/108));
            calendarView.setTileSize(tileWidth, tileLength);
//            int tileLength = (screenHeight / 2) * (5 / 6) / 9;

//            Button save = (Button) view.findViewById(R.id.save);
//            save.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                EditText input = (EditText) view.findViewById(R.id.edit);
////                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
////                SharedPreferences.Editor edit = sharedPref.edit();
////
////                edit.putString(formattedSelectDate, input.getText().toString());
////                edit.commit();
//                }
//            });

            calendarView.addDecorators(
                    oneDayDecorator,
                    new Deadline(tileWidth, tileLength)
            );
            oneDayDecorator.setDate(selectedDate);
            calendarView.invalidateDecorators();

            return view;
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
//            Toast.makeText(getActivity(), FORMATTER.format(date.getDate()), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            // TODO Auto-generated method stub
            super.onSaveInstanceState(outState);

//            if (calendarView != null) {
//                calendarView.onSaveInstanceState();
//            }

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
                }
            }

            public void sameDate() {
                dayOrMonth.setChecked(true);
                CalendarDayFragment dayView = new CalendarDayFragment();
                switchFrame(dayView);
            }
        }

        public MaterialCalendarView getCalendarView() { return calendarView; }

        public void resetToToday() {
            selectedDate = today;
            oneDayDecorator.setDate(selectedDate);
            calendarView.invalidateDecorators();
            calendarView.setSelectedDate(selectedDate);
        }

    }

    private class CalendarDayFragment extends Fragment implements AdapterView.OnItemClickListener, CalendarResetter {
        private View dayView;
        private HListView listView;
        private InfiniteAdapter mAdapter;

        private static final String LOG_TAG = "CalendarDay";
        private final int dayWidth = screenWidth / 4;
        private final int day_center = screenWidth / 2 - dayWidth / 2
                - (int) (mActivity.getResources().getDimension(R.dimen.normal_hlv_divider));

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

            List<Date> items = new ArrayList<>();

            for(int i = 1; i <= 61; i++) {
                items.add(past.getTime());
                past.add(Calendar.DAY_OF_YEAR, 1);
            }
            head = 0;
            tail = items.size() - 1;
            past.setTime(selectedDate);
            past.add(Calendar.DAY_OF_YEAR, -30);

            mAdapter = new InfiniteAdapter( mActivity, R.layout.test_item_1, android.R.id.text1, items );
            listView.setHeaderDividersEnabled(true);
            listView.setFooterDividersEnabled(true);
            listView.setOnItemClickListener(this);
            listView.setAdapter(mAdapter);

            selectedPosition = todayPosition;
            listView.setSelectionFromLeft(selectedPosition, day_center); //Zooms and aligns center
            oldView = listView.getAdapter().getView(selectedPosition, null, container);

            listView.setOnScrollListener(new EndlessScrollListener(items.size()) {
                @Override
                public void onLoadMore(boolean direction) {
                    if (direction)
                        addFuture();
                    else
                        addPast();
                }

                public void onScrollStateChanged(AbsHListView View, int scrollState) {
                }
            });

            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View p_v, MotionEvent p_event) {
                    // this will disallow the touch request for parent scroll on touch of child view
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

        // Append more data into the adapter
        public void customLoadMoreDataFromApi(int offset) {
            // This method probably sends out a network request and appends new data items to your adapter.
            // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
            // Deserialize API response and then construct new objects to append to the adapter
        }

        private void scrollList() {
            listView.smoothScrollBy(1500, 300);
        }

        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
            Date date = mAdapter.getItem(position);
            if (selectedDate.equals(date)) {
                dayOrMonth.setChecked(false);
                CalendarMonthFragment monthView = new CalendarMonthFragment();
                switchFrame(monthView);
                return;
            }
            selectedDate = date;
            oldView.setBackgroundColor(getResources().getColor(R.color.day_item));
            oldView = view;
            view.setBackgroundColor(getResources().getColor(R.color.selected));

//            TextView txt = (TextView) dayView.findViewById(R.id.txt);
//            txt.setText(formatter.format(date));
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
                listView.smoothScrollToPositionFromLeft(position, day_center, milliseconds);
            }
        }

        public View getOldView() { return oldView; }

        public void resetToToday() {
            selectedDate = CalendarDay.today().getDate();
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

            mAdapter = new InfiniteAdapter( mActivity, R.layout.test_item_1, android.R.id.text1, items );
            listView.setAdapter(mAdapter);
            oldView = listView.getAdapter().getView(selectedPosition, null, container);
            listView.setSelectionFromLeft(selectedPosition, day_center); //Zooms and aligns center
        }

        private class InfiniteAdapter extends ArrayAdapter<Date> {

            private List<Date> mItems;
            private LayoutInflater mInflater;
            private Context mContext;
            private int mResource;
            private int mTextResId;
            ViewGroup parent;

            Animation animation;

            public InfiniteAdapter( Context context, int resourceId, int textViewResourceId, List<Date> objects ) {
                super( context, resourceId, textViewResourceId, objects );
                mInflater = LayoutInflater.from( context );
                mContext = context;
                animation = AnimationUtils.loadAnimation(mContext, R.anim.push_up_in);
                mResource = resourceId;
                mTextResId = textViewResourceId;
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
//                Log.w("getView", "Testing");
                this.parent = parent;
//                position%=mItems.size();

                if( null == convertView ) {
                    convertView = mInflater.inflate( mResource, parent, false );
                }

                if ( position == selectedPosition) {
                    oldView = convertView;
                    convertView.setBackgroundColor(getResources().getColor(R.color.selected));
                } else
                    convertView.setBackgroundColor(getResources().getColor(R.color.day_item));


                TextView textView = (TextView) convertView.findViewById( mTextResId );
                textView.setText( formatter.format(getItem( position%mItems.size() )) );

                ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.width = dayWidth;

//                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_left_in);
//
//                animation.setDuration(300);
                convertView.startAnimation(animation);

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
